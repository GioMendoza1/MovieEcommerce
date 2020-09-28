package edu.uci.ics.gamendo1.service.api_gateway.resources;

import edu.uci.ics.gamendo1.service.api_gateway.GatewayService;
import edu.uci.ics.gamendo1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.gamendo1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.api_gateway.models.IDM.*;
import edu.uci.ics.gamendo1.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.gamendo1.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.gamendo1.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.gamendo1.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;


@Path("idm")
public class IDMEndpoints {
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NoContentResponseModel registerUserRequest(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to register user.");
        RegisterRequestModel requestModel;

        try {
            requestModel = (RegisterRequestModel) ModelValidator.verifyModel(jsonText, RegisterRequestModel.class);
        } catch (ModelValidationException e) {
            e.printStackTrace();
            return null;
        }

        ServiceLogger.LOGGER.info(requestModel.getEmail());
        String str = new String(requestModel.getPassword());
        ServiceLogger.LOGGER.info(str);
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        //    Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
        //    not every endpoint requires the same information. The register user request in particular does not contain
        //    any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
        //    doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
        //    this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
        //    the endpoint we're sending this request to, and the transactionID for this request.

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserRegister());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return responseModel;
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NoContentResponseModel loginUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to login the user.");
        LoginRequestModel requestModel;

        try {
            requestModel = (LoginRequestModel) ModelValidator.verifyModel(jsonText, LoginRequestModel.class);
        } catch (ModelValidationException e) {
            e.printStackTrace();
            return null;
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserLogin());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return responseModel;
    }

    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySessionRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to verify session of a user.");
        SessionRequestModel requestModel;


        try {
            requestModel = (SessionRequestModel) ModelValidator.verifyModel(jsonText, SessionRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, SessionRequestModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPSessionVerify());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).entity(responseModel).build();
    }

    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUserPrivilegeRequest(String jsonText) {

        ServiceLogger.LOGGER.info("Received request to verify session of a user.");
        PrivilegeRequestModel requestModel;


        try {
            requestModel = (PrivilegeRequestModel) ModelValidator.verifyModel(jsonText, PrivilegeRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, PrivilegeRequestModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserPrivilegeVerify());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).entity(responseModel).build();
    }
}
