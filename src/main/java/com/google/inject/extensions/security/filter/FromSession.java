package com.google.inject.extensions.security.filter;

import com.google.inject.extensions.security.SecurityScoped;
import com.google.inject.extensions.security.SecurityService;
import com.google.inject.extensions.security.SecurityUser;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author tbaum
 * @since 12.05.2014
 */
public class FromSession implements AuthFilterPlugin {
    public static final String SESSION_TOKEN = "_SECURITY_UUID";
    private final SecurityService securityService;

    @Inject public FromSession(SecurityService securityService) {
        this.securityService = securityService;
    }

    @SecurityScoped @Override
    public void postAuth(HttpServletRequest request, HttpServletResponse response) {
        SecurityUser securityUser = securityService.currentUser();
        String token = securityUser != null ? securityUser.getToken() : null;
        if (token != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_TOKEN, token);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(SESSION_TOKEN);
            }
        }
    }

    public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        final String sessionToken = (String) session.getAttribute(SESSION_TOKEN);
        return sessionToken != null && !sessionToken.isEmpty() && securityService.authenticate(sessionToken) != null;
    }

}
