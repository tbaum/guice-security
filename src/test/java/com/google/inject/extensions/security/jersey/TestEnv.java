package com.google.inject.extensions.security.jersey;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.Scopes;
import com.google.inject.extensions.security.*;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * @author tbaum
 * @since 16.03.2014
 */
public class TestEnv extends GuiceBerryModule {
    public static final SecretKey JWS_KEY = secretKeyFor(HS512);

    @Override
    protected void configure() {
        super.configure();
        install(new SecurityModule() {

            @Override protected void configureSecurity() {
                bind(SecurityInterceptor.class).in(Scopes.SINGLETON);
                RoleConverter roleConverter = new AbstractRoleConverter() {
                    @Override protected Map<String, Class<? extends SecurityRole>> allRoles() {
                        HashMap<String, Class<? extends SecurityRole>> r = new HashMap<>();
                        r.put("ROLE1", UserServiceImpl.SpecialRole.class);
                        return r;
                    }
                };
                bind(RoleConverter.class).toInstance(roleConverter);
                bind(SecurityTokenService.class).toInstance(new SecurityTokenServiceImpl(
                        JWS_KEY, HOURS.toMillis(1), roleConverter));
                bind(UserService.class).to(UserServiceImpl.class);

            }
        });

        install(new TomcatGuiceBerryEnvMain());
    }
}
