package com.google.inject.extensions.security;

import com.google.inject.AbstractModule;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public abstract class SecurityModule extends AbstractModule {

    @Override public final void configure() {
        final SecurityScope securityScope = new SecurityScope();
        bindScope(SecurityScoped.class, securityScope);
        bind(SecurityScope.class).toInstance(securityScope);
        SecurityInterceptor securityInterceptor = new SecurityInterceptor();
        requestInjection(securityInterceptor);
        bindInterceptor(any(), annotatedWith(Secured.class), securityInterceptor);
        bindInterceptor(any(), annotatedWith(SecurityScoped.class), new SecurityScopedInterceptor(securityScope));

        configureSecurity();
    }

    protected abstract void configureSecurity();
}
