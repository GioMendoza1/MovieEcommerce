package edu.uci.ics.gamendo1.service.billing.core;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.gamendo1.service.billing.BillingService;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.*;
import edu.uci.ics.gamendo1.service.billing.resources.PayPalClient;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import com.paypal.base.rest.APIContext;

public class Order {

    public static boolean placeOrderDB(OrderRequestModel requestModel, OrderPlaceResponseModel responseModel)
    {
        try{
            String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
            {
                responseModel.setResultCode(332);
                responseModel.setMessage("Customer does not exist.");
                return true;
            }

            String query2 = "SELECT * FROM carts, movie_prices WHERE email = ? AND carts.movieId = movie_prices.movieId";
            ps = BillingService.getCon().prepareStatement(query2);
            ps.setString(1, requestModel.getEmail());
            rs = ps.executeQuery();
            if(!rs.next())
            {
                responseModel.setResultCode(341);
                responseModel.setMessage("Shopping cart for this customer not found.");
            }
            else{
                double total = 0;
                ArrayList<OrderItemsModel> currentCart = new ArrayList<OrderItemsModel>();
                OrderItemsModel currentItem = new OrderItemsModel();
                do{
                    currentItem.setSaleDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                    currentItem.setEmail(rs.getString("email"));
                    currentItem.setMovieId(rs.getString("movieId"));
                    ServiceLogger.LOGGER.warning(rs.getString("movieId"));
                    ServiceLogger.LOGGER.warning(currentItem.getMovieId());
                    currentItem.setQuantity(rs.getInt("quantity"));
                    total += ((rs.getFloat("unit_price") - rs.getFloat("discount")) * rs.getInt("quantity"));
                    currentCart.add(currentItem);
                    currentItem = new OrderItemsModel();

                }while(rs.next());
                //ServiceLogger.LOGGER.warning(currentCart.get(1).getMovieId());
                /*for (int i = 0; i < currentCart.size(); i++)
                {
                    ServiceLogger.LOGGER.warning(currentCart.get(i).getMovieId());
                }*/

                DecimalFormat df = new DecimalFormat("#.00");
                ServiceLogger.LOGGER.warning(df.format(total));
                PayPalClient payPalClient = new PayPalClient();
                Payment createdPayment = new Payment();
                Map<String, Object> payPalInfo = payPalClient.createPayment(df.format(total));
                ServiceLogger.LOGGER.warning("Test1");
                if (payPalInfo.get("boolean").toString().equals("false"))
                {
                    responseModel.setResultCode(342);
                    responseModel.setMessage("Create payment failed.");
                    return true;
                }
                RedirectUrls urls = new RedirectUrls();
                urls.setReturnUrl(payPalInfo.get("redirect_url").toString());
                createdPayment.setRedirectUrls(urls);
                int tokenIndex = payPalInfo.get("redirect_url").toString().lastIndexOf("token=") + 6;
                String token = payPalInfo.get("redirect_url").toString().substring(tokenIndex);

                CallableStatement cStmt = BillingService.getCon().prepareCall("{call insert_sales_transactions(?,?,?,?,?,?)}");
                for (int i = 0, size = currentCart.size(); i < size; i++)
                {
                    cStmt.setString(1, currentCart.get(i).getEmail());
                    cStmt.setString(2, currentCart.get(i).getMovieId());
                    cStmt.setInt(3, currentCart.get(i).getQuantity());
                    cStmt.setDate(4, currentCart.get(i).getSaleDate());
                    cStmt.setString(5, token);
                    cStmt.setString(6, null);
                    ServiceLogger.LOGGER.warning("Trying query: " + cStmt.toString());
                    cStmt.execute();
                }
                String query4 = "DELETE FROM carts WHERE email = ?";
                ps = BillingService.getCon().prepareStatement(query4);
                ps.setString(1, requestModel.getEmail());
                ps.execute();


                responseModel.setResultCode(3400);
                responseModel.setMessage("Order placed successfully.");
                responseModel.setRedirectURL(payPalInfo.get("redirect_url").toString());
                responseModel.setToken(token);
            }
            return true;

        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean retrieveOrderDB(OrderRequestModel requestModel, OrderRetrieveResponseModel responseModel)
    {
        String clientId = "Aas5uSd4Tm99LG17RHcQBnJ-BitNHadP4fJ7QQGuBmQMFTwTp1inzEbAq-r7xnofEazjLmyygZhjLN4O";
        String clientSecret = "EKZku1G4RkQ_gAf8JwLc9c0gaJo_Nc6HlyWNZslejNunZHyy7wi-g86t0mMMKIF12lM9HgeXlId9wp8-";
        try {

            String query = "SELECT * FROM sales, transactions, movie_prices WHERE email = ? AND sales.id = transactions.sId AND sales.movieId = movie_prices.movieId ORDER BY transactionId;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE);
            ServiceLogger.LOGGER.info("Attempting to retrieve data");
            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String currentTID = rs.getString("transactionId");
                APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
                Sale sale = Sale.get(apiContext, currentTID);
                ArrayList<TransactionModel> currentTransaction = new ArrayList<TransactionModel>();
                TransactionModel tempTransaction = new TransactionModel();
                ArrayList<OrderItemsModel> currentItems = new ArrayList<OrderItemsModel>();
                OrderItemsModel tempItems = new OrderItemsModel();
                OrderItemsModel[] tempOrder = new OrderItemsModel[1];
                TransactionModel[] tempTrans = new TransactionModel[1];

                tempTransaction.setTransactionId(currentTID);
                tempTransaction.setState(sale.getState());
                tempTransaction.setAmount(new AmountModel(sale.getAmount().getTotal(), sale.getAmount().getCurrency()));
                tempTransaction.setTransactionFee(new TransactionFeeModel(sale.getTransactionFee().getValue(), sale.getTransactionFee().getCurrency()));
                tempTransaction.setCreate_time(sale.getCreateTime());
                tempTransaction.setUpdate_time(sale.getUpdateTime());
                rs.beforeFirst();
                while (rs.next())
                {
                    if (!currentTID.equals(rs.getString("transactionId")))
                    {
                        tempTransaction.setItems(currentItems.toArray(tempOrder));
                        currentTransaction.add(tempTransaction);
                        tempTransaction = new TransactionModel();
                        currentItems = new ArrayList<>();
                        currentTID = rs.getString("transactionId");
                        sale = Sale.get(apiContext, currentTID);
                        tempTransaction.setTransactionId(currentTID);
                        tempTransaction.setState(sale.getState());
                        tempTransaction.setAmount(new AmountModel(sale.getAmount().getTotal(), sale.getAmount().getCurrency()));
                        tempTransaction.setTransactionFee(new TransactionFeeModel(sale.getTransactionFee().getValue(), sale.getTransactionFee().getCurrency()));
                        tempTransaction.setCreate_time(sale.getCreateTime());
                        tempTransaction.setUpdate_time(sale.getUpdateTime());
                    }
                    tempItems.setEmail(requestModel.getEmail());
                    tempItems.setMovieId(rs.getString("movieId"));
                    tempItems.setQuantity(rs.getInt("quantity"));
                    tempItems.setUnit_price(rs.getFloat("unit_price"));
                    tempItems.setDiscount(rs.getFloat("discount"));
                    tempItems.setSaleDate(rs.getDate("saleDate"));
                    currentItems.add(tempItems);
                    tempItems = new OrderItemsModel();
                }
                tempTransaction.setItems(currentItems.toArray(tempOrder));
                currentTransaction.add(tempTransaction);
                responseModel.setTransactions(currentTransaction.toArray(tempTrans));
                responseModel.setResultCode(3410);
                responseModel.setMessage("Orders retrieved successfully.");
            }
            else{
                responseModel.setResultCode(332);
                responseModel.setMessage("Customer does not exist.");
            }
            return true;
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to retrieve data");
            e.printStackTrace();
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean completeOrderDB(OrderCompleteRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try {
            String query = "SELECT * FROM transactions where token = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getToken());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                responseModel.setResultCode(3421);
                responseModel.setMessage("Token not found.");
                return true;
            }
            PayPalClient payPalClient = new PayPalClient();
            Map<String, Object> payPalInfo = payPalClient.completePayment(requestModel.getPaymentId(), requestModel.getPayerID());

            if (payPalInfo.get("status").toString().equalsIgnoreCase("failure"))
            {
                responseModel.setResultCode(3422);
                responseModel.setMessage("Payment can not be completed.");
                return true;
            }

            String query2 = "UPDATE transactions SET transactionId = ? WHERE token = ?";
            ps = BillingService.getCon().prepareStatement(query2);
            Payment createdPayment = (Payment) payPalInfo.get("payment");
            ps.setString(1,createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
            ps.setString(2, requestModel.getToken());
            ps.execute();

            responseModel.setResultCode(3420);
            responseModel.setMessage("Payment is completed successfully.");
            return true;


        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to retrieve data");
            e.printStackTrace();
        }
        return false;
    }
}
