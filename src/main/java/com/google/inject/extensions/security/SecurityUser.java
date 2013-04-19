package com.google.inject.extensions.security;

import java.util.Set;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface SecurityUser {

    String getLogin();

    Set<Class<? extends SecurityRole>> getRoles();

    String getToken();

    void setToken(String token);

    boolean hasAccessTo(Secured secured);
}
