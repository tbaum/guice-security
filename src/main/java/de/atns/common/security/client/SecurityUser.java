package de.atns.common.security.client;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public interface SecurityUser {
// -------------------------- OTHER METHODS --------------------------

    boolean hasAccessTo(Secured secured);
}
 