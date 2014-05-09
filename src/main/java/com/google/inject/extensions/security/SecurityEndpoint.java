package com.google.inject.extensions.security;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.google.inject.extensions.security.SecurityFilter.HEADER_NAME;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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
    public Response login(@FormParam("login") String login, @FormParam("password") String password) {
        SecurityUser user = userService.findUser(login, password);
        if (user == null) return Response.status(500).build();

        String token = securityService.authenticate(user);
        return Response
                .ok(token)
                .header(HEADER_NAME, token)
                .build();
    }

    @POST @Path("/login") @Consumes(APPLICATION_JSON)
    public Response login(Map<String, String> data) {
        SecurityUser user = userService.findUser(data.get("login"), data.get("password"));
        if (user == null) {
            throw new RuntimeException("unable to authenticate");
        }
        String token = securityService.authenticate(user);
        return Response
                .ok(getResult(token, user.getLogin()))
                .header(HEADER_NAME, token)
                .build();
    }

    private Map<String, String> getResult(String authenticate, String login) {
        Map<String, String> result = new HashMap<>();
        result.put("success", "true");
        result.put("token", authenticate);
        result.put("login", login);
        return result;
    }

    @POST @Path("/logout") public void logout() {
        securityService.clearAuthentication();
    }

    @GET @Path("/check") @Secured
    public Map<String, String> authenticate() {
        SecurityUser securityUser = securityService.currentUser();
        return getResult(securityUser.getToken(), securityUser.getLogin());
    }
}
