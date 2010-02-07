package de.atns.common.security;

import de.atns.common.security.client.SecurityUser;

import java.util.UUID;

/**
 * @author tbaum
 * @since 05.01.2010
 */
public class SecurityUserToken {
// ------------------------------ FIELDS ------------------------------

    private final SecurityUser user;
    private final UUID uuid = UUID.randomUUID();

// --------------------------- CONSTRUCTORS ---------------------------

    public SecurityUserToken(final SecurityUser user) {
        this.user = user;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public SecurityUser getUser() {
        return user;
    }

    public UUID getUuid() {
        return uuid;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override public String toString() {
        return "SecurityUserToken{" +
                "uuid=" + uuid +
                ", user=" + user +
                '}';
    }
}
