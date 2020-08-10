package com.google.inject.extensions.security;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author tbaum
 * @since 23.12.2013
 */
class TestSecurityModule extends SecurityModule {
    public static final SecretKey JWS_KEY = secretKeyFor(HS512);

    @Override protected void configureSecurity() {
        AbstractRoleConverter roleConverter = new AbstractRoleConverter() {
            @Override protected Map<String, Class<? extends SecurityRole>> allRoles() {
                return new HashMap<>();
            }
        };
        bind(SecurityTokenService.class)
                .toInstance(new SecurityTokenServiceImpl(
                        JWS_KEY,
                        MINUTES.toMillis(10),
                        roleConverter));

        bind(UserService.class).toInstance(new MockUserService());
        bind(RoleConverter.class).toInstance(roleConverter);
    }
}
