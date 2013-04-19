package com.google.inject.extensions.security;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tbaum
 * @since 25.10.2009
 */
public class SecurityInterceptor implements MethodInterceptor {

    private final SecurityScope securityScope;

    public SecurityInterceptor(final SecurityScope securityScope) {
        this.securityScope = securityScope;
    }

    @Override public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Secured secured = invocation.getMethod().getAnnotation(Secured.class);
        final SecurityUser user = securityScope.get();

        if (user == null) {
            throw new NotLogginException();
        }

        if (!user.hasAccessTo(secured)) {
            throw new NotInRoleException(invocation.getMethod().toString(), toStringList(secured));
        }

        return invocation.proceed();
    }

    private List<String> toStringList(Secured secured) {
        List<String> roles = new ArrayList<String>();
        for (Class<? extends SecurityRole> role : secured.value()) {
            roles.add(role.getSimpleName());
        }
        return roles;
    }
}

