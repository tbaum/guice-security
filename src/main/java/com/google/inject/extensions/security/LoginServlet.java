package com.google.inject.extensions.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.extensions.security.filter.FromHeader;
import com.google.inject.extensions.security.filter.FromSession;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tbaum
 * @since 28.10.2009 21:39:05
 */
@Singleton public class LoginServlet extends HttpServlet {

    private final FromSession authSession;
    private final FromHeader authHeader;
    private final SecurityService securityService;
    private final UserService userService;

    @Inject public LoginServlet(SecurityService securityService, UserService userService,
                                FromSession authSession, FromHeader authHeader) {
        this.authSession = authSession;
        this.authHeader = authHeader;
        this.securityService = securityService;
        this.userService = userService;
    }

    @Override protected void service(final HttpServletRequest req, final HttpServletResponse resp) {
        SecurityUser user = userService.findUser(req.getParameter("login"), req.getParameter("password"));
        if (user != null) {
            securityService.authenticate(user);
        }
        authSession.postAuth(req, resp);
        authHeader.postAuth(req, resp);
    }
}
