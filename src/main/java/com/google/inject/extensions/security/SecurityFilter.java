package com.google.inject.extensions.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import static com.google.inject.extensions.security.ClassHelper.resolveAll;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

/**
 * @author tbaum
 * @since 30.09.2009
 */
@Singleton public class SecurityFilter implements Filter {

    public static final String HEADER_NAME = "X-Authorization";
    private static final String SESSION_TOKEN = "_SECURITY_UUID";
    private static final String PARAMETER_NAME = "_SECURITY_UUID";
    private static final String COOKIE_NAME = "_SECURITY_UUID";
    private final ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();
    private final ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<>();
    private final SecurityService securityService;
    private final RoleConverter roleConverter;
    private final UserService userService;
    private final SecurityAudit audit;

    @Inject
    public SecurityFilter(SecurityService securityService, RoleConverter roleConverter, UserService userService,
                          SecurityAudit audit) {
        this.securityService = securityService;
        this.roleConverter = roleConverter;
        this.userService = userService;
        this.audit = audit;
    }

    @Override public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override @SecurityScoped
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            final HttpServletRequest servletRequest = (HttpServletRequest) request;
            currentRequest.set(servletRequest);

            final HttpServletResponse servletResponse = (HttpServletResponse) response;
            currentResponse.set(servletResponse);

            audit.startRequest(servletRequest, servletResponse);

            try {
                authenticateToken(servletRequest.getHeader(HEADER_NAME), servletResponse);
                authBasicHeader(servletRequest, servletResponse);
                authenticateToken(servletRequest.getParameter(PARAMETER_NAME), servletResponse);
                authenticateToken(findCookie(servletRequest.getCookies()), servletResponse);
            } catch (Exception e) {
                logout();
            }

            try {
                authFromSession(servletRequest, servletResponse);
            } catch (Exception e) {
                logout();
            }

            sendHeaders(servletResponse);

            try {
                chain.doFilter(request, response);
            } catch (NotLogginException e) {
                servletResponse.setStatus(401);
            } catch (NotInRoleException e) {
                servletResponse.setStatus(403);
            }
        } finally {
            audit.finishRequest();
            currentRequest.remove();
            currentResponse.remove();
        }
    }

    private String findCookie(Cookie[] cookies) throws UnsupportedEncodingException {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return URLDecoder.decode(cookie.getValue(), "UTF-8");
            }
        }
        return null;
    }

    private void sendHeaders(HttpServletResponse response) {
        SecurityUser currentUser = securityService.currentUser();
        if (currentUser != null) {
            response.addHeader("X-Authorized-User", currentUser.getLogin());

            Set<Class<? extends SecurityRole>> allRoles = new HashSet<>();
            for (Class<? extends SecurityRole> role : currentUser.getRoles()) {
                allRoles.addAll(resolveAll(role));
            }

            for (Class<? extends SecurityRole> r1 : allRoles) {
                response.addHeader("X-Authorized-Role", roleConverter.toString(r1));
            }
        }
    }

    @Override public void destroy() {
    }

    private void authenticateToken(String token, HttpServletResponse response) {
        if (token == null || token.isEmpty()) {
            return;
        }
        token = securityService.authenticate(token);
        response.setHeader(HEADER_NAME, token);
    }

    private void authBasicHeader(final HttpServletRequest request, HttpServletResponse response) {
        final String auth = request.getHeader("Authorization");

        if (auth == null || auth.toLowerCase().indexOf("basic ") != 0) {
            return;
        }

        String[] u = new String(parseBase64Binary(auth.substring(6))).split(":");
        final SecurityUser user = userService.findUser(u[0], u[1]);
        String token = user == null ? null : securityService.authenticate(user);
        response.setHeader(HEADER_NAME, token);
    }

    private void authFromSession(HttpServletRequest request, HttpServletResponse response) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        final String sessionToken = (String) session.getAttribute(SESSION_TOKEN);
        authenticateToken(sessionToken, response);
    }

    public void setSessionToken(String token) {
        currentRequest.get().getSession(true).setAttribute(SESSION_TOKEN, token);
        currentResponse.get().setHeader(HEADER_NAME, token);
    }

    public void logout() {
        final HttpSession session = currentRequest.get().getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_TOKEN);
        }
        securityService.clearAuthentication();
    }
}
