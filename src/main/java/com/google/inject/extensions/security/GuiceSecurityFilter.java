package com.google.inject.extensions.security;

import com.google.inject.Singleton;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mwolter
 * @since 02.05.14
 */
@Singleton
public class GuiceSecurityFilter {
    public static final String HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final Logger LOG = LoggerFactory.getLogger(GuiceSecurityFilter.class);
    private final SecurityService securityService;
    private final SecurityAudit audit;
    private final SecurityTokenService securityTokenService;

    @Inject
    public GuiceSecurityFilter(SecurityService securityService, SecurityAudit audit, SecurityTokenService securityTokenService) {
        this.securityService = securityService;
        this.audit = audit;
        this.securityTokenService = securityTokenService;
    }


    @SecurityScoped
    void handleFilter(ServletRequest rq, ServletResponse rp, FilterChain chain) throws IOException, ServletException {
        try {
            final HttpServletRequest request = (HttpServletRequest) rq;
            HttpServletResponse response = (HttpServletResponse) rp;

            audit.startRequest(request, response);

            try {
                String header = request.getHeader(HEADER_NAME);
                if (header != null && header.startsWith(BEARER_PREFIX)) {
                    String token = header.substring(BEARER_PREFIX.length());
                    SecurityTokenService.ParsedToken user = securityTokenService.validateToken(token);
                    securityService.authenticate(user);
                }
            } catch (JwtException e1) {
                LOG.warn(e1.getMessage(), e1);
            }

            try {
                chain.doFilter(request, response);
            } catch (NotLogginException e) {
                response.setStatus(401);
            } catch (NotInRoleException e) {
                response.setStatus(403);
            }
        } finally {
            audit.finishRequest();
        }
    }
}
