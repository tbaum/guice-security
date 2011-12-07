package de.atns.common.security;

import com.google.inject.AbstractModule;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public abstract class SecurityModule<USER extends SecurityUser> extends AbstractModule {
// -------------------------- OTHER METHODS --------------------------

    @Override public final void configure() {
        requestStaticInjection(SecurityRolePresentation.class);

        final SecurityScope securityScope = new SecurityScope();
        bindScope(SecurityScoped.class, securityScope);
        bind(SecurityScope.class).toInstance(securityScope);
        final SecurityInterceptor securityInterceptor = new SecurityInterceptor(securityScope);
        bindInterceptor(any(), annotatedWith(Secured.class), securityInterceptor);

        configureSecurity();
    }

    protected abstract void configureSecurity();
}
