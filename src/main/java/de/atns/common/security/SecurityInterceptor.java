package de.atns.common.security;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author tbaum
 * @since 25.10.2009
 */
public class SecurityInterceptor implements MethodInterceptor {
// ------------------------------ FIELDS ------------------------------

    private final SecurityScope securityScope;

// --------------------------- CONSTRUCTORS ---------------------------

    public SecurityInterceptor(final SecurityScope securityScope) {
        this.securityScope = securityScope;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface MethodInterceptor ---------------------

    @Override public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Secured secured = invocation.getMethod().getAnnotation(Secured.class);
        final SecurityUser user = securityScope.get(SecurityUser.class);

        if (user == null) {
            throw new NotLogginException();
        }

        if (!user.hasAccessTo(secured)) {
            throw new NotInRoleException(secured, invocation.getMethod().toString());
        }

        return invocation.proceed();
    }
}

