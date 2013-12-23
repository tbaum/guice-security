package com.google.inject.extensions.security;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tbaum
 * @since 25.10.2009
 */
public class SecurityInterceptor implements MethodInterceptor {

    @Inject private final SecurityScope securityScope = null;
    @Inject private final Injector injector = null;

    @Override public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Secured secured = invocation.getMethod().getAnnotation(Secured.class);
        final SecurityUser user = securityScope.get();

        if (user == null) {
            throw new NotLogginException();
        }

        final SecurityDecisionMaker decisionMaker = injector.getInstance(secured.decisionMaker());

        if (decisionMaker.hasAccessTo(user, secured, invocation)) {
            return invocation.proceed();
        }

        throw new NotInRoleException(invocation.getMethod().toString(), toStringList(secured));
    }

    private List<String> toStringList(Secured secured) {
        List<String> roles = new ArrayList<>();
        for (Class<? extends SecurityRole> role : secured.value()) {
            roles.add(role.getSimpleName());
        }
        return roles;
    }
}

