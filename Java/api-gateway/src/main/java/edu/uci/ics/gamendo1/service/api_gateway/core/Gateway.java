package edu.uci.ics.gamendo1.service.api_gateway.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.api_gateway.GatewayService;
import edu.uci.ics.gamendo1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.api_gateway.models.IDM.SessionRequestModel;
import edu.uci.ics.gamendo1.service.api_gateway.models.IDM.SessionResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Gateway {
    public static String isSessionValid(String email, String sessionID, SessionResponseModel responseModel) {
        ServiceLogger.LOGGER.info("Verifying session with IDM...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String idm = GatewayService.getIdmConfigs().getIdmUri();

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = GatewayService.getIdmConfigs().getEPSessionVerify();
        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(idm).path(IDM_ENDPOINT_PATH);
        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Set the payload
        ServiceLogger.LOGGER.info("Setting payload of the request");
        SessionRequestModel requestModel = new SessionRequestModel(email, sessionID);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        if (response.getStatus() == 200) {
            ServiceLogger.LOGGER.info("Recieved Status 200");
            String jsonText = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            try {
                SessionResponseModel responseModelTemp = mapper.readValue(jsonText, SessionResponseModel.class);
                ServiceLogger.LOGGER.info(responseModel.getMessage() + responseModel.getResultCode());
                responseModel.setResultCode(responseModelTemp.getResultCode());
                responseModel.setMessage(responseModelTemp.getMessage());
                if (responseModel.getResultCode() == 130) {
                    return responseModelTemp.getSessionID();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return null;
    }
}
