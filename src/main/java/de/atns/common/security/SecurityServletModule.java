package de.atns.common.security;

import com.google.inject.servlet.ServletModule;

public abstract class SecurityServletModule extends ServletModule {

    protected abstract void configureSecurity();

    @Override protected void configureServlets() {
        install(new SecurityModule() {
            @Override protected void configureSecurity() {
                SecurityServletModule.this.configureSecurity();
            }
        });
        filter("/*").through(SecurityFilter.class);
    }
}
