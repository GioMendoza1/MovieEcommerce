package edu.uci.ics.gamendo1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.uci.ics.gamendo1.service.movies.core.Movies;
import edu.uci.ics.gamendo1.service.movies.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.movies.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("star")
public class StarSearch {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearch(@Context HttpHeaders headers, @QueryParam("name") String name, @QueryParam("birthYear") Integer birthYear, @QueryParam("movieTitle") String movieTitle, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("orderby") String orderby, @QueryParam("direction") String direction)
    {
        StarSearchRequestModel requestModel;
        StarSearchResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"name\":\"" + name + "\"}";
        try {
            JsonNode jsonText = mapper.readTree(jsonString);
            ((ObjectNode) jsonText).put("birthYear", birthYear);
            ((ObjectNode) jsonText).put("movieTitle", movieTitle);
            ((ObjectNode) jsonText).put("limit", limit);
            ((ObjectNode) jsonText).put("offset", offset);
            ((ObjectNode) jsonText).put("sortby", orderby);
            ((ObjectNode) jsonText).put("direction", direction);
            requestModel = mapper.treeToValue(jsonText, StarSearchRequestModel.class);
            ServiceLogger.LOGGER.info("Mapping was a success: " + requestModel.toString());

            if (requestModel.getLimit() == null || (requestModel.getLimit() != 10 && requestModel.getLimit() != 25 && requestModel.getLimit() != 50 && requestModel.getLimit() != 100))
                requestModel.setLimit(10);

            if (requestModel.getOffset() == null || requestModel.getOffset() <= 0 || requestModel.getOffset() % limit != 0)
                requestModel.setOffset(0);

            if (requestModel.getOrderby() == null || (!requestModel.getOrderby().equalsIgnoreCase("name") && !requestModel.getOrderby().equalsIgnoreCase("birthYear")) || (requestModel.getDirection() == null || (!requestModel.getDirection().equalsIgnoreCase("asc") && !requestModel.getDirection().equalsIgnoreCase("desc")))) {
                requestModel.setOrderby("name");
                requestModel.setDirection("asc, birthYear desc");
            }
            else
            {
                if (requestModel.getOrderby().equalsIgnoreCase("name"))
                    requestModel.setDirection(requestModel.getDirection() + ", birthYear desc");
                else
                    requestModel.setDirection(requestModel.getDirection() + ", name asc");
            }

            responseModel = new StarSearchResponseModel();
            if (Movies.searchingStarDB(requestModel, responseModel))
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

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchByID(@Context HttpHeaders headers, @PathParam("id") String id) {
        StarSearchByIDResponseModel responseModel = new StarSearchByIDResponseModel();
        if (Movies.searchingStarByIDDB(id, responseModel)) {
            ServiceLogger.LOGGER.info("Success");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        } else {
            ServiceLogger.LOGGER.info("Fail");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarsin(@Context HttpHeaders headers, String jsonText)
    {
        String email = headers.getHeaderString("email");
        ObjectMapper mapper = new ObjectMapper();
        StarAddStarsinRequestModel requestModel;
        MovieResponseModel responseModel;
        try {
            if (!Movies.isUserAllowedToMakeRequest(email, 3)) {
                responseModel = new MovieResponseModel(141, "User has insufficient privilege.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            requestModel = mapper.readValue(jsonText, StarAddStarsinRequestModel.class);
            responseModel = new MovieResponseModel();

            if (Movies.addStarsinDB(requestModel,responseModel))
            {
                ServiceLogger.LOGGER.info("Success");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            } else {
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

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStar(@Context HttpHeaders headers, String jsonText)
    {
        String email = headers.getHeaderString("email");
        ObjectMapper mapper = new ObjectMapper();

        StarAddRequestModel requestModel;
        MovieResponseModel responseModel;
        try{
            if (!Movies.isUserAllowedToMakeRequest(email, 3)) {
                responseModel = new MovieResponseModel(141, "User has insufficient privilege.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            requestModel = mapper.readValue(jsonText, StarAddRequestModel.class);
            responseModel = new MovieResponseModel();

            if (requestModel.getBirthYear() > 2019)
                requestModel.setBirthYear(null);

            if (Movies.addStarDB(requestModel, responseModel))
            {
                ServiceLogger.LOGGER.info("Success");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            } else {
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
}
