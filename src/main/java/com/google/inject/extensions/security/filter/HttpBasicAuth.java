package com.google.inject.extensions.security.filter;

import com.google.inject.extensions.security.SecurityService;
import com.google.inject.extensions.security.SecurityUser;
import com.google.inject.extensions.security.UserService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

/**
 * @author tbaum
 * @since 12.05.2014
 */
public class HttpBasicAuth implements AuthFilterPlugin {

    private final SecurityService securityService;
    private final UserService userService;

    @Inject public HttpBasicAuth(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    public boolean authenticate(final HttpServletRequest request, HttpServletResponse response) {
        final String auth = request.getHeader("Authorization");

        if (auth == null || auth.toLowerCase().indexOf("basic ") != 0) {
            return false;
        }

        String[] u = new String(parseBase64Binary(auth.substring(6))).split(":");
        final SecurityUser user = userService.findUser(u[0], u[1]);
        return user != null && securityService.authenticate(user) != null;
    }

    @Override public void postAuth(HttpServletRequest request, HttpServletResponse response) {
    }
}
