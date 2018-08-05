package com.google.inject.extensions.security.filter;

import com.google.inject.BindingAnnotation;
import com.google.inject.extensions.security.*;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author tbaum
 * @since 12.05.2014
 */
public class FromHeader implements AuthFilterPlugin {

    private final boolean sendTokenInResponse;
    private final SecurityService securityService;
    private static final Logger LOG = getLogger(FromHeader.class);

    @Inject public FromHeader(@SendTokenInResponse boolean sendTokenInResponse, SecurityService securityService) {
        this.sendTokenInResponse = sendTokenInResponse;
        this.securityService = securityService;
    }


    public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader(SecurityFilter.HEADER_NAME);
            return token != null && !token.isEmpty() && this.securityService.authenticate(token) != null;
        } catch (InvalidTokenException e) {
            LOG.warn(e.getMessage());
            return false;
        }
    }

    public void postAuth(HttpServletRequest request, HttpServletResponse response) {
        SecurityUser securityUser = securityService.currentUser();
        String token = securityUser != null ? securityUser.getToken() : null;

        if (sendTokenInResponse) {
            response.setHeader(SecurityFilter.HEADER_NAME, token);
        }
    }

    @BindingAnnotation @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME) public @interface SendTokenInResponse {
    }

}
