package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.atns.common.cache.TimeoutCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

import static java.util.UUID.randomUUID;

/**
 * @author tbaum
 * @since 16.12.2009
 */
@Singleton public class SecurityServiceImpl implements SecurityService {

    private static final Log LOG = LogFactory.getLog(SecurityServiceImpl.class);

    private final TimeoutCache<UUID, SecurityUser> cache = new TimeoutCache<UUID, SecurityUser>(1800000);

    private final SecurityScope securityScope;
    private final UserService userService;

    @Inject public SecurityServiceImpl(final UserService userService, final SecurityScope securityScope) {
        this.userService = userService;
        this.securityScope = securityScope;
    }

    @Override public SecurityUser authenticate(final UUID uuid) {
        final SecurityUser user = cache.get(uuid);
        if (user != null) {
            cache.put(uuid, user);
            user.setToken(uuid.toString());
            securityScope.put(SecurityUser.class, user);
            return user;
        }
        LOG.warn("no securit-token found in cache for " + uuid);
        return null;
    }

    @Override public SecurityUser currentUser() {
        return securityScope.get(SecurityUser.class);
    }

    @Override public UUID login(final String login, final String password) {
        LOG.debug("login user=" + login);
        final SecurityUser user = userService.findUser(login, password);
        if (user == null) {
            return null;
        }

        return login(user);
    }

    @Override public UUID login(SecurityUser user) {
        final UUID uuid = randomUUID();
        cache.put(uuid, user);
        user.setToken(uuid.toString());

        // clear old user value
        securityScope.exit();
        securityScope.enter();

        securityScope.put(SecurityUser.class, user);
        return uuid;
    }

    @Override public SecurityUser logout() {
        final SecurityUser user = currentUser();
        cache.removeValue(user);
        return user;
    }
}
