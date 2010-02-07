package de.atns.common.security;

import de.atns.common.security.client.SecurityUser;

import java.util.List;

/**
 * @author Michael Hunger
 * @since 24.01.2010
 */
public interface SecurityErrorHandler {
// -------------------------- OTHER METHODS --------------------------

    void notInRole(SecurityUser user, List<String> roles);

    void notLoggedIn();
}
