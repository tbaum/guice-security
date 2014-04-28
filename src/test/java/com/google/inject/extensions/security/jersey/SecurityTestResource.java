package com.google.inject.extensions.security.jersey;


import com.google.inject.extensions.security.Secured;
import com.google.inject.extensions.security.SecurityService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

/**
 * @author tbaum
 * @since 10.03.2014
 */
@Path("/security-test")
public class SecurityTestResource {
    private final SecurityService s;

    @Inject public SecurityTestResource(SecurityService s) {
        this.s = s;
    }

    @GET @Path("unsecured")
    public Response unsecured() {
        return Response.ok("12357", APPLICATION_OCTET_STREAM).build();
    }

    @GET @Secured @Path("secured") @Produces(APPLICATION_JSON)
    public Map<String, Object> secured() {
        return new HashMap<String, Object>() {{
            put("user", s.currentUser().getLogin());
        }};
    }
}



