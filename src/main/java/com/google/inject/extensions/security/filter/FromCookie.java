package com.google.inject.extensions.security.filter;

import com.google.inject.extensions.security.SecurityService;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author tbaum
 * @since 12.05.2014
 */
public class FromCookie implements AuthFilterPlugin {

    private static final String COOKIE_NAME = "_SECURITY_UUID";
    private final SecurityService securityService;

    @Inject public FromCookie(SecurityService securityService) {
        this.securityService = securityService;
    }

    public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        String token = findCookie(request.getCookies());
        return token != null && !token.isEmpty() && securityService.authenticate(token) != null;
    }

    @Override public void postAuth(HttpServletRequest request, HttpServletResponse response) {
    }

    private String findCookie(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                try {
                    return URLDecoder.decode(cookie.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
