package com.google.inject.extensions.security;

import java.util.HashMap;
import java.util.Map;

import static com.google.inject.extensions.security.SecurityTokenServiceImpl.createSalt;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author tbaum
 * @since 23.12.2013
 */
class TestSecurityModule extends SecurityModule {
    private static final String TEST_SALT = createSalt();

    @Override protected void configureSecurity() {
        bind(SecurityTokenService.class)
                .toInstance(new SecurityTokenServiceImpl(MINUTES.toMillis(10), MINUTES.toMillis(20), TEST_SALT, 5));

        bind(UserService.class).toInstance(new MockUserService());

        bind(RoleConverter.class).toInstance(new AbstractRoleConverter() {
            @Override protected Map<String, Class<? extends SecurityRole>> allRoles() {
                return new HashMap<>();
            }
        });
    }
}
