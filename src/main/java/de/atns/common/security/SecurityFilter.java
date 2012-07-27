package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

import static java.util.UUID.fromString;

/**
 * @author tbaum
 * @since 30.09.2009
 */
@Singleton public class SecurityFilter implements Filter {

    public static final String HEADER_NAME = "X-Authorization";

    private static final Log LOG = LogFactory.getLog(SecurityFilter.class);
    private static final String SESSION_UUID = "_SECURITY_UUID";
    private static final String PARAMETER_NAME = "_SECURITY_UUID";

    private final ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<HttpServletRequest>();
    private final ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<HttpServletResponse>();

    private final SecurityScope securityScope;
    private final SecurityService securityService;

    @Inject public SecurityFilter(final SecurityScope securityScope, final SecurityService securityService) {
        this.securityScope = securityScope;
        this.securityService = securityService;
    }

    @Override public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override public void doFilter(final ServletRequest request, final ServletResponse response,
                                   final FilterChain chain) throws IOException, ServletException {
        securityScope.enter();
        try {
            if (request instanceof HttpServletRequest) {
                currentRequest.set((HttpServletRequest) request);
                currentResponse.set((HttpServletResponse) response);

                authFromHeader((HttpServletRequest) request);
                authFromParameter((HttpServletRequest) request);
                authFromSession();
            }
            try {
                chain.doFilter(request, response);
            } catch (ServletException e) {
                Throwable cause = e.getRootCause();
                if (cause instanceof NotLogginException) {
                    ((HttpServletResponse) response).setStatus(401);
                } else if (cause instanceof NotInRoleException) {
                    ((HttpServletResponse) response).setStatus(403);
                } else throw e;
            }
        } finally {
            currentRequest.remove();
            currentResponse.remove();
            securityScope.exit();
        }
    }

    @Override public void destroy() {
    }

    private void authFromHeader(final HttpServletRequest request) {
        final String uuid = request.getHeader(HEADER_NAME);
        if (uuid != null && !uuid.isEmpty()) {
            securityService.authenticate(fromString(uuid.replaceAll("[^0-9a-z-]", "")));
        }
    }

    private void authFromParameter(final HttpServletRequest request) {
        final String uuid = request.getParameter(PARAMETER_NAME);
        if (uuid != null && !uuid.isEmpty()) {
            securityService.authenticate(fromString(uuid.replaceAll("[^0-9a-z-]", "")));
        }
    }

    private void authFromSession() {
        final UUID uuid = getAuthToken();
        if (uuid != null) {
//            LOG.debug("session " + uuid);
            securityService.authenticate(uuid);
        }
    }

    public UUID getAuthToken() {
        final HttpSession session = currentRequest.get().getSession(false);
        if (session != null) {
            return (UUID) session.getAttribute(SESSION_UUID);
        }
        return null;
    }

    public UUID login(final String login, final String password) {
        final UUID token = securityService.login(login, password);
        authenticate(token);
        return token;
    }

    public void authenticate(final UUID token) {
        if (token == null) {
            final HttpSession session = currentRequest.get().getSession(false);
            if (session != null) {
                session.removeAttribute(SESSION_UUID);
            }
        } else {
            final HttpSession session = currentRequest.get().getSession(true);
            session.setAttribute(SESSION_UUID, token);
            currentResponse.get().setHeader(HEADER_NAME, token.toString());
        }
    }

    public void logout() {
        securityService.logout();
        currentRequest.get().removeAttribute(SESSION_UUID);
    }
}
