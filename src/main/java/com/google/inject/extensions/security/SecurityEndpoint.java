package com.google.inject.extensions.security;

import com.google.inject.Inject;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Map;

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
    public String login(@FormParam("login") String login, @FormParam("password") String password) {
        SecurityUser user = userService.findUser(login, password);
        return user == null ? null : securityService.authenticate(user);
    }

    @POST @Path("/login") @Consumes(APPLICATION_JSON)
    public Map<String, String> login(Map<String, String> data) {
        SecurityUser user = userService.findUser(data.get("login"), data.get("password"));
        if (user == null) {
            throw new RuntimeException("unable to authenticate");
        }
        return getResult(securityService.authenticate(user), user.getLogin());
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
