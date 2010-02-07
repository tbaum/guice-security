package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.atns.common.security.client.SecurityUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author tbaum
 * @since 30.09.2009
 */
@Singleton public class SecurityFilter implements Filter {
// ------------------------------ FIELDS ------------------------------

    private static final Log LOG = LogFactory.getLog(SecurityFilter.class);

    private final SecurityScope securityScope;
    private final SecurityService securityService;
    private final SecurityErrorHandler errorHandler;
    private final Provider<SecurityUser> user;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject public SecurityFilter(final SecurityScope securityScope, final SecurityService securityService, final SecurityErrorHandler errorHandler, final Provider<SecurityUser> user) {
        this.securityScope = securityScope;
        this.securityService = securityService;
        this.errorHandler = errorHandler;
        this.user = user;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Filter ---------------------

    @Override public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override public void doFilter(final ServletRequest request,
                                   final ServletResponse response,
                                   final FilterChain chain) throws IOException, ServletException {
        securityScope.enter();
        try {
            securityService.authFromHeader(request);
            securityService.authFromSession(request);

            try {
                chain.doFilter(request, response);
            } catch (NotInRoleException e) {
                LOG.error("User "+user.get()+" not in role "+e.getRoles());
                errorHandler.notInRole(user.get(),e.getRoles());
            } catch (NotLogginException e) {
                LOG.error("Not logged in for "+((HttpServletRequest)request).getRequestURI());
                errorHandler.notLoggedIn();
            }
        } finally {
            securityScope.exit();
        }
    }

    @Override public void destroy() {
    }
}