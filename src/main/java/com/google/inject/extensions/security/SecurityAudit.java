package com.google.inject.extensions.security;

import com.google.inject.ImplementedBy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author tbaum
 * @since 04.02.2014
 */
@ImplementedBy(SecurityAuditImpl.class)
public interface SecurityAudit {
    void startRequest(HttpServletRequest request, HttpServletResponse response);

    void finishRequest();

    void failed(SecurityUser user, Secured secured, Method method, Object... arguments);

    void granted(SecurityUser user, Secured secured, Method method, Object... arguments);
}
