package com.google.inject.extensions.security;

import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Singleton;

/**
 * @author tbaum
 * @since 23.12.2013
 */
@Singleton
public class InRoleDecisionMaker implements SecurityDecisionMaker {

    @Override public boolean hasAccessTo(SecurityUser user, Secured secured, MethodInvocation invocation) {
        Class<? extends SecurityRole>[] requiredRoles = secured.value();

        if (requiredRoles.length == 0) {
            return true;
        }
        if (user.getRoles() == null) return false;

        for (final Class myRole : user.getRoles()) {
            for (final Class<? extends SecurityRole> s : requiredRoles) {
                if (s.isAssignableFrom(myRole)) {
                    return true;
                }
            }
        }
        return false;
    }
}
