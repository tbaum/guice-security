package de.atns.common.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tbaum
 * @since 28.10.2009 21:39:05
 */
@Singleton public class LoginServlet extends HttpServlet {

    private final SecurityFilter securityFilter;

    @Inject public LoginServlet(final SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Override protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final String login = req.getParameter("login");
        final String password = req.getParameter("password");

        securityFilter.login(login, password);
    }
}
