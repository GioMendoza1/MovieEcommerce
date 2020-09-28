package edu.uci.ics.gamendo1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.gamendo1.service.idm.core.Users;
import edu.uci.ics.gamendo1.service.idm.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.idm.models.PrivilegeRequestModel;
import edu.uci.ics.gamendo1.service.idm.models.PrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.regex.Pattern;

@Path("privilege")
public class VerifyPrivilege {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response verifyPrivilege(String jsonText) {
        ServiceLogger.LOGGER.info("Recieved request to insert sentence into DB.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        PrivilegeRequestModel requestModel;
        PrivilegeResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, PrivilegeRequestModel.class);
            String email = requestModel.getEmail();

            if (email == null || email.length() > 50 || email.length() < 1) {
                responseModel = new PrivilegeResponseModel(-10, "Email address has invalid length.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (!Pattern.matches("^[A-Za-z0-9\\-\\!\\#\\$\\%\\&\\'\\*\\+\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+\\@[A-Za-z0-9]+\\.[A-Za-z0-9]+$", email)) {
                responseModel = new PrivilegeResponseModel(-11, "Email address has invalid format.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            if (requestModel.getPlevel() < 1 || requestModel.getPlevel() > 5) {
                responseModel = new PrivilegeResponseModel(-14, "Privilege level out of valid range.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            responseModel = new PrivilegeResponseModel();
            if (Users.determinePrivilege(requestModel, responseModel)) {
                return Response.status(Response.Status.OK).entity(responseModel).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new PrivilegeResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new PrivilegeResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
