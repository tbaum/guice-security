package com.google.inject.extensions.security.jersey;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.extensions.security.ClientAuthFilter;
import com.google.inject.extensions.security.SecurityFilter;
import com.google.inject.extensions.security.SecurityTokenService;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.google.inject.extensions.security.jersey.TomcatGuiceBerryEnvMain.BASE_URI;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * @author tbaum
 * @since 16.03.2014
 */
public class SecurityApiTest {

    @Rule public GuiceBerryRule guiceBerry = new GuiceBerryRule(TestEnv.class);
    @Inject SecurityTokenService securityTokenService;


    @Test public void clientFilterTest() {
        Client client = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target1 = client.target(BASE_URI);
        client.register(new ClientAuthFilter("remote2", "abcdef", target1));
        WebTarget target = client.target(BASE_URI);

        Response response = target.path("/api/security-test/secured").request(APPLICATION_JSON_TYPE).get();

        assertEquals(200, response.getStatus());
        assertEquals(new HashMap<String, Object>() {{
            put("user", "remote2");
        }}, response.readEntity(Map.class));
    }


    @Test public void performLogin() {
        Client client = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build();

        WebTarget target = client.target(BASE_URI);
        Response response = target.path("/api/auth/login").request(APPLICATION_JSON_TYPE)
                .post(Entity.entity(new HashMap<String, Object>() {{
                    put("login", "remote1");
                    put("password", "abcdef");
                }}, APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
        final String headerToken = response.getHeaderString(SecurityFilter.HEADER_NAME);
        assertNotNull(headerToken);

        SecurityResponse result = response.readEntity(SecurityResponse.class);
        assertEquals("remote1", result.login);
        assertEquals(headerToken, result.token);
        assertTrue(result.success);
    }

    @Test public void performInvalidLogin() {
        Client client = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build();

        WebTarget target = client.target(BASE_URI);
        Response response = target.path("/api/auth/login").request(APPLICATION_JSON_TYPE)
                .post(Entity.entity(new HashMap<String, Object>() {{
                    put("login", "remote1");
                    put("password", "fooo");
                }}, APPLICATION_JSON_TYPE));

        assertEquals(401, response.getStatus());
        assertThat(response.readEntity(String.class), containsString("unable to authenticate"));
    }

    @Test public void securedNotAuthorizedResource() {
        WebTarget target = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build().target(BASE_URI);
        Response response = target.path("/api/security-test/secured").request(APPLICATION_JSON_TYPE).get();

        assertEquals(401, response.getStatus());
    }

    @Test public void securedResource() {
        WebTarget target = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build().target(BASE_URI);
        Response response = target.path("/api/security-test/secured").request(APPLICATION_JSON_TYPE)
                .header("X-Authorization", securityTokenService.createToken("remote1"))
                .get();

        assertEquals(200, response.getStatus());
        assertEquals(new HashMap<String, Object>() {{
            put("user", "remote1");
        }}, response.readEntity(Map.class));
    }

    @Test public void securedResourceInvalidAuth() {
        WebTarget target = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build().target(BASE_URI);
        Response response = target.path("/api/security-test/secured").request(APPLICATION_JSON_TYPE)
                .header("X-Authorization", "foo")
                .get();

        assertEquals(401, response.getStatus());
    }

    @Test public void unsecuredResource() {
        WebTarget target = JerseyClientBuilder.newBuilder().build().target(BASE_URI);
        Response response = target.path("/api/security-test/unsecured").request(APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        assertEquals("12357", response.readEntity(String.class));
    }

    public static class SecurityResponse {
        public String token;
        public String login;
        public boolean success;
    }
}
