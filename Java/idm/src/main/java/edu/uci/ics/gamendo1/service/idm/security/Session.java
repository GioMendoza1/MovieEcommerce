package edu.uci.ics.gamendo1.service.idm.security;

import edu.uci.ics.gamendo1.service.idm.ServiceIdm;
import edu.uci.ics.gamendo1.service.idm.configs.Configs;
import edu.uci.ics.gamendo1.service.idm.logger.ServiceLogger;
import org.glassfish.hk2.utilities.RethrowErrorService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Session {
    public static final long SESSION_TIMEOUT = ServiceIdm.configs.getTimeout();
    public static final long TOKEN_EXPR = ServiceIdm.configs.getExpiration(); // 30-minute expiration
    public static final int ACTIVE = 1;
    public static final int CLOSED = 2;
    public static final int EXPIRED = 3;
    public static final int REVOKED = 4;
    public static final int RESETSESSION = 5;

    private final String email;
    private final Token sessionID;
    private final Timestamp timeCreated;
    private final Timestamp exprTime;
    private Timestamp lastUsed;

    /*
        This constructor is used to generate a new session. The only required information is the user's email address.
        This session will be forever associated with the user corresponding to the email address passed.
     */
    private Session(String email) {
        sessionID = Token.generateToken();
        this.email = email;
        this.timeCreated = new Timestamp(System.currentTimeMillis());
        this.lastUsed = timeCreated;
        this.exprTime = new Timestamp(System.currentTimeMillis() + TOKEN_EXPR);
    }

    /*
        This constructor is used when trying to rebuild an existing session for verification purposes. The email,
        sessionID (token) will come from the client in a request, the lastUsed time will be known at the time the
        request happens (current time), timeCreated and exprTime come from the sessions table in the database.
     */
    private Session(String email, Token sessionID, Timestamp timeCreated, Timestamp lastUsed, Timestamp exprTime) {
        this.email = email;
        this.sessionID = sessionID;
        this.timeCreated = timeCreated;
        this.lastUsed = timeCreated;
        this.exprTime = exprTime;
    }

    /*
        Use factory method to create a new session for increased security.
     */
    public static Session createSession(String name) {
        return new Session(name);
    }

    /*
        Rebuild an existing session object using the username and sessionID.
     */
    public static Session rebuildSession(String email, Token sessionID, Timestamp timeCreated, Timestamp lastUsed, Timestamp exprTime) {
        return new Session(email, sessionID, timeCreated, lastUsed, exprTime);
    }

    /*
        This updates the lastUsed data member to the current time.
     */
    public void update() {
        if (isDataValid() == 1) {
            lastUsed = new Timestamp(System.currentTimeMillis());
        }
    }

    public void updateDB(int status) {
        try {
            String query = "UPDATE sessions set email = ?, sessionID = ?, status = ?, timeCreated = ?, lastUsed = ?, exprTime = ? WHERE sessionID = ?;";

            PreparedStatement ps = ServiceIdm.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, sessionID.toString());
            ps.setInt(3, status);
            ps.setTimestamp(4, timeCreated);
            ps.setTimestamp(5, lastUsed);
            ps.setTimestamp(6, exprTime);
            ps.setString(7, sessionID.toString());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Success");
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("Query update failed.");
            e.printStackTrace();
        }
    }

    /*
        This method checks if the session has timed out. A timeout occurs if the user attempts to use a session more
        than SESSION_TIMEOUT time after the session was initially created.
     */
    public int isDataValid() {
        // Get the current time
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        // Verify that the current time is not greater than the hard expiration time of the session
        if (currentTime.after(exprTime)) {
            return EXPIRED;
        }
        // Verify that the current time is not AFTER the time of last use, causing a timeout.
        Timestamp timeoutTime = new Timestamp(lastUsed.getTime() + SESSION_TIMEOUT);
        if (currentTime.after(timeoutTime)) {
            return REVOKED;
        }

        Timestamp currentTimeTimeout = new Timestamp(currentTime.getTime() + SESSION_TIMEOUT);
        if (currentTimeTimeout.after(exprTime)){
            return RESETSESSION;
        }
        return ACTIVE;
    }

    @Override
    /*
        This method checks that two session objects are equal.
     */
    public boolean equals(Object o) {
        // Compare with self
        if (this == o) {
            return true;
        }

        // Compare with class type
        if (!(o instanceof Session)) {
            return false;
        }

        // Compare data members
        Session session = (Session) o;
        return getTimeCreated() == session.getTimeCreated() &&
                getLastUsed() == session.getLastUsed() &&
                getSessionID().equals(session.getSessionID()) &&
                getEmail().equals(session.getEmail());
    }

    public Token getSessionID() {
        return sessionID;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public Timestamp getLastUsed() {
        return lastUsed;
    }

    public Timestamp getExprTime() {
        return exprTime;
    }
}