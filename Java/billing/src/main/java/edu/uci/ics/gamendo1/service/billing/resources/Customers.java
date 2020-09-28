package edu.uci.ics.gamendo1.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.billing.core.Customer;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.CustomerModifyRequestModel;
import edu.uci.ics.gamendo1.service.billing.models.CustomerRequestModel;
import edu.uci.ics.gamendo1.service.billing.models.CustomerRetrieveResponseModel;
import edu.uci.ics.gamendo1.service.billing.models.ShopModifyResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.regex.Pattern;

@Path("customer")
public class Customers {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(String jsonText)
    {
        ServiceLogger.LOGGER.info("Recieved request to insert into customer DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        CustomerModifyRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try{
            requestModel = mapper.readValue(jsonText, CustomerModifyRequestModel.class);
            if (requestModel.getCcId().length() > 19 || requestModel.getCcId().length() < 16)
            {
                responseModel = new ShopModifyResponseModel(321, "Credit card ID has invalid length.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if (!Pattern.matches("^[0-9]{16,19}$", requestModel.getCcId()))
            {
                responseModel = new ShopModifyResponseModel(322, "Credit card ID has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();
            if (Customer.insertCustomerDB(requestModel, responseModel)){
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        }catch(IOException e){
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new ShopModifyResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new ShopModifyResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String jsonText)
    {
        ServiceLogger.LOGGER.info("Recieved request to insert into customer DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        CustomerModifyRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, CustomerModifyRequestModel.class);
            if (requestModel.getCcId().length() > 19 || requestModel.getCcId().length() < 16) {
                responseModel = new ShopModifyResponseModel(321, "Credit card ID has invalid length.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if (!Pattern.matches("^[0-9]{16,19}$", requestModel.getCcId())) {
                responseModel = new ShopModifyResponseModel(322, "Credit card ID has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();
            if (Customer.updateCustomerDB(requestModel, responseModel)){
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }


        }catch(IOException e){
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new ShopModifyResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new ShopModifyResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieve(String jsonText){
        ServiceLogger.LOGGER.info("Recieved request to retrieve from customer DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        CustomerRequestModel requestModel;
        CustomerRetrieveResponseModel responseModel;

        try{
            requestModel = mapper.readValue(jsonText, CustomerRequestModel.class);
            responseModel = new CustomerRetrieveResponseModel();

            if (Customer.retrieveCustomerDB(requestModel, responseModel)){
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        }catch(IOException e){
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CustomerRetrieveResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CustomerRetrieveResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
