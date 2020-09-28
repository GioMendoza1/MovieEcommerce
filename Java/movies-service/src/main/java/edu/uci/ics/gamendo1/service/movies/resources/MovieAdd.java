package edu.uci.ics.gamendo1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.movies.core.Movies;
import edu.uci.ics.gamendo1.service.movies.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.movies.models.MovieAddRequestModel;
import edu.uci.ics.gamendo1.service.movies.models.MovieAddResponseModel;
import edu.uci.ics.gamendo1.service.movies.models.MovieResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("add")
public class MovieAdd {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovie(@Context HttpHeaders headers, String jsonText)
    {
        String email = headers.getHeaderString("email");
        ObjectMapper mapper = new ObjectMapper();

        MovieAddRequestModel requestModel;
        MovieAddResponseModel responseModel;

        try {
            if (!Movies.isUserAllowedToMakeRequest(email, 3)) {
                MovieResponseModel response = new MovieResponseModel(141, "User has insufficient privilege.");
                return Response.status(Response.Status.OK).entity(response).build();
            }
            requestModel = mapper.readValue(jsonText, MovieAddRequestModel.class);
            responseModel = new MovieAddResponseModel();

            if(Movies.addMovieDB(requestModel, responseModel))
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
                MovieResponseModel response = new MovieResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                MovieResponseModel response = new MovieResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
