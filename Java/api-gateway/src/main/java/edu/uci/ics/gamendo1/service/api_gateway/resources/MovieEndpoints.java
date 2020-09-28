package edu.uci.ics.gamendo1.service.api_gateway.resources;

import edu.uci.ics.gamendo1.service.api_gateway.GatewayService;
import edu.uci.ics.gamendo1.service.api_gateway.core.Gateway;
import edu.uci.ics.gamendo1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.gamendo1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.api_gateway.models.IDM.SessionResponseModel;
import edu.uci.ics.gamendo1.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.gamendo1.service.api_gateway.models.billing.OrderRequestModel;
import edu.uci.ics.gamendo1.service.api_gateway.models.movie.*;
import edu.uci.ics.gamendo1.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.gamendo1.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.gamendo1.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.util.HashMap;

@Path("movies")
public class MovieEndpoints {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {

        ServiceLogger.LOGGER.info("Received request to search for movies.");

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

        HashMap<String, String> queryParams = new HashMap<>();

        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            String[] tmp = query.split("&");
            for (String s : tmp) {
                String[] c = s.split("=");
                if (c.length == 2) {
                    queryParams.put(c[0], c[1]);
                }
            }
        }


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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieSearch());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the httpType
        cr.setHttpMethodType(1);
        // set the params for get/delete methods
        cr.setParams(queryParams);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("get/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("movieid") String movieId) {
        ServiceLogger.LOGGER.info("Received request to get movie by ID.");

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

        HashMap<String, String> queryParams = new HashMap<>();

        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            String[] tmp = query.split("&");
            for (String s : tmp) {
                String[] c = s.split("=");
                if (c.length == 2) {
                    queryParams.put(c[0], c[1]);
                }
            }
        }


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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieGet() + "/" + movieId);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the httpType
        cr.setHttpMethodType(1);
        // set the params for get/delete methods
        cr.setParams(queryParams);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovieRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add movie.");
        MovieAddRequestModel requestModel;

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
            requestModel = (MovieAddRequestModel) ModelValidator.verifyModel(jsonText, MovieAddRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, MovieAddRequestModel.class);
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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieAdd());
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

    @Path("delete/{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("movieid") String movieId) {
        ServiceLogger.LOGGER.info("Received request to search for movies.");

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

        HashMap<String, String> queryParams = new HashMap<>();

        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            String[] tmp = query.split("&");
            for (String s : tmp) {
                String[] c = s.split("=");
                if (c.length == 2) {
                    queryParams.put(c[0], c[1]);
                }
            }
        }


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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieDelete() + "/" + movieId);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the httpType
        cr.setHttpMethodType(2);
        // set the params for get/delete methods
        cr.setParams(queryParams);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("genre")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        ServiceLogger.LOGGER.info("Received request to search for movies.");

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

        HashMap<String, String> queryParams = new HashMap<>();

        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            String[] tmp = query.split("&");
            for (String s : tmp) {
                String[] c = s.split("=");
                if (c.length == 2) {
                    queryParams.put(c[0], c[1]);
                }
            }
        }


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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreMovie());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the httpType
        cr.setHttpMethodType(1);
        // set the params for get/delete methods
        cr.setParams(queryParams);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("genre/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenreRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add genre.");
        GenreAddRequestModel requestModel;

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
            requestModel = (GenreAddRequestModel) ModelValidator.verifyModel(jsonText, GenreAddRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenreAddRequestModel.class);
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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreAdd());
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

    @Path("genre/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovieRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("movieid") String movieId) {
        ServiceLogger.LOGGER.info("Received request to search for movies.");

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

        HashMap<String, String> queryParams = new HashMap<>();

        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            String[] tmp = query.split("&");
            for (String s : tmp) {
                String[] c = s.split("=");
                if (c.length == 2) {
                    queryParams.put(c[0], c[1]);
                }
            }
        }


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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the httpType
        cr.setHttpMethodType(1);
        // set the params for get/delete methods
        cr.setParams(queryParams);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("star/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        ServiceLogger.LOGGER.info("Received request to search for movies.");

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

        HashMap<String, String> queryParams = new HashMap<>();

        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            String[] tmp = query.split("&");
            for (String s : tmp) {
                String[] c = s.split("=");
                if (c.length == 2) {
                    queryParams.put(c[0], c[1]);
                }
            }
        }


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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarSearch());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the httpType
        cr.setHttpMethodType(1);
        // set the params for get/delete methods
        cr.setParams(queryParams);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("star/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Received request to search for movies.");

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

        HashMap<String, String> queryParams = new HashMap<>();

        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            String[] tmp = query.split("&");
            for (String s : tmp) {
                String[] c = s.split("=");
                if (c.length == 2) {
                    queryParams.put(c[0], c[1]);
                }
            }
        }


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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarGet() + "/" + id);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the httpType
        cr.setHttpMethodType(1);
        // set the params for get/delete methods
        cr.setParams(queryParams);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
    }

    @Path("star/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add genre.");
        StarAddRequestModel requestModel;

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
            requestModel = (StarAddRequestModel) ModelValidator.verifyModel(jsonText, StarAddRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, StarAddRequestModel.class);
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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarAdd());
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

    @Path("star/starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovieRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add genre.");
        StarAddStarsinRequestModel requestModel;

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
            requestModel = (StarAddStarsinRequestModel) ModelValidator.verifyModel(jsonText, StarAddStarsinRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, StarAddStarsinRequestModel.class);
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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarIn());
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

    @Path("rating")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add genre.");
        UpdateRatingRequestModel requestModel;

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
            requestModel = (UpdateRatingRequestModel) ModelValidator.verifyModel(jsonText, UpdateRatingRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, UpdateRatingRequestModel.class);
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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPRating());
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
