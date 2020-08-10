package com.google.inject.extensions.security;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author tbaum
 * @since 10.08.2011
 */
@Path("/auth")
public class SecurityEndpoint {

    private final SecurityTokenService tokenService;
    private final SecurityService securityService;
    private final UserService userService;

    @Inject
    public SecurityEndpoint(SecurityTokenService tokenService, SecurityService securityService, UserService userService) {
        this.tokenService = tokenService;
        this.securityService = securityService;
        this.userService = userService;
    }

    @POST
    @Path("/login") @Produces(APPLICATION_JSON)
    public Response loginForm(@FormParam("username") String username, @FormParam("password") String password) {
        SecurityUser user = userService.findUser(username, password);
        if (user == null) {
            return Response.status(401, "unable to authenticate").build();
        }
        securityService.authenticate(user);
        return success(user);
    }

    protected Response success(SecurityUser user) {
        String token = tokenService.createToken(user);
        String username = user.getUsername();
        return Response.ok(new LoginResponse(token, true, username), APPLICATION_JSON).build();
    }

    @POST
    @Path("/login") @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response login(LoginRequest data) {
        return loginForm(data.username, data.password);
    }

    @POST
    @Path("/login") @Produces(TEXT_PLAIN)
    public Response loginText(@FormParam("username") String username, @FormParam("password") String password) {
        SecurityUser user = userService.findUser(username, password);
        if (user == null) {
            return Response.status(401, "unable to authenticate").build();
        }
        securityService.authenticate(user);
        String token = tokenService.createToken(user);
        return Response.ok(token).build();
    }

    @POST
    @Path("/login") // @Produces(TEXT_PLAIN)
    @Consumes(APPLICATION_JSON)
    public Response loginText(LoginRequest data) {
        return loginText(data.username, data.password);
    }

    @GET
    @Path("/refresh")
    @Secured @Produces(APPLICATION_JSON)
    public Response authenticate() {
        SecurityUser user = securityService.currentUser();
        return success(user);
    }

    @GET
    @Path("/refresh")
    @Secured //@Produces(TEXT_PLAIN)
    public Response authenticateText() {
        SecurityUser user = securityService.currentUser();
        String token = tokenService.createToken(user);
        return Response.ok(token).build();
    }

    public static class LoginResponse {
        public String token;
        public boolean success;
        public String login;

        public LoginResponse() {
        }

        LoginResponse(String token, boolean success, String login) {
            this.token = token;
            this.success = success;
            this.login = login;
        }
    }

    public static class LoginRequest {
        public String username;
        public String password;

        LoginRequest() {
        }

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
