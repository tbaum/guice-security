package com.google.inject.extensions.security;

import com.google.inject.Singleton;
import com.google.inject.extensions.security.filter.AuthFilterPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.*;

/**
 * @author mwolter
 * @since 02.05.14
 */
@Singleton
public class GuiceSecurityFilter {
    private static final Logger LOG = LoggerFactory.getLogger(GuiceSecurityFilter.class);
    private final SecurityService securityService;
    private final SecurityAudit audit;
    private final Set<AuthFilterPlugin> filters;

    @Inject public GuiceSecurityFilter(SecurityService securityService, SecurityAudit audit, Set<AuthFilterPlugin> filters) {
        this.securityService = securityService;
        this.audit = audit;
        this.filters = filters;
    }

    @SecurityScoped void handleFilter(ServletRequest rq, ServletResponse rp, FilterChain chain) throws IOException, ServletException {
        try {
            final HttpServletRequest request = (HttpServletRequest) rq;
            final HttpServletResponseHeaderWrapper response = new HttpServletResponseHeaderWrapper(rp);

            audit.startRequest(request, response);

            for (AuthFilterPlugin ca : filters) {
                try {
                    if (ca.authenticate(request, response)) break;
                } catch (Exception e) {
                    LOG.warn("should not happen: {}", e.getMessage(), e);
                    securityService.clearAuthentication();
                }
            }

            for (AuthFilterPlugin filter : filters) {
                filter.postAuth(request, response);
            }

            response.flushHeaders();
            try {
                chain.doFilter(rq, rp);
            } catch (NotLogginException e) {
                response.setStatus(401);
            } catch (NotInRoleException e) {
                response.setStatus(403);
            }
        } finally {
            audit.finishRequest();
        }
    }

    private static class HttpServletResponseHeaderWrapper extends HttpServletResponseWrapper {
        private final Map<String, List<String>> headers = new HashMap<>();
        private final HttpServletResponse response;

        public HttpServletResponseHeaderWrapper(ServletResponse response) {
            this((HttpServletResponse) response);
        }

        public HttpServletResponseHeaderWrapper(HttpServletResponse response) {
            super(response);
            this.response = response;
        }

        void flushHeaders() {
            for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                for (String value : header.getValue()) {
                    response.addHeader(header.getKey(), value);
                }
            }
        }

        @Override public void setHeader(String name, String value) {
            headers.remove(name);
            if (value != null) addHeader(name, value);
        }

        @Override public void addHeader(String name, String value) {
            List<String> values;
            if (headers.containsKey(name)) {
                values = headers.get(name);
            } else {
                headers.put(name, values = new ArrayList<>());
            }
            values.add(value);
        }
    }
}
