package edu.uci.ics.gamendo1.service.movies.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.uci.ics.gamendo1.service.movies.core.Movies;
import edu.uci.ics.gamendo1.service.movies.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.movies.models.MovieSearchByIDResponseModel;
import edu.uci.ics.gamendo1.service.movies.models.MovieSearchRequestModel;
import edu.uci.ics.gamendo1.service.movies.models.MovieSearchResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("search")
public class MovieSearch {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response movieSearch(@Context HttpHeaders headers, @QueryParam("title") String title, @QueryParam("genre") String genre, @QueryParam("year") Integer year,
                                @QueryParam("director") String director, @QueryParam("hidden") Boolean hidden, @QueryParam("offset") Integer offset,
                                @QueryParam("limit") Integer limit, @QueryParam("orderby") String orderby, @QueryParam("direction") String direction) {
        ServiceLogger.LOGGER.info("Received request for movie search.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);
        ServiceLogger.LOGGER.info("TRANSACTIONID" + transactionID);
        MovieSearchRequestModel requestModel;
        MovieSearchResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"title\":\"" + title + "\"}";
        try {
            JsonNode jsonText = mapper.readTree(jsonString);
            ((ObjectNode) jsonText).put("genre", genre);
            ((ObjectNode) jsonText).put("year", year);
            ((ObjectNode) jsonText).put("director", director);
            ((ObjectNode) jsonText).put("hidden", hidden);
            ((ObjectNode) jsonText).put("limit", limit);
            ((ObjectNode) jsonText).put("offset", offset);
            ((ObjectNode) jsonText).put("orderby", orderby);
            ((ObjectNode) jsonText).put("direction", direction);
            requestModel = mapper.treeToValue(jsonText, MovieSearchRequestModel.class);
            ServiceLogger.LOGGER.info("Mapping was a success: " + requestModel.toString());

            if (requestModel.getYear() == 0)
                requestModel.setYear(null);
            ServiceLogger.LOGGER.info("test1");

            if (requestModel.getLimit() == null || (requestModel.getLimit() != 10 && requestModel.getLimit() != 25 && requestModel.getLimit() != 50 && requestModel.getLimit() != 100))
                requestModel.setLimit(10);
            ServiceLogger.LOGGER.info("test2");
            if (requestModel.getOffset() == null || requestModel.getOffset() <= 0 || (requestModel.getOffset() % requestModel.getLimit()) != 0)
                requestModel.setOffset(0);
            ServiceLogger.LOGGER.info("test3");
            if (requestModel.getOrderby() == null || (!requestModel.getOrderby().equalsIgnoreCase("rating") && !requestModel.getOrderby().equalsIgnoreCase("title")) || requestModel.getDirection() == null || (!requestModel.getDirection().equalsIgnoreCase("asc") && !requestModel.getDirection().equalsIgnoreCase("desc"))) {
                requestModel.setOrderby("rating");
                requestModel.setDirection("desc, title asc");
            }
            else
            {
                if (requestModel.getOrderby().equalsIgnoreCase("rating"))
                {
                    requestModel.setDirection(requestModel.getDirection() + ", title asc");
                }
                else
                {
                    requestModel.setDirection(requestModel.getDirection() + ", rating desc");
                }
            }
            ServiceLogger.LOGGER.info("test4");
            if (Movies.isUserAllowedToMakeRequest(email, 3))
                requestModel.setIsHidden(true);
            else
                requestModel.setIsHidden(false);
            ServiceLogger.LOGGER.info("test5");
            responseModel = new MovieSearchResponseModel();
            if (Movies.searchingMovieDB(requestModel, responseModel))
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
            ServiceLogger.LOGGER.info("Error");
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}
