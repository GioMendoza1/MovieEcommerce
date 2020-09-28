package edu.uci.ics.gamendo1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.billing.core.CreditCard;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.CCModifyRequestModel;
import edu.uci.ics.gamendo1.service.billing.models.CCRequestModel;
import edu.uci.ics.gamendo1.service.billing.models.CCRetrieveResponseModel;
import edu.uci.ics.gamendo1.service.billing.models.ShopModifyResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Pattern;

@Path("creditcard")
public class CreditCards {

    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(String jsonText)
    {
        ServiceLogger.LOGGER.info("Recieved request to insert into credit card DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        CCModifyRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            mapper.setDateFormat(dateFormat);
            requestModel = mapper.readValue(jsonText, CCModifyRequestModel.class);

            if (requestModel.getId().length() > 19 || requestModel.getId().length() < 16)
            {
                responseModel = new ShopModifyResponseModel(321, "Credit card ID has invalid length.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if (!Pattern.matches("^[0-9]{16,19}$", requestModel.getId()))
            {
                responseModel = new ShopModifyResponseModel(322, "Credit card ID has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if(requestModel.getExpiration().getTime() < System.currentTimeMillis())
            {
                responseModel = new ShopModifyResponseModel(323, "expiration has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();

            if (CreditCard.insertCCDB(requestModel, responseModel))
            {
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
        ServiceLogger.LOGGER.info("Recieved request to insert into credit card DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        CCModifyRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            mapper.setDateFormat(dateFormat);
            requestModel = mapper.readValue(jsonText, CCModifyRequestModel.class);

            if (requestModel.getId().length() > 19 || requestModel.getId().length() < 16) {
                responseModel = new ShopModifyResponseModel(321, "Credit card ID has invalid length.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if (!Pattern.matches("^[0-9]{16,19}$", requestModel.getId())) {
                responseModel = new ShopModifyResponseModel(322, "Credit card ID has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if (requestModel.getExpiration().getTime() < System.currentTimeMillis()) {
                responseModel = new ShopModifyResponseModel(323, "expiration has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            responseModel = new ShopModifyResponseModel();

            if (CreditCard.updateCCDB(requestModel,responseModel))
            {
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

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(String jsonText){
        ServiceLogger.LOGGER.info("Recieved request to delete from credit card DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        CCRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try{
            requestModel = mapper.readValue(jsonText, CCRequestModel.class);

            if (requestModel.getId().length() > 19 || requestModel.getId().length() < 16) {
                responseModel = new ShopModifyResponseModel(321, "Credit card ID has invalid length.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if (!Pattern.matches("^[0-9]{16,19}$", requestModel.getId())) {
                responseModel = new ShopModifyResponseModel(322, "Credit card ID has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();
            if (CreditCard.deleteCCDB(requestModel,responseModel))
            {
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
    public Response retrieve(String jsonText)
    {
        ServiceLogger.LOGGER.info("Recieved request to retrieve from credit card DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        CCRequestModel requestModel;
        CCRetrieveResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, CCRequestModel.class);

            if (requestModel.getId().length() > 19 || requestModel.getId().length() < 16) {
                responseModel = new CCRetrieveResponseModel(321, "Credit card ID has invalid length.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            if (!Pattern.matches("^[0-9]{16,19}$", requestModel.getId())) {
                responseModel = new CCRetrieveResponseModel(322, "Credit card ID has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            responseModel = new CCRetrieveResponseModel();

            if (CreditCard.retrieveCCDB(requestModel, responseModel)){

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
                responseModel = new CCRetrieveResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CCRetrieveResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
