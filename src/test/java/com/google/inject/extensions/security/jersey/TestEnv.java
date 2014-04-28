package com.google.inject.extensions.security.jersey;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.Scopes;
import com.google.inject.extensions.security.*;

import java.util.Iterator;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * @author tbaum
 * @since 16.03.2014
 */
public class TestEnv extends GuiceBerryModule {

    @Override
    protected void configure() {
        super.configure();
        install(new SecurityModule() {

            @Override protected void configureSecurity() {
                bind(SecurityInterceptor.class).in(Scopes.SINGLETON);
                bind(RoleConverter.class).toInstance(new MyRoleConverter());

                bind(SecurityTokenService.class).toInstance(
                        new SecurityTokenServiceImpl(HOURS.toMillis(1), DAYS.toMillis(14),
                                "asdfghdsffadsdffafjkhgfds", 8)
                );
                bind(UserService.class).to(UserServiceImpl.class);

            }
        });

        install(new TomcatGuiceBerryEnvMain());
    }

    private static class MyRoleConverter implements RoleConverter {
        @Override public Iterator<String> iterator() {
            return null;
        }

        @Override public Class<? extends SecurityRole> toRole(String role) {
            return null;
        }

        @Override public String toString(Class<? extends SecurityRole> role) {
            return null;
        }
    }
}
