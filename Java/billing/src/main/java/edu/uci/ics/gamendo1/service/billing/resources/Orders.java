package edu.uci.ics.gamendo1.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.uci.ics.gamendo1.service.billing.core.Order;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.*;
import sun.java2d.pipe.RegionSpanIterator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("order")
public class Orders {

    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response place(String jsonText){
        ObjectMapper mapper = new ObjectMapper();
        OrderRequestModel requestModel;
        OrderPlaceResponseModel responseModel;


        try{
            requestModel = mapper.readValue(jsonText, OrderRequestModel.class);
            responseModel = new OrderPlaceResponseModel();
            ServiceLogger.LOGGER.info("Attempting to place order.");
            if (Order.placeOrderDB(requestModel, responseModel))
            {
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        }catch(IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new OrderPlaceResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new OrderPlaceResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response complete(@QueryParam("paymentId") String paymentId, @QueryParam("token") String token, @QueryParam("PayerID") String PayerID){
        OrderCompleteRequestModel requestModel;
        ShopModifyResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"paymentId\":\"" + paymentId + "\"}";
        try {
            JsonNode jsonText = mapper.readTree(jsonString);
            ((ObjectNode) jsonText).put("token", token);
            ((ObjectNode) jsonText).put("PayerID", PayerID);
            requestModel = mapper.treeToValue(jsonText, OrderCompleteRequestModel.class);

            responseModel = new ShopModifyResponseModel();

            if (Order.completeOrderDB(requestModel, responseModel))
            {
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        }catch(IOException e){
            ServiceLogger.LOGGER.warning("IOException.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }



    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieve(String jsonText){

        ServiceLogger.LOGGER.info("Recieved request to retrieve from credit card DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        OrderRequestModel requestModel;
        OrderRetrieveResponseModel responseModel;

        try{
            requestModel = mapper.readValue(jsonText, OrderRequestModel.class);
            responseModel = new OrderRetrieveResponseModel();
            if (Order.retrieveOrderDB(requestModel, responseModel))
            {
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        }catch(IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new OrderRetrieveResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new OrderRetrieveResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
