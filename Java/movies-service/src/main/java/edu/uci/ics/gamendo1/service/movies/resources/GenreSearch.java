package edu.uci.ics.gamendo1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.movies.core.Movies;
import edu.uci.ics.gamendo1.service.movies.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.movies.models.GenreAddRequestModel;
import edu.uci.ics.gamendo1.service.movies.models.GenreSearchResponseModel;
import edu.uci.ics.gamendo1.service.movies.models.MovieResponseModel;
import edu.uci.ics.gamendo1.service.movies.models.PrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("genre")
public class GenreSearch {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response genreSearch(@Context HttpHeaders headers)
    {
        GenreSearchResponseModel responseModel = new GenreSearchResponseModel();

        if(Movies.searchGenreDB(responseModel))
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

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenre(@Context HttpHeaders headers, String jsonText)
    {
        String email = headers.getHeaderString("email");
        ObjectMapper mapper = new ObjectMapper();
        GenreAddRequestModel requestModel;
        MovieResponseModel responseModel;
        try {
            if (!Movies.isUserAllowedToMakeRequest(email, 3)) {
                responseModel = new MovieResponseModel(141, "User has insufficient privilege.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            requestModel = mapper.readValue(jsonText, GenreAddRequestModel.class);
            responseModel = new MovieResponseModel();

            if (Movies.addGenreDB(requestModel, responseModel))
            {
                ServiceLogger.LOGGER.info("Success");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                ServiceLogger.LOGGER.info("Fail");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }catch(IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new MovieResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new MovieResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchGenreByMovie(@Context HttpHeaders headers, @PathParam("movieid") String id)
    {
        String email = headers.getHeaderString("email");
        if (!Movies.isUserAllowedToMakeRequest(email, 3)) {
            PrivilegeResponseModel response = new PrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(response).build();
        }

        GenreSearchResponseModel responseModel = new GenreSearchResponseModel();
        if (Movies.searchMovieGenresDB(id, responseModel))
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
