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

    private final SecurityFilter securityFilter;

    @Inject public LogoutServlet(final SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Override protected void service(final HttpServletRequest req, final HttpServletResponse resp) {
        securityFilter.logout();
    }
}
