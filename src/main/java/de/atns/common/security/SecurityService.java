package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.atns.common.security.client.SecurityUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.atns.common.security.AuthenticateFilter.SESSION_UUID;

/**
 * @author tbaum
 * @since 16.12.2009
 */
@Singleton public class SecurityService {
// ------------------------------ FIELDS ------------------------------

    private static final Log LOG = LogFactory.getLog(SecurityService.class);
    private final SecurityScope securityScope;
    private final UserService userService;
    private final Map<UUID, SecurityUserToken> cache = new HashMap<UUID, SecurityUserToken>();

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject public SecurityService(final UserService userService, final SecurityScope securityScope) {
        this.userService = userService;
        this.securityScope = securityScope;
    }

// -------------------------- OTHER METHODS --------------------------

    public void authFromHeader(final ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest servletRequest = (HttpServletRequest) request;
            final String uuid = servletRequest.getHeader("X-Authorization");
            if (uuid != null && !uuid.isEmpty()) {
                LOG.debug("using header-token " + uuid);
            }
            authFromCookie(uuid);
        }
    }

    private void authFromCookie(final String uuid) {
        if (uuid != null && !uuid.isEmpty()) {
            final SecurityUserToken user = cache.get(UUID.fromString(uuid.replaceAll("[^0-9a-z-]", "")));
            if (user != null) {
                securityScope.put(SecurityUser.class, user.getUser());
            } else {
                LOG.warn("no securit-token found in cache for " + uuid);
            }
        }
    }                                        

    public void authFromSession(final ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest servletRequest = (HttpServletRequest) request;

            final HttpSession session = servletRequest.getSession(false);
            if (session != null) {
                final String uuid = (String) session.getAttribute(SESSION_UUID);
                if (uuid != null && !uuid.isEmpty()) {
                    LOG.debug("using session-var " + uuid);
                }

                authFromCookie(uuid);
            }
        }
    }

    public SecurityUserToken login(final String login, final String password) {
        LOG.debug("login user=" + login);
        final SecurityUser user = userService.findUser(login, password);
        final SecurityUserToken token = new SecurityUserToken(user);
        cache.put(token.getUuid(), token);
        return token;
    }
}
