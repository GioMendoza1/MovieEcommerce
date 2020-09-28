package edu.uci.ics.gamendo1.service.api_gateway.resources;

import edu.uci.ics.gamendo1.service.api_gateway.GatewayService;
import edu.uci.ics.gamendo1.service.api_gateway.core.Gateway;
import edu.uci.ics.gamendo1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.gamendo1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.api_gateway.models.IDM.SessionResponseModel;
import edu.uci.ics.gamendo1.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.gamendo1.service.api_gateway.models.billing.*;
import edu.uci.ics.gamendo1.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.gamendo1.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.gamendo1.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("billing")
public class BillingEndpoints {
    @Path("cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCartRequest(@Context HttpHeaders headers, String jsonText) {

        ServiceLogger.LOGGER.info("Received request to insert into cart.");
        ShopModifyRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (ShopModifyRequestModel) ModelValidator.verifyModel(jsonText, ShopModifyRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShopModifyRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();


    }

    @Path("cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        ShopModifyRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (ShopModifyRequestModel) ModelValidator.verifyModel(jsonText, ShopModifyRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShopModifyRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();



    }

    @Path("cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete cart.");
        ShopDeleteRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (ShopDeleteRequestModel) ModelValidator.verifyModel(jsonText, ShopDeleteRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShopDeleteRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve from cart.");
        ShopRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;

        ServiceLogger.LOGGER.info("Retrieve cart request ResultCode: " + Integer.toString(sessionResponseModel.getResultCode()));
        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (ShopRequestModel) ModelValidator.verifyModel(jsonText, ShopRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShopRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to clear cart.");
        ShopRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;

        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (ShopRequestModel) ModelValidator.verifyModel(jsonText, ShopRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShopRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartClear());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("creditcard/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert credit card.");
        CCModifyRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;

        ServiceLogger.LOGGER.info("Testing new resultCode: " + Integer.toString(sessionResponseModel.getResultCode()));
        ServiceLogger.LOGGER.info("Testing new message: " + sessionResponseModel.getMessage());
        ServiceLogger.LOGGER.info("Testing new sessionID: " + sessionResponseModel.getSessionID());


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (CCModifyRequestModel) ModelValidator.verifyModel(jsonText, CCModifyRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CCModifyRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        cr.setEmail(email);

        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("creditcard/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update credit card.");
        CCModifyRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (CCModifyRequestModel) ModelValidator.verifyModel(jsonText, CCModifyRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CCModifyRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("creditcard/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete Credit Card.");
        CCRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (CCRequestModel) ModelValidator.verifyModel(jsonText, CCRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CCRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("creditcard/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve credit card.");
        CCRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (CCRequestModel) ModelValidator.verifyModel(jsonText, CCRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CCRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("customer/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        CustomerModifyRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerModifyRequestModel) ModelValidator.verifyModel(jsonText, CustomerModifyRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerModifyRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();

    }

    @Path("customer/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        CustomerModifyRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerModifyRequestModel) ModelValidator.verifyModel(jsonText, CustomerModifyRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerModifyRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("customer/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        CustomerRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerRequestModel) ModelValidator.verifyModel(jsonText, CustomerRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        OrderRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;



        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (OrderRequestModel) ModelValidator.verifyModel(jsonText, OrderRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, OrderRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderPlace());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        cr.setHttpMethodType(0);

        cr.setEmail(email);

        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        OrderRequestModel requestModel;

        String email = headers.getHeaderString("email");
        String sessionID =  headers.getHeaderString("sessionID");

        if (sessionID == null || sessionID.isEmpty()) {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-17);
            responseModel.setMessage("SessionID not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        if (email == null || email.isEmpty())
        {
            SessionResponseModel responseModel = new SessionResponseModel();
            responseModel.setResultCode(-16);
            responseModel.setMessage("Email not provided in request header.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        SessionResponseModel sessionResponseModel = new SessionResponseModel();
        sessionResponseModel.setResultCode(-1000);

        String potentialSession = Gateway.isSessionValid(email, sessionID, sessionResponseModel);

        if (potentialSession != null)
            sessionID = potentialSession;


        if (sessionResponseModel.getResultCode() == -1000)
            return Response.status(Status.OK).build();

        if (sessionResponseModel.getResultCode() == 131) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }
        if (sessionResponseModel.getResultCode() == 134) {
            return Response.status(Status.OK).entity(sessionResponseModel).build();
        }

        // Map jsonText to RequestModel
        try {
            requestModel = (OrderRequestModel) ModelValidator.verifyModel(jsonText, OrderRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, OrderRequestModel.class);
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
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        cr.setHttpMethodType(0);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }
}
