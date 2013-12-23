package com.google.inject.extensions.security;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author tbaum
 * @since 23.12.2013
 */
public interface SecurityDecisionMaker {
    boolean hasAccessTo(SecurityUser user, Secured secured, MethodInvocation invocation);
}
