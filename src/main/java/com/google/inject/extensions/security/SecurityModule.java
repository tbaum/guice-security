package com.google.inject.extensions.security;

import com.google.inject.AbstractModule;
import com.google.inject.binder.ScopedBindingBuilder;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static com.google.inject.multibindings.Multibinder.newSetBinder;

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

        bindFilters();
//        bind(FromSession.class);
//        bind(FromHeader.class);
        configureSecurity();
    }

    protected void bindFilters() {
//        bindConstant().annotatedWith(FromHeader.SendTokenInResponse.class).to(true);

//        bindAuthFilterPlugin(FromHeader.class);
//        bindAuthFilterPlugin(FromParameter.class);
      //  bindAuthFilterPlugin(HttpBasicAuth.class);
     //   bindAuthFilterPlugin(HttpBearerAuth.class);
//        bindAuthFilterPlugin(FromSession.class);
//        bindAuthFilterPlugin(FromCookie.class);
     //   bindAuthFilterPlugin(AddDetails.class);
    }

  //  protected ScopedBindingBuilder bindAuthFilterPlugin(Class<? extends AuthFilterPlugin> implementation) {
   //     return newSetBinder(binder(), AuthFilterPlugin.class).addBinding().to(implementation);
   // }

    protected abstract void configureSecurity();
}
