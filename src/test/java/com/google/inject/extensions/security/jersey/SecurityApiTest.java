package com.google.inject.extensions.security.jersey;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.extensions.security.ClientAuthFilter;
import com.google.inject.extensions.security.SecurityEndpoint.LoginRequest;
import com.google.inject.extensions.security.SecurityEndpoint.LoginResponse;
import com.google.inject.extensions.security.SecurityTokenService;
import com.google.inject.extensions.security.SimpleUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.inject.extensions.security.GuiceSecurityFilter.BEARER_PREFIX;
import static com.google.inject.extensions.security.GuiceSecurityFilter.HEADER_NAME;
import static com.google.inject.extensions.security.jersey.TomcatGuiceBerryEnvMain.BASE_URI;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

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

        Response response = target.path("/api/security-test/secured")
                .request(APPLICATION_JSON).get();

        assertEquals(200, response.getStatus());
        assertEquals(new HashMap<String, Object>() {{
            put("user", "remote2");
        }}, response.readEntity(Map.class));
    }


    @Test public void performLogin() {
        Client client = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build();

        WebTarget target = client.target(BASE_URI);
        Response response = target.path("/api/auth/login").request(APPLICATION_JSON)
                .post(json(new LoginRequest("remote1", "abcdef")));

        assertEquals(200, response.getStatus());
        String token = response.readEntity(LoginResponse.class).token;
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(TestEnv.JWS_KEY).build().parseClaimsJws(token);
        assertEquals("remote1", claimsJws.getBody().getSubject());
        assertEquals(Collections.singletonList("ROLE1"), claimsJws.getBody().get("a", List.class));
    }

    @Test public void performLoginText() {
        Client client = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build();

        WebTarget target = client.target(BASE_URI);
        Response response = target.path("/api/auth/login").request()
                .post(json(new LoginRequest("remote1", "abcdef")));

        assertEquals(200, response.getStatus());
        String token = response.readEntity(String.class);
        JwtParser defaultJwtParser = Jwts.parserBuilder().setSigningKey(TestEnv.JWS_KEY).build();
        Jws<Claims> claimsJws = defaultJwtParser.parseClaimsJws(token);
        assertEquals("remote1", claimsJws.getBody().getSubject());
    }

    @Test public void performInvalidLogin() {
        Client client = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build();

        WebTarget target = client.target(BASE_URI);
        Response response = target.path("/api/auth/login").request()
                .post(json(new LoginRequest("remote1", "fooo")));
        assertEquals(401, response.getStatus());
        assertThat(response.readEntity(String.class), containsString("Unauthorized"));
    }

    @Test public void securedNotAuthorizedResource() {
        WebTarget target = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build().target(BASE_URI);
        Response response = target.path("/api/security-test/secured").request().get();

        assertEquals(401, response.getStatus());
    }

    @Test public void securedResource() {
        WebTarget target = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build().target(BASE_URI);
        String token = securityTokenService.createToken(new SimpleUser("remote1"));
        Response response = target.path("/api/security-test/secured").request()
                .header(HEADER_NAME, BEARER_PREFIX + token)
                .get();

        assertEquals(200, response.getStatus());
        assertEquals(new HashMap<String, Object>() {{
            put("user", "remote1");
        }}, response.readEntity(Map.class));
    }

    @Test public void securedResourceInvalidAuth1() {
        WebTarget target = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build().target(BASE_URI);
        Response response = target.path("/api/security-test/secured").request()
                .header(HEADER_NAME, BEARER_PREFIX + "eyJhbGciOiJIUzUxMiJ9" +
                        ".eyJhIjpbIkFETUlOIl0sInN1YiI6InVzZXIxIiwiaWF0IjoxNTk3MDc4NzUwLCJleHAiOjQxODkwNzg3NTB9" +
                        ".bbW4YFJkG_ivcKq3X3hpb4Yf-OgBLnDKECOn84sMz5Lf6VEfSwaueoLcCyJlP9uibkxPJOLHMzTQ9YZyH3RUHQ")
                .get();

        assertEquals(401, response.getStatus());
    }

    @Test public void securedResourceInvalidAuth2() {
        WebTarget target = JerseyClientBuilder.newBuilder().register(JacksonFeature.class).build().target(BASE_URI);
        Response response = target.path("/api/security-test/secured").request()
                .header(HEADER_NAME, BEARER_PREFIX + "xxx")
                .get();

        assertEquals(401, response.getStatus());
    }

    @Test public void unsecuredResource() {
        WebTarget target = JerseyClientBuilder.newBuilder().build().target(BASE_URI);
        Response response = target.path("/api/security-test/unsecured").request().get();
        assertEquals(200, response.getStatus());
        assertEquals("12357", response.readEntity(String.class));
    }
}
