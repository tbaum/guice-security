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

/**
 * @author tbaum
 * @since 21.10.2009
 */
@Singleton
public class AuthenticateFilter implements Filter {
// ------------------------------ FIELDS ------------------------------

    static final String SESSION_USER = "_SECURITY_USER";
    static final String SESSION_UUID = "_SECURITY_UUID";
    private static final Log LOG = LogFactory.getLog(AuthenticateFilter.class);
    private final SecurityService securityService;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public AuthenticateFilter(final SecurityService securityService) {
        this.securityService = securityService;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Filter ---------------------

    @Override public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override public void doFilter(final ServletRequest request,
                                   final ServletResponse response,
                                   final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        final String path = req.getRequestURI();

        if (path.contains("_security_login")) {
            LOG.debug("perfom login");
            final SecurityUserToken token = securityService.login(req.getParameter("login"), req.getParameter("password"));
            LOG.debug("token =" + token.getUuid());

            req.getSession(true).setAttribute(SESSION_USER, token.getUser());
            req.getSession(true).setAttribute(SESSION_UUID, token.getUuid().toString());
            ((HttpServletResponse) response).setHeader("X-Authorization", token.getUuid().toString());
        } else if (path.contains("_security_logout")) {
            LOG.debug("perfom logout");
            final HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute(SESSION_USER);
                session.removeAttribute(SESSION_UUID);
            }
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() {
    }
}