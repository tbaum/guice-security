package de.atns.common.security;

import com.google.inject.ImplementedBy;

import java.util.UUID;

/**
 * @author tbaum
 * @since 16.12.2009
 */
@ImplementedBy(SecurityServiceImpl.class)
public interface SecurityService {

    SecurityUser authenticate(UUID uuid);

    SecurityUser currentUser();

    UUID login(String login, String password);

    UUID login(SecurityUser securityUser);

    SecurityUser logout();
}
