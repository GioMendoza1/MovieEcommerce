package edu.uci.ics.gamendo1.service.idm.core;

import edu.uci.ics.gamendo1.service.idm.ServiceIdm;
import edu.uci.ics.gamendo1.service.idm.configs.Configs;
import edu.uci.ics.gamendo1.service.idm.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.idm.models.*;
import edu.uci.ics.gamendo1.service.idm.security.Crypto;
import edu.uci.ics.gamendo1.service.idm.security.Session;
import edu.uci.ics.gamendo1.service.idm.security.Token;
import org.apache.commons.codec.binary.Hex;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static edu.uci.ics.gamendo1.service.idm.security.Session.*;

public class Users {
    public static boolean registerUser(RegisterResponseModel responseModel, RegisterRequestModel requestModel, String salt, String hashedPass)
    {

        try {
            String query = "SELECT * FROM users WHERE email = ?;";
            PreparedStatement ps = ServiceIdm.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            ServiceLogger.LOGGER.info("Checking if email was returned");
            if (rs.next())
            {
                ServiceLogger.LOGGER.info("Email returned");
                responseModel.setResultCode(16);
                responseModel.setMessage("Email already in use.");
                return true;
            }
            ServiceLogger.LOGGER.info("Email not returned");
            ServiceLogger.LOGGER.info("Finding privilege level");
            String query2 = "SELECT plevel FROM privilege_levels WHERE pname = 'user';";
            ps = ServiceIdm.getCon().prepareStatement(query2);
            rs = ps.executeQuery();

            rs.next();
            int plevel = rs.getInt("plevel");
            ServiceLogger.LOGGER.info("Privelege level found");

            ServiceLogger.LOGGER.info("Finding user status");
            String query3 = "SELECT statusid FROM user_status WHERE status = 'active';";
            ps = ServiceIdm.getCon().prepareStatement(query3);
            rs = ps.executeQuery();

            rs.next();
            int statusid = rs.getInt("statusid");
            ServiceLogger.LOGGER.info("User status found");

            ServiceLogger.LOGGER.info("attempting to insert data");
            String query4 = "INSERT INTO users (email, status, plevel, salt, pword) VALUES (?, ?, ?, ?, ?);";
            ps = ServiceIdm.getCon().prepareStatement(query4);
            ps.setString(1, requestModel.getEmail());
            ps.setInt(2, statusid);
            ps.setInt(3, plevel);
            ps.setString(4, salt);
            ps.setString(5, hashedPass);
            ps.execute();

            ServiceLogger.LOGGER.info("Data successfully entered");

            responseModel.setResultCode(110);
            responseModel.setMessage("User registered successfully.");
            return true;






        }
        catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean loginUser(LoginRequestModel requestModel, LoginResponseModel responseModel)
    {
        try {

            String query = "SELECT * FROM users WHERE email = ?;";
            PreparedStatement ps = ServiceIdm.getCon().prepareStatement(query);


            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            ServiceLogger.LOGGER.info("Checking if user was found");
            String password;
            String salt;
            int status;
            if (rs.next()) {
                password = rs.getString("pword");
                salt = rs.getString("salt");
                status = rs.getInt("status");
            }
            else
            {
                ServiceLogger.LOGGER.info("User was not found.");
                responseModel.setResultCode(14);
                responseModel.setMessage("User not found.");
                return true;
            }

            byte[] originalSalt = Token.convert(salt);
            String hashedPass = Hex.encodeHexString(Crypto.hashPassword(requestModel.getPassword(), originalSalt, Crypto.ITERATIONS, Crypto.KEY_LENGTH));

            if (!password.equals(hashedPass))
            {
                responseModel.setResultCode(11);
                responseModel.setMessage("Passwords do not match.");
                return true;
            }

            String query2 = "SELECT * FROM sessions WHERE email = ?;";
            ps = ServiceIdm.getCon().prepareStatement(query2);

            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Checking if session exists");
            if (rs.next())
            {
                String query3 = "DELETE FROM sessions WHERE email = ?;";
                ps = ServiceIdm.getCon().prepareStatement(query3);

                ps.setString(1, requestModel.getEmail());
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ps.execute();
                ServiceLogger.LOGGER.info("Successfully removed previous session");
            }

            Session newSession = Session.createSession(requestModel.getEmail());
            ServiceLogger.LOGGER.info("attempting to insert data");
            String query4 = "INSERT INTO sessions (sessionID, email, status, timeCreated, lastUsed, exprTime) VALUES (?, ?, ?, ?, ?, ?);";
            ps = ServiceIdm.getCon().prepareStatement(query4);
            ps.setString(1, newSession.getSessionID().toString());
            ps.setString(2, newSession.getEmail());
            ps.setInt(3, status);
            ps.setTimestamp(4, newSession.getTimeCreated());
            ps.setTimestamp(5, newSession.getLastUsed());
            ps.setTimestamp(6, newSession.getExprTime());
            ps.execute();
            ServiceLogger.LOGGER.info("Data successfully entered");

            responseModel.setResultCode(120);
            responseModel.setMessage("User logged in successfully.");
            responseModel.setSessionID(newSession.getSessionID().toString());
            return true;
        }
        catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean determineSession(SessionRequestModel requestModel, SessionResponseModel responseModel){
        try{

            String query = "SELECT * FROM users WHERE email = ?;";
            PreparedStatement ps = ServiceIdm.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            ServiceLogger.LOGGER.info("Checking if user was found");
            if (!rs.next())
            {
                ServiceLogger.LOGGER.info("User was not found");
                responseModel.setResultCode(14);
                responseModel.setMessage("User not found.");
                return true;
            }
            ServiceLogger.LOGGER.info("User was found");

            String query2 = "SELECT * FROM sessions WHERE email = ? AND sessionID = ?;";
            ps = ServiceIdm.getCon().prepareStatement(query2);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getSessionID());
            rs = ps.executeQuery();
            Timestamp lastUsed;
            Timestamp exprTime;
            Timestamp timeCreated;
            int status;
            if (rs.next()){
                lastUsed = rs.getTimestamp("lastUsed");
                exprTime = rs.getTimestamp("exprTime");
                status = rs.getInt("status");
                timeCreated = rs.getTimestamp("timeCreated");
            }
            else{
                responseModel.setResultCode(134);
                responseModel.setMessage("Session not found.");
                return true;
            }

            Session newSessions = Session.rebuildSession(requestModel.getEmail(), Token.rebuildToken(requestModel.getSessionID()), timeCreated, lastUsed, exprTime);

            if (status == EXPIRED)
            {
                responseModel.setResultCode(131);
                responseModel.setMessage("Session is expired.");
                return true;
            }
            else if (status == CLOSED)
            {
                responseModel.setResultCode(132);
                responseModel.setMessage("Session is closed.");
                return true;
            }
            else if (status == REVOKED)
            {
                responseModel.setResultCode(133);
                responseModel.setMessage("Session is revoked.");
                return true;
            }

            ServiceLogger.LOGGER.info("Testing2");

            int newStatus = newSessions.isDataValid();


            if (newStatus == ACTIVE)
            {
                newSessions.update();
                newSessions.updateDB(newStatus);
                responseModel.setResultCode(130);
                responseModel.setMessage("Session is active.");
                responseModel.setSessionID(newSessions.getSessionID().toString());
                return true;
            }

            if (newStatus == EXPIRED)
            {
                newSessions.updateDB(newStatus);
                responseModel.setResultCode(131);
                responseModel.setMessage("Session is expired.");
                return true;
            }

            if (newStatus == REVOKED)
            {
                newSessions.update();
                newSessions.updateDB(newStatus);
                ServiceLogger.LOGGER.info("Session is revoked");
                responseModel.setResultCode(133);
                responseModel.setMessage("Session is revoked.");
                return true;
            }
            if (newStatus == RESETSESSION)
            {
                newSessions.update();
                newSessions.updateDB(REVOKED);

                Session newSession = Session.createSession(requestModel.getEmail());
                ServiceLogger.LOGGER.info("attempting to insert data");
                String query5 = "INSERT INTO sessions (sessionID, email, status, timeCreated, lastUsed, exprTime) VALUES (?, ?, ?, ?, ?, ?);";
                ps = ServiceIdm.getCon().prepareStatement(query5);
                ps.setString(1, newSession.getSessionID().toString());
                ps.setString(2, newSession.getEmail());
                ps.setInt(3, ACTIVE);
                ps.setTimestamp(4, newSession.getTimeCreated());
                ps.setTimestamp(5, newSession.getLastUsed());
                ps.setTimestamp(6, newSession.getExprTime());
                ps.execute();
                ServiceLogger.LOGGER.info("Data successfully entered");
                ServiceLogger.LOGGER.info("Session is active.");
                responseModel.setResultCode(130);
                responseModel.setMessage("Session is active.");
                responseModel.setSessionID(newSession.getSessionID().toString());
                return true;
            }
            ServiceLogger.LOGGER.info("Testing5");
        }
        catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        ServiceLogger.LOGGER.info("Testing6");
        return false;
    }

    public static boolean determinePrivilege(PrivilegeRequestModel requestModel, PrivilegeResponseModel responseModel)
    {
        try {
            String query = "SELECT * FROM users WHERE email = ?;";
            PreparedStatement ps = ServiceIdm.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            ServiceLogger.LOGGER.info("Checking if user was found");
            int plevel = 0;
            if (rs.next()) {
                ServiceLogger.LOGGER.info("User was found");
                plevel = rs.getInt("plevel");
            }
            else{
                ServiceLogger.LOGGER.info("User was not found");
                responseModel.setResultCode(14);
                responseModel.setMessage("User not found.");
                return true;
            }

            ServiceLogger.LOGGER.info("Checking plevels: " + Integer.toString(plevel) + " " + Integer.toString(requestModel.getPlevel()));

            if (plevel <= requestModel.getPlevel())
            {
                ServiceLogger.LOGGER.info("User has sufficient prrivege level.");
                responseModel.setResultCode(140);
                responseModel.setMessage("User has sufficient privilege level.");
                return true;
            }
            else
            {
                ServiceLogger.LOGGER.info("User has insufficient privilege level.");
                responseModel.setResultCode(141);
                responseModel.setMessage("User has insufficient privilege level.");
                return true;
            }

        }
        catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve/insert data");
            e.printStackTrace();
        }
        return false;
    }
}
