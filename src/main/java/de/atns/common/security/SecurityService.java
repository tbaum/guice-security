package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.atns.common.cache.TimeoutCache;
import de.atns.common.security.client.SecurityUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

import static java.util.UUID.randomUUID;

/**
 * @author tbaum
 * @since 16.12.2009
 */
@Singleton public class SecurityService {
// ------------------------------ FIELDS ------------------------------

    private static final Log LOG = LogFactory.getLog(SecurityService.class);

    private final TimeoutCache<UUID, SecurityUser> cache = new TimeoutCache<UUID, SecurityUser>(1800000);

    private final SecurityScope securityScope;
    private final UserService userService;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject public SecurityService(final UserService userService, final SecurityScope securityScope) {
        this.userService = userService;
        this.securityScope = securityScope;
    }

// -------------------------- OTHER METHODS --------------------------

    public SecurityUser authenticate(final UUID uuid) {
        final SecurityUser user = cache.get(uuid);
        if (user != null) {
            cache.put(uuid, user);
            securityScope.put(SecurityUser.class, user);
            return user;
        }
        LOG.warn("no securit-token found in cache for " + uuid);
        return null;
    }

    public UUID login(final String login, final String password) {
        LOG.debug("login user=" + login);
        final SecurityUser user = userService.findUser(login, password);
        if (user == null) {
            return null;
        }

        final UUID uuid = randomUUID();
        cache.put(uuid, user);
        securityScope.put(SecurityUser.class, user);
        return uuid;
    }

    public SecurityUser logout() {
        SecurityUser user = securityScope.get(SecurityUser.class);
        cache.removeValue(user);
        return user;
    }

}
