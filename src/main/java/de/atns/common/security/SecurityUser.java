package de.atns.common.security;

import java.util.Set;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface SecurityUser {

    String getLogin();

    Set<Class<? extends SecurityRole>> getRoles();

    String getToken();

    boolean hasAccessTo(Secured secured);

    void setToken(String token);
}
