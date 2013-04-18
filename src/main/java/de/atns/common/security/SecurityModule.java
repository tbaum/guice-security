package de.atns.common.security;

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
        bindInterceptor(any(), annotatedWith(Secured.class), new SecurityInterceptor(securityScope));
        bindInterceptor(any(), annotatedWith(SecurityScoped.class), new SecurityScopedInterceptor(securityScope));

        configureSecurity();
    }

    protected abstract void configureSecurity();
}
