package com.google.inject.extensions.security;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * @author tbaum
 * @since 23.12.2013
 */
public class SimpleUser implements SecurityUser {
    private final Set<Class<? extends SecurityRole>> roles;
    private final String login;
    private String token;

    @SafeVarargs public SimpleUser(String login, Class<? extends SecurityRole>... roles) {
        this.roles = new HashSet<>(asList(roles));
        this.login = login;
    }

    @Override public String getLogin() {
        return login;
    }

    @Override public Set<Class<? extends SecurityRole>> getRoles() {
        return roles;
    }

    @Override public String getToken() {
        return token;
    }

    @Override public void setToken(String token) {
        this.token = token;
    }
}
