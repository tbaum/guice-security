package de.atns.common.security;

import com.google.inject.Inject;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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

    @POST @Path("/login")
    public String login(@FormParam("login") String login, @FormParam("password") String password) {
        SecurityUser user = userService.findUser(login, password);
        return user == null ? null : securityService.authenticate(user);
    }

    @POST @Path("/logout") public void logout() {
        securityService.clearAuthentication();
    }
}
