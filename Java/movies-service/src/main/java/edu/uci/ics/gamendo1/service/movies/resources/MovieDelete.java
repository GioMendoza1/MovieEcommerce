package edu.uci.ics.gamendo1.service.movies.resources;

import edu.uci.ics.gamendo1.service.movies.core.Movies;
import edu.uci.ics.gamendo1.service.movies.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.movies.models.MovieResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("delete")
public class MovieDelete {
    @Path("{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieByID(@Context HttpHeaders headers, @PathParam("movieid") String id)
    {
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " + email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);
        ServiceLogger.LOGGER.info("TRANSACTIONID" + transactionID);
        MovieResponseModel responseModel;

        if (!Movies.isUserAllowedToMakeRequest(email, 3))
        {
            responseModel = new MovieResponseModel(141, "User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        responseModel = new MovieResponseModel();
        if(Movies.deleteMovieDB(id, responseModel))
        {
            ServiceLogger.LOGGER.info("Success");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        else
        {
            ServiceLogger.LOGGER.info("Fail");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
}
