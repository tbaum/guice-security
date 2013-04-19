package com.google.inject.extensions.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tbaum
 * @since 28.10.2009 21:39:05
 */
@Singleton public class LoginServlet extends HttpServlet {

    private final SecurityFilter securityFilter;
    private final SecurityService securityService;
    private final UserService userService;

    @Inject
    public LoginServlet(final SecurityFilter securityFilter, SecurityService securityService, UserService userService) {
        this.securityFilter = securityFilter;
        this.securityService = securityService;
        this.userService = userService;
    }

    @Override protected void service(final HttpServletRequest req, final HttpServletResponse resp) {
        SecurityUser user = userService.findUser(req.getParameter("login"), req.getParameter("password"));
        String token = user == null ? null : securityService.authenticate(user);
        securityFilter.setSessionToken(token);
    }
}
