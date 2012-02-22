package de.atns.common.security;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

public final class ClientAuthFilter extends ClientFilter {

    private String token;
    private final String username;
    private final String password;
    private final WebResource resource;

    public static WebResource login(String server, Client client) {
        return client.resource(server + "/api/auth/login");
    }

    public ClientAuthFilter(final String username, final String password, WebResource resource) {
        this.username = username;
        this.password = password;
        this.resource = resource;
    }

    @Override public ClientResponse handle(final ClientRequest cr) throws ClientHandlerException {
        addAuthHeader(cr);
        ClientResponse handle = getNext().handle(cr);

        int status = handle.getStatus();
        if (status == 401 || status == 403) {
            invalidateToken();
            addAuthHeader(cr);
            handle = getNext().handle(cr);
        }

        return handle;
    }

    private void addAuthHeader(ClientRequest cr) {
        if (token == null) {
            token = fetchToken();
        }

        cr.getHeaders().add("X-Authorization", token);
    }

    private String fetchToken() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("login", username);
        params.add("password", password);
        return resource.post(String.class, params);
    }

    private void invalidateToken() {
        token = null;
    }
}
