package com.google.inject.extensions.security;

import com.google.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;

/**
 * @author tbaum
 * @since 10.08.2011
 */
@Path("/auth") @Produces(APPLICATION_JSON)
public class SecurityEndpoint {

    private final SecurityService securityService;
    private final UserService userService;

    @Inject public SecurityEndpoint(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @POST @Path("/login") @Consumes(APPLICATION_FORM_URLENCODED)
    public String login(@FormParam("login") String login, @FormParam("password") String password) {
        SecurityUser user = userService.findUser(login, password);
        return user == null ? null : securityService.authenticate(user);
    }

    @POST @Path("/login") @Consumes(APPLICATION_JSON)
    public String login(Map<String, String> data) {
        SecurityUser user = userService.findUser(data.get("login"), data.get("password"));
        return user == null ? null : securityService.authenticate(user);
    }

    @POST @Path("/logout") public void logout() {
        securityService.clearAuthentication();
    }

    @GET @Path("/info") @Secured public Response info() {
        return ok().build();
    }
}
