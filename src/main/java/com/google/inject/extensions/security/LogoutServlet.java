package com.google.inject.extensions.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.extensions.security.filter.FromSession;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tbaum
 * @since 28.10.2009 21:39:05
 */
@Singleton public class LogoutServlet extends HttpServlet {

    private final FromSession authSession;
    private final SecurityService securityService;

    @Inject public LogoutServlet(FromSession authSession, SecurityService securityService) {
        this.authSession = authSession;
        this.securityService = securityService;
    }

    @Override @SecurityScoped protected void service(HttpServletRequest req, HttpServletResponse resp) {
        securityService.clearAuthentication();
        authSession.postAuth(req, resp);
    }
}
