package de.atns.common.security;

import com.google.inject.Inject;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author tbaum
 * @since 10.08.2011
 */
@Path("/auth") @Produces(APPLICATION_JSON)
public class SecurityEndpoint {

    private final SecurityFilter securityFilter;

    @Inject public SecurityEndpoint(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @POST @Path("/login") public UUID login(@FormParam("login") String login, @FormParam("password") String password) {
        securityFilter.login(login, password);
        return securityFilter.getAuthToken();
    }

    @POST @Path("/logout") public void logout() {
        securityFilter.logout();
    }
}
