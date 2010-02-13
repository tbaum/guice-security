package de.atns.common.security.client;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface SecurityUser {
// -------------------------- OTHER METHODS --------------------------

    String getLogin();

    boolean hasAccessTo(Secured secured);
}
 