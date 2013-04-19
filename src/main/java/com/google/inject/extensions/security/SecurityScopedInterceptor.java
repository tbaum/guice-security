package com.google.inject.extensions.security;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author tbaum
 * @since 18.04.2013
 */
public class SecurityScopedInterceptor implements MethodInterceptor {

    private final SecurityScope securityScope;

    public SecurityScopedInterceptor(final SecurityScope securityScope) {
        this.securityScope = securityScope;
    }

    @Override public Object invoke(final MethodInvocation invocation) throws Throwable {
        boolean alreadyInScope = securityScope.inScope();
        if (!alreadyInScope) {
            securityScope.enter();
        }

        try {
            return invocation.proceed();
        } finally {
            if (!alreadyInScope) {
                securityScope.exit();
            }
        }
    }
}

