package com.google.inject.extensions.security.jersey;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;

import static com.google.inject.extensions.security.jersey.TestContextListener.injector;
import static org.jvnet.hk2.guice.bridge.api.GuiceBridge.getGuiceBridge;

/**
 * @author tbaum
 * @since 12.03.14
 */
public class TestApplication extends ResourceConfig {

    @Inject
    public TestApplication(ServiceLocator serviceLocator) {
        getGuiceBridge().initializeGuiceBridge(serviceLocator);
        serviceLocator.getService(GuiceIntoHK2Bridge.class).bridgeGuiceInjector(injector);

        register(SecurityFeature.class);
        register(JacksonFeature.class);

        packages(getClass().getPackage().getName());
    }

}

