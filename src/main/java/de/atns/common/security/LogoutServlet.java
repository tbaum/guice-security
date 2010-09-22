package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tbaum
 * @since 28.10.2009 21:39:05
 */
@Singleton public class LogoutServlet extends HttpServlet {
// ------------------------------ FIELDS ------------------------------

    private final SecurityFilter securityFilter;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject public LogoutServlet(final SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) {
        securityFilter.logout();
    }
}
