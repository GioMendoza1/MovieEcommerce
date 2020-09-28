package edu.uci.ics.gamendo1.service.billing.core;

import edu.uci.ics.gamendo1.service.billing.BillingService;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class Customer {
    public static boolean insertCustomerDB(CustomerModifyRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try {
            String query = "SELECT * FROM creditcards WHERE id = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getCcId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
            {
                responseModel.setResultCode(331);
                responseModel.setMessage("Credit card ID not found.");
                return true;
            }

            String query2 = "INSERT INTO customers(email, firstName, lastName, ccId, address) VALUES (?,?,?,?,?);";
            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
            ServiceLogger.LOGGER.info("Attempting to insert data");
            ps2.setString(1, requestModel.getEmail());
            ps2.setString(2, requestModel.getFirstName());
            ps2.setString(3, requestModel.getLastName());
            ps2.setString(4, requestModel.getCcId());
            ps2.setString(5, requestModel.getAddress());
            ps2.execute();
            ServiceLogger.LOGGER.info("Insertion success.");
            responseModel.setResultCode(3300);
            responseModel.setMessage("Customer inserted successfully.");
            return true;
        }catch(SQLException e){
            if (e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.info("Error found: " + e.getErrorCode());
                responseModel.setResultCode(333);
                responseModel.setMessage("Duplicate insertion.");
                return true;
            }
        }
        return false;
    }

    public static boolean updateCustomerDB(CustomerModifyRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try{
            String query2 = "SELECT * FROM creditcards WHERE id = ?";
            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
            ps2.setString(1, requestModel.getCcId());
            ResultSet rs = ps2.executeQuery();
            if (!rs.next())
            {
                responseModel.setResultCode(331);
                responseModel.setMessage("Credit card ID not found.");
                return true;
            }

            String query = "UPDATE customers SET email = ?, firstName = ?, lastName = ?, ccId = ?, address = ? WHERE ccId = ? AND email = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to update data");
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getFirstName());
            ps.setString(3, requestModel.getLastName());
            ps.setString(4, requestModel.getCcId());
            ps.setString(5, requestModel.getAddress());
            ps.setString(6, requestModel.getCcId());
            ps.setString(7, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int i = ps.executeUpdate();
            if (i > 0)
            {
                responseModel.setResultCode(3310);
                responseModel.setMessage("Customer updated successfully.");
            }
            else
            {
                responseModel.setResultCode(332);
                responseModel.setMessage("Customer does not exist.");
            }
            return true;
        }catch(SQLException e){
            if (e.getErrorCode() == 1452)
            {
                responseModel.setResultCode(331);
                responseModel.setMessage("Credit card ID not found.");
                return true;
            }
        }
        return false;
    }

    public static boolean retrieveCustomerDB(CustomerRequestModel requestModel, CustomerRetrieveResponseModel responseModel){
        try{
            String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to retrieve data");
            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                responseModel.setResultCode(3320);
                responseModel.setMessage("Customer retrieved successfully.");
                responseModel.setCustomer(new CustomerModel());
                responseModel.getCustomer().setEmail(rs.getString("email"));
                responseModel.getCustomer().setFirstName(rs.getString("firstName"));
                responseModel.getCustomer().setLastName(rs.getString("lastName"));
                responseModel.getCustomer().setCcId(rs.getString("ccId"));
                responseModel.getCustomer().setAddress(rs.getString("address"));
            }
            else
            {
                responseModel.setResultCode(332);
                responseModel.setMessage("Customer does not exist.");
            }
            return true;
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to retrieve data");
            e.printStackTrace();
        }
        return false;
    }
}
