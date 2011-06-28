package de.atns.common.security;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface SecurityUser {
// -------------------------- OTHER METHODS --------------------------

    String getLogin();

    String getToken();

    boolean hasAccessTo(Secured secured);

    void setToken(String token);
}
