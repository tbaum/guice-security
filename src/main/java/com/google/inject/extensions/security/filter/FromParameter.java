package com.google.inject.extensions.security.filter;

import com.google.inject.extensions.security.SecurityService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tbaum
 * @since 12.05.2014
 */
public class FromParameter implements AuthFilterPlugin {
    private static final String PARAMETER_NAME = "_SECURITY_UUID";
    private final SecurityService securityService;

    @Inject public FromParameter(SecurityService securityService) {
        this.securityService = securityService;
    }

    public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getParameter(PARAMETER_NAME);
        return token != null && !token.isEmpty() && securityService.authenticate(token) != null;
    }

    @Override public void postAuth(HttpServletRequest request, HttpServletResponse response) {
    }
}
