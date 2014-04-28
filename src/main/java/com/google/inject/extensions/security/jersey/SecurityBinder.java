package com.google.inject.extensions.security.jersey;

import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * @author tbaum
 * @since 28.04.2014
 */
public class SecurityBinder extends AbstractBinder {
    @Override protected void configure() {
        bind(SecurityInterceptionService.class).to(InterceptionService.class).in(Singleton.class);
    }
}
