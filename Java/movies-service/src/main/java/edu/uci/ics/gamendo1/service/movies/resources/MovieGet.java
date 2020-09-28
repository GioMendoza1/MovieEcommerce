package edu.uci.ics.gamendo1.service.movies.resources;

import edu.uci.ics.gamendo1.service.movies.core.Movies;
import edu.uci.ics.gamendo1.service.movies.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.movies.models.MovieSearchByIDResponseModel;
import edu.uci.ics.gamendo1.service.movies.models.PrivilegeResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("get")
public class MovieGet {
    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieByID(@Context HttpHeaders headers, @PathParam("movieid") String id) {
        ServiceLogger.LOGGER.info("Received request for movie search by ID.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " + email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);
        ServiceLogger.LOGGER.info("TRANSACTIONID" + transactionID);

        MovieSearchByIDResponseModel responseModel = new MovieSearchByIDResponseModel();
        boolean userAllowed = Movies.isUserAllowedToMakeRequest(email, 4);

        if (Movies.searchingMovieDBByID(id, userAllowed, responseModel))
        {
            ServiceLogger.LOGGER.info("Success");
            if (responseModel.getResultCode() == 141)
            {
                PrivilegeResponseModel response = new PrivilegeResponseModel(141, "User has insufficient privilege.");
                return Response.status(Response.Status.OK).entity(response).build();
            }
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        else
        {
            ServiceLogger.LOGGER.info("Fail");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }


}
