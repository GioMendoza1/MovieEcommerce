package edu.uci.ics.gamendo1.service.api_gateway.threadpool;

import edu.uci.ics.gamendo1.service.api_gateway.GatewayService;
import edu.uci.ics.gamendo1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.api_gateway.models.RequestModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class Worker extends Thread {
    int id;
    ThreadPool threadPool;
    public static final int POST_REQUEST  = 0;
    public static final int GET_REQUEST = 1;
    public static final int DELETE_REQUEST = 2;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        Worker newWorker = new Worker(id, threadPool);
        return newWorker;
    }

    public void process() {
        ClientRequest clientRequest = threadPool.remove();
        if (clientRequest == null)
        {
            return;
        }
        ServiceLogger.LOGGER.info("Processing request # " + clientRequest.getEmail() + " at " + (System.currentTimeMillis() / 1000) % 60 + " seconds.");

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI
        ServiceLogger.LOGGER.info("Building URI...");
        //String IDM_URI = ExampleService.getExampleConfigs().getIdmConfigs().getIdmUri();
        String IDM_URI = clientRequest.getURI();

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        //String IDM_ENDPOINT_PATH = ExampleService.getExampleConfigs().getIdmConfigs().getPrivilegePath();
        String IDM_ENDPOINT_PATH = clientRequest.getEndpoint();

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH.split("\\{")[0]);

        HashMap<String, String> params = clientRequest.getParams();
        if (params != null) {
            for (Map.Entry<String, String> item : params.entrySet()) {
                String key = item.getKey();
                String value = item.getValue();
                webTarget = webTarget.queryParam(key, value);
            }
        }

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);


        Response response = null;
        ServiceLogger.LOGGER.info("Sending request...");
        invocationBuilder.header("email", clientRequest.getEmail()).header("sessionID", clientRequest.getSessionID()).header("transactionID", clientRequest.getTransactionID());
        if (clientRequest.getHttpMethodType() == POST_REQUEST)
        {
            ServiceLogger.LOGGER.info("Setting payload of the request...");
            RequestModel requestSending = clientRequest.getRequest();
            response = invocationBuilder.post(Entity.entity(requestSending, MediaType.APPLICATION_JSON));
        }
        else if (clientRequest.getHttpMethodType() == GET_REQUEST)
        {

            response = invocationBuilder.get();

        }
        else if (clientRequest.getHttpMethodType() == DELETE_REQUEST)
        {
            //invocationBuilder.header("email", clientRequest.getEmail()).header("sessionID", clientRequest.getSessionID()).header("transactionID", clientRequest.getTransactionID());
            response = invocationBuilder.delete();
        }

        Connection con = GatewayService.getConPool().requestCon();
        try {
            String query = "INSERT INTO responses(transactionid, email, sessionid, response, httpstatus) VALUES (?,?,?,?,?);";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, clientRequest.getTransactionID());
            ps.setString(2, clientRequest.getEmail());
            ps.setString(3, clientRequest.getSessionID());
            ps.setString(4, response.readEntity(String.class));
            ps.setInt(5, response.getStatus());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Success!");


        }catch(SQLException e){
            e.printStackTrace();
        }
        GatewayService.getConPool().releaseCon(con);
        ServiceLogger.LOGGER.info("Released connection.");

    }

    @Override
    public void run() {
        while (true) {
            process();
        }
    }
}
