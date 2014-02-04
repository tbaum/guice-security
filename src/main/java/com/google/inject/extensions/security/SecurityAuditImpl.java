package com.google.inject.extensions.security;

import org.slf4j.Logger;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

import static java.lang.Boolean.TRUE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author tbaum
 * @since 04.02.2014
 */
@Singleton
public class SecurityAuditImpl implements SecurityAudit {

    private static final Logger LOG = getLogger(SecurityAuditImpl.class);
    private final ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    private final ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();
    private final ThreadLocal<Boolean> printed = new ThreadLocal<Boolean>() {{
        set(false);
    }};

    @Override public void failed(SecurityUser user, Secured secured, Method method, Object... arguments) {
        logFirstRequestUri();
        LOG.warn("denied {} access to {}::{} args {}",
                user != null ? user.getLogin() : "-",
                method.getDeclaringClass().getCanonicalName(), method.getName(), Arrays.toString(arguments));

    }

    private void logFirstRequestUri() {
        if (TRUE.equals(printed.get())) return;
        printed.set(true);

        final HttpServletRequest request = this.request.get();
        if (request == null) {
            LOG.error("request not set");
        } else {
            LOG.info("request: {} {}", request.getRequestURI(), request.getRemoteHost());
        }
    }

    @Override public void granted(SecurityUser user, Secured secured, Method method, Object... arguments) {
        logFirstRequestUri();
        LOG.info("granted {} access to {}::{} args {}",
                user != null ? user.getLogin() : "-",
                method.getDeclaringClass().getCanonicalName(), method.getName(), Arrays.toString(arguments));
    }

    @Override public void startRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request.set(request);
        this.response.set(response);
        printed.set(false);
    }

    @Override public void finishRequest() {
        request.remove();
        response.remove();
    }
}
