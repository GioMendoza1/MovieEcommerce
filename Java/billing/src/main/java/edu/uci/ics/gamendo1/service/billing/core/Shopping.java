package edu.uci.ics.gamendo1.service.billing.core;

import edu.uci.ics.gamendo1.service.billing.BillingService;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class Shopping {
    public static boolean insertShoppingDB(ShopModifyRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try {
            String query = "INSERT INTO carts(email, movieId, quantity) VALUES (?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to insert data");
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovieId());
            ps.setInt(3, requestModel.getQuantity());
            ps.execute();
            ServiceLogger.LOGGER.info("Insertion success.");
            responseModel.setResultCode(3100);
            responseModel.setMessage("Shopping cart item inserted successfully");
            return true;
        }catch(SQLException e) {
            e.printStackTrace();
            if (e instanceof SQLIntegrityConstraintViolationException){
                responseModel.setResultCode(311);
                responseModel.setMessage("Duplicate insertion.");
                return true;
            }
        }
        return false;
    }

    public static boolean updateShoppingDB(ShopModifyRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try{
            String query = "UPDATE carts SET quantity = ? WHERE email = ? AND movieId = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to update data");
            ps.setInt(1, requestModel.getQuantity());
            ps.setString(2, requestModel.getEmail());
            ps.setString(3, requestModel.getMovieId());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int i = ps.executeUpdate();
            if (i > 0)
            {
                responseModel.setResultCode(3110);
                responseModel.setMessage("Shopping cart item updated successfully.");
            }
            else
            {
                responseModel.setResultCode(312);
                responseModel.setMessage("Shopping item does not exist.");
            }
            return true;

        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to update data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteShoppingDB(ShopDeleteRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try{
            String query = "DELETE FROM carts WHERE email = ? AND movieId = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to delete data");
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovieId());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int i = ps.executeUpdate();

            if (i > 0)
            {
                responseModel.setResultCode(3120);
                responseModel.setMessage("Shopping cart item deleted successfully.");
            }
            else {
                responseModel.setResultCode(312);
                responseModel.setMessage("Shopping item does not exist.");
            }

            return true;

        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to delete data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean retrieveShoppingDB(ShopRequestModel requestModel, ShopRetrieveResponseModel responseModel){
        try {
            String query = "SELECT * FROM carts WHERE email = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE);
            ServiceLogger.LOGGER.info("Attempting to retrieve data");
            ps.setString(1, requestModel.getEmail());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                rs.last();
                int count = 0;
                int size = rs.getRow();
                rs.beforeFirst();
                responseModel.setItems(new ItemModel[size]);
                while (rs.next())
                {
                    responseModel.getItems()[count] = new ItemModel();
                    responseModel.getItems()[count].setEmail(rs.getString("email"));
                    responseModel.getItems()[count].setMovieId(rs.getString("movieId"));
                    responseModel.getItems()[count].setQuantity(rs.getInt("quantity"));
                    count++;
                }
                responseModel.setResultCode(3130);
                responseModel.setMessage("Shopping cart retrieved successfully.");
            }
            else{
                responseModel.setResultCode(312);
                responseModel.setMessage("Shopping item does not exist.");
            }
            return true;


        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to retrieve data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean clearShoppingDB(ShopRequestModel requestModel, ShopModifyResponseModel responseModel){
        try{
            String query = "DELETE FROM carts where email = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to delete cart");
            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();

            responseModel.setResultCode(3140);
            responseModel.setMessage("Shopping cart cleared successfully.");
            return true;

        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to clear cart");
            e.printStackTrace();
        }
        return false;
    }
}
