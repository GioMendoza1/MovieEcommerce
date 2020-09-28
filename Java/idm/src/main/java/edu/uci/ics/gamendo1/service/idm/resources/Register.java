package edu.uci.ics.gamendo1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.idm.core.Users;
import edu.uci.ics.gamendo1.service.idm.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.idm.models.RegisterRequestModel;
import edu.uci.ics.gamendo1.service.idm.models.RegisterResponseModel;
import edu.uci.ics.gamendo1.service.idm.security.Crypto;
import org.apache.commons.codec.binary.Hex;

import javax.ws.rs.*;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.regex.Pattern;

@Path("register")
public class Register {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response insertRequest(String jsonText){
        ServiceLogger.LOGGER.info("Recieved request to insert sentence into DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        RegisterRequestModel requestModel;
        RegisterResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);
            String email = requestModel.getEmail();
            char[] password = requestModel.getPassword();
            if (email == null || email.length() > 50 || email.length() < 1){
                responseModel = new RegisterResponseModel(-10, "Email address has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (!Pattern.matches("^[A-Za-z0-9\\-\\!\\#\\$\\%\\&\\'\\*\\+\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+\\@[A-Za-z0-9]+\\.[A-Za-z0-9]+$", email))
            {
                responseModel = new RegisterResponseModel(-11, "Email address has invalid format.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if(password == null || password.length > 128 || password.length < 1)
            {
                responseModel = new RegisterResponseModel(-12, "Password has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if(password.length < 7 || password.length > 16)
            {
                responseModel = new RegisterResponseModel(12, "Password does not meet length requirements.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            String strPass = String.copyValueOf(password);
            if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[.!@#$%]).{7,16}$", strPass))
            {
                responseModel = new RegisterResponseModel(13, "Password does not meet character requirements.");
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            byte[] salt = Crypto.genSalt();
            String hashedPass = Hex.encodeHexString(Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH));
            String encodedSalt = Hex.encodeHexString(salt);
            responseModel = new RegisterResponseModel();
            if (Users.registerUser(responseModel, requestModel, encodedSalt, hashedPass)){
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }



        }
        catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new RegisterResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new RegisterResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
