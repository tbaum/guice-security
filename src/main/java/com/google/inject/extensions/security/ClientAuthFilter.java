package com.google.inject.extensions.security;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public final class ClientAuthFilter implements ClientRequestFilter, ClientResponseFilter {

    private final String username;
    private final String password;
    private final WebTarget resource;
    private String token;

    public ClientAuthFilter(String username, String password, WebTarget resource, String authPath) {
        this.username = username;
        this.password = password;
        this.resource = resource.path(authPath);
    }

    public ClientAuthFilter(String username, String password, WebTarget resource) {
        this(username, password, resource, "/api/auth/login");
    }

    static boolean repeatRequest(ClientRequestContext request, ClientResponseContext response, String newToken) {
        Client client = ClientBuilder.newClient(request.getConfiguration());
        String method = request.getMethod();
        MediaType mediaType = request.getMediaType();
        URI lUri = request.getUri();

        WebTarget resourceTarget = client.target(lUri);
        Invocation.Builder builder = resourceTarget.request(mediaType);
        MultivaluedMap<String, Object> newHeaders = new MultivaluedHashMap<>();
        for (Map.Entry<String, List<Object>> entry : request.getHeaders().entrySet()) {
            if (SecurityFilter.HEADER_NAME.equals(entry.getKey())) {
                continue;
            }
            newHeaders.put(entry.getKey(), entry.getValue());
        }

        newHeaders.add(SecurityFilter.HEADER_NAME, newToken);
        builder.headers(newHeaders);

        Invocation invocation;
        if (request.getEntity() == null) {
            invocation = builder.build(method);
        } else {
            invocation = builder.build(method,
                    Entity.entity(request.getEntity(), request.getMediaType()));
        }
        Response nextResponse = invocation.invoke();


        if (nextResponse.hasEntity()) {
            response.setEntityStream(nextResponse.readEntity(InputStream.class));
        }
        MultivaluedMap<String, String> headers = response.getHeaders();
        headers.clear();
        headers.putAll(nextResponse.getStringHeaders());
        response.setStatus(nextResponse.getStatus());

        return response.getStatus() != Response.Status.UNAUTHORIZED.getStatusCode();
    }

    private void addAuthHeader(ClientRequestContext cr) {
        if (token == null) {
            token = fetchToken();
        }
        cr.getHeaders().add(SecurityFilter.HEADER_NAME, token);
    }

    private String fetchToken() {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("login", username);
        params.add("password", password);
        return resource.request().post(Entity.form(params), String.class);
    }

    private void invalidateToken() {
        token = null;
    }

    @Override public void filter(ClientRequestContext requestContext) throws IOException {
        if (token == null) {
            token = fetchToken();
        }

        requestContext.getHeaders().add(SecurityFilter.HEADER_NAME, token);
    }

    @Override public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        if (status == 401 || status == 403) {
            invalidateToken();
            addAuthHeader(requestContext);
            repeatRequest(requestContext, responseContext, token);
        }
    }
}
