package edu.uci.ics.gamendo1.service.api_gateway.resources;

import edu.uci.ics.gamendo1.service.api_gateway.GatewayService;
import edu.uci.ics.gamendo1.service.api_gateway.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("report")
public class GatewayEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String report(@Context HttpHeaders headers)
    {
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        ServiceLogger.LOGGER.info("Testing transactionID " + transactionID);
        if (transactionID == null) {
            return "nocontent";
        }

        Connection con = GatewayService.getConPool().requestCon();
        try {
            ServiceLogger.LOGGER.info("Attempting to retrieve from responses");
            String query = "SELECT response, httpstatus FROM responses where transactionid = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, transactionID);

            ResultSet rs = ps.executeQuery();
            Response.Status httpstatus = Response.Status.NO_CONTENT;
            String response = null;
            if (rs.next()) {
                httpstatus = Response.Status.fromStatusCode(rs.getInt("httpstatus"));
                response = rs.getString("response");
            }
            if (response != null) {
                ServiceLogger.LOGGER.info("Attempting to delete response from table");
                String query2 = "DELETE from responses WHERE transactionid = ?;";
                ps = con.prepareStatement(query2);
                ps.setString(1, transactionID);
                ps.execute();
                ServiceLogger.LOGGER.info("Deletion completed");
                return response;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "nocontent";


    }
//    public Response report(@Context HttpHeaders headers)
//    {
//        String email = headers.getHeaderString("email");
//        String sessionID = headers.getHeaderString("sessionID");
//        String transactionID = headers.getHeaderString("transactionID");
//
//        ServiceLogger.LOGGER.info("Testing transactionID " + transactionID);
//        if (transactionID == null) {
//            return Response.status(Response.Status.NO_CONTENT).build();
//        }
//
//        Connection con = GatewayService.getConPool().requestCon();
//        try {
//            ServiceLogger.LOGGER.info("Attempting to retrieve from responses");
//            String query = "SELECT response, httpstatus FROM responses where transactionid = ?;";
//            PreparedStatement ps = con.prepareStatement(query);
//            ps.setString(1, transactionID);
//
//            ResultSet rs = ps.executeQuery();
//            Response.Status httpstatus = Response.Status.NO_CONTENT;
//            String response = null;
//            if (rs.next()) {
//                httpstatus = Response.Status.fromStatusCode(rs.getInt("httpstatus"));
//                response = rs.getString("response");
//            }
//            if (response != null) {
//                ServiceLogger.LOGGER.info("Attempting to delete response from table");
//                String query2 = "DELETE from responses WHERE transactionid = ?;";
//                ps = con.prepareStatement(query2);
//                ps.setString(1, transactionID);
//                ps.execute();
//                ServiceLogger.LOGGER.info("Deletion completed");
//                return Response.ok().header("sessionID", sessionID).entity(response).build();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return Response.status(Response.Status.NO_CONTENT).build();
//
//
//    }
}
