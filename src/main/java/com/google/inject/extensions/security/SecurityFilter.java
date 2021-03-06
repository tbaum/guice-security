package com.google.inject.extensions.security;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.*;
import java.io.IOException;

/**
 * @author tbaum
 * @since 30.09.2009
 */
@Singleton
public class SecurityFilter implements Filter {

    public static final String INJECTOR = SecurityFilter.class.getCanonicalName();
    //    public static final String HEADER_NAME = "X-Authorization";
    private final boolean useInit;

    @Inject
    private GuiceSecurityFilter guiceSecurityFilter;

    @Inject
    public SecurityFilter(GuiceSecurityFilter guiceSecurityFilter) {
        useInit = false;
        this.guiceSecurityFilter = guiceSecurityFilter;
    }

    @SuppressWarnings("UnusedDeclaration")
    public SecurityFilter() {
        useInit = true;
    }

    @Override
    public void init(FilterConfig config) {
        if (useInit) {
            ServletContext context = config.getServletContext();
            Injector injector = (Injector) context.getAttribute(INJECTOR);
            injector.injectMembers(this);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        guiceSecurityFilter.handleFilter(request, response, chain);
    }

    @Override
    public void destroy() {
    }
}
