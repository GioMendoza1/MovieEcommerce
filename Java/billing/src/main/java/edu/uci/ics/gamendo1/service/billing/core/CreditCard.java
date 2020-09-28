package edu.uci.ics.gamendo1.service.billing.core;

import edu.uci.ics.gamendo1.service.billing.BillingService;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;


public class CreditCard {

    public static boolean insertCCDB(CCModifyRequestModel requestModel, ShopModifyResponseModel responseModel) {
        try {
            String query = "INSERT INTO creditcards(id, firstName, lastName, expiration) VALUES (?, ?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to insert data");
            ps.setString(1, requestModel.getId());
            ps.setString(2, requestModel.getFirstName());
            ps.setString(3, requestModel.getLastName());
            ps.setDate(4,requestModel.getExpiration());
            ps.execute();
            ServiceLogger.LOGGER.info("Insertion success.");
            responseModel.setResultCode(3200);
            responseModel.setMessage("Credit card inserted successfully.");
            return true;
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                responseModel.setResultCode(325);
                responseModel.setMessage("Duplicate insertion.");
                return true;
            }
        }
        return false;
    }

    public static boolean updateCCDB(CCModifyRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try {
            String query = "UPDATE creditcards SET firstName = ?, lastName = ?, expiration = ? WHERE id = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to update data");
            ps.setString(1, requestModel.getFirstName());
            ps.setString(2, requestModel.getLastName());
            ps.setDate(3,requestModel.getExpiration());
            ps.setString(4, requestModel.getId());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int i = ps.executeUpdate();
            if (i > 0)
            {
                responseModel.setResultCode(3210);
                responseModel.setMessage("Credit card updated successfully.");
            }
            else
            {
                responseModel.setResultCode(324);
                responseModel.setMessage("Credit card does not exist.");
            }
            return true;
        } catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to update data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteCCDB(CCRequestModel requestModel, ShopModifyResponseModel responseModel)
    {
        try{
            String query = "DELETE FROM creditcards WHERE id = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to delete data");
            ps.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int i = ps.executeUpdate();

            if (i > 0)
            {
                responseModel.setResultCode(3220);
                responseModel.setMessage("Credit card deleted successfully.");
            }
            else {
                responseModel.setResultCode(324);
                responseModel.setMessage("Credit card does not exist.");
            }

            return true;

        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to delete data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean retrieveCCDB(CCRequestModel requestModel, CCRetrieveResponseModel responseModel)
    {
        try{
            String query = "SELECT * FROM creditcards where id = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Attempting to retrieve data");
            ps.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                responseModel.setResultCode(3230);
                responseModel.setMessage("Credit card retrieved successfully.");

                responseModel.setCreditcard(new CreditCardModel());

                responseModel.getCreditcard().setId(rs.getString("id"));
                responseModel.getCreditcard().setFirstName(rs.getString("firstName"));
                responseModel.getCreditcard().setLastName(rs.getString("lastName"));
                responseModel.getCreditcard().setExpiration(rs.getDate("expiration"));
            }
            else{
                responseModel.setResultCode(324);
                responseModel.setMessage("Credit card does not exist.");
            }

            return true;

        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Unable to retrieve data");
            e.printStackTrace();
        }
        return false;
    }
}
