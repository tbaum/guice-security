package com.google.inject.extensions.security;

import com.google.inject.extensions.security.filter.FromHeader;
import com.google.inject.extensions.security.filter.FromSession;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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
    private final FromSession authSession;
    private final FromHeader authHeader;

    @Inject public SecurityEndpoint(SecurityService securityService, UserService userService, FromSession authSession,
                                    FromHeader authHeader) {
        this.securityService = securityService;
        this.userService = userService;
        this.authSession = authSession;
        this.authHeader = authHeader;
    }

    @POST @Path("/login") @Consumes(APPLICATION_FORM_URLENCODED)
    public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response,
                          @FormParam("login") String login, @FormParam("password") String password) {
        SecurityUser user = userService.findUser(login, password);
        if (user == null) return Response.status(500).build();

        String token = securityService.authenticate(user);
        authSession.postAuth(request, response);
        authHeader.postAuth(request, response);

        return Response
                .ok(token)
                .header(HEADER_NAME, token)
                .build();
    }

    @POST @Path("/login") @Consumes(APPLICATION_JSON)
    public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response,
                          Map<String, String> data) {
        SecurityUser user = userService.findUser(data.get("login"), data.get("password"));
        if (user == null) {
            return Response.status(401,"unable to authenticate").build();
        }
        String token = securityService.authenticate(user);
        authSession.postAuth(request, response);
        authHeader.postAuth(request, response);

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

    @POST @Path("/logout")
    public void logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        securityService.clearAuthentication();
        authSession.postAuth(request, response);
    }

    @GET @Path("/check") @Secured
    public Map<String, String> authenticate() {
        SecurityUser securityUser = securityService.currentUser();
        return getResult(securityUser.getToken(), securityUser.getLogin());
    }
}
