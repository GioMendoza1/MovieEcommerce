package edu.uci.ics.gamendo1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.billing.core.Shopping;
import edu.uci.ics.gamendo1.service.billing.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.billing.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.regex.Pattern;

@Path("cart")
public class ShoppingCart {


    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(String jsonText)
    {
        ServiceLogger.LOGGER.info("Recieved request to insert into shopping cart DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        ShopModifyRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, ShopModifyRequestModel.class);
            String email = requestModel.getEmail();

            if (email == null || email.length() > 50 || email.length() < 1)
            {
                responseModel = new ShopModifyResponseModel(-10, "Email address has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (!Pattern.matches("^[A-Za-z0-9\\-\\!\\#\\$\\%\\&\\'\\*\\+\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+\\@[A-Za-z0-9]+\\.[A-Za-z0-9]+$", email))
            {
                responseModel = new ShopModifyResponseModel(-11, "Email address has invalid format.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (requestModel.getQuantity() < 1)
            {
                responseModel = new ShopModifyResponseModel(33, "Quantity has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();

            if (Shopping.insertShoppingDB(requestModel, responseModel))
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
    public Response update(String jsonText){
        ServiceLogger.LOGGER.info("Recieved request to insert into shopping cart DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        ShopModifyRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, ShopModifyRequestModel.class);
            String email = requestModel.getEmail();

            if (email == null || email.length() > 50 || email.length() < 1)
            {
                responseModel = new ShopModifyResponseModel(-10, "Email address has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (!Pattern.matches("^[A-Za-z0-9\\-\\!\\#\\$\\%\\&\\'\\*\\+\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+\\@[A-Za-z0-9]+\\.[A-Za-z0-9]+$", email))
            {
                responseModel = new ShopModifyResponseModel(-11, "Email address has invalid format.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (requestModel.getQuantity() < 1)
            {
                responseModel = new ShopModifyResponseModel(33, "Quantity has invalid value.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();

            if (Shopping.updateShoppingDB(requestModel, responseModel))
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
        ServiceLogger.LOGGER.info("Recieved request to insert into shopping cart DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        ShopDeleteRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try{
            requestModel = mapper.readValue(jsonText, ShopDeleteRequestModel.class);
            String email = requestModel.getEmail();

            if (email == null || email.length() > 50 || email.length() < 1)
            {
                responseModel = new ShopModifyResponseModel(-10, "Email address has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (!Pattern.matches("^[A-Za-z0-9\\-\\!\\#\\$\\%\\&\\'\\*\\+\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+\\@[A-Za-z0-9]+\\.[A-Za-z0-9]+$", email))
            {
                responseModel = new ShopModifyResponseModel(-11, "Email address has invalid format.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();

            if (Shopping.deleteShoppingDB(requestModel, responseModel))
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
        ServiceLogger.LOGGER.info("Recieved request to insert into shopping cart DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        ShopRequestModel requestModel;
        ShopRetrieveResponseModel responseModel;

        try{
            requestModel = mapper.readValue(jsonText, ShopRequestModel.class);
            String email = requestModel.getEmail();

            if (email == null || email.length() > 50 || email.length() < 1)
            {
                responseModel = new ShopRetrieveResponseModel(-10, "Email address has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (!Pattern.matches("^[A-Za-z0-9\\-\\!\\#\\$\\%\\&\\'\\*\\+\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+\\@[A-Za-z0-9]+\\.[A-Za-z0-9]+$", email))
            {
                responseModel = new ShopRetrieveResponseModel(-11, "Email address has invalid format.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = new ShopRetrieveResponseModel();

            if (Shopping.retrieveShoppingDB(requestModel, responseModel))
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
                responseModel = new ShopRetrieveResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new ShopRetrieveResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clear(String jsonText){
        ServiceLogger.LOGGER.info("Recieved request to clear users shopping cart DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        ShopRequestModel requestModel;
        ShopModifyResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, ShopRequestModel.class);
            String email = requestModel.getEmail();

            if (email == null || email.length() > 50 || email.length() < 1) {
                responseModel = new ShopModifyResponseModel(-10, "Email address has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (!Pattern.matches("^[A-Za-z0-9\\-\\!\\#\\$\\%\\&\\'\\*\\+\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+\\@[A-Za-z0-9]+\\.[A-Za-z0-9]+$", email)) {
                responseModel = new ShopModifyResponseModel(-11, "Email address has invalid format.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = new ShopModifyResponseModel();

            if (Shopping.clearShoppingDB(requestModel, responseModel))
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



}
