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
    private final ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<HttpServletRequest>();
    private final ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<HttpServletResponse>();
    private final SecurityService securityService;
    private final RoleConverter roleConverter;
    private final UserService userService;

    @Inject
    public SecurityFilter(SecurityService securityService, RoleConverter roleConverter, UserService userService) {
        this.securityService = securityService;
        this.roleConverter = roleConverter;
        this.userService = userService;
    }

    @Override public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override @SecurityScoped
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            currentRequest.set(httpServletRequest);

            HttpServletResponse servletResponse = (HttpServletResponse) response;
            currentResponse.set(servletResponse);
            try {
                authenticateToken(httpServletRequest.getHeader(HEADER_NAME));
                authBasicHeader(httpServletRequest);
                authenticateToken(httpServletRequest.getParameter(PARAMETER_NAME));
                authenticateToken(findCookie(httpServletRequest.getCookies()));
            } catch (Exception e) {
                logout();
                servletResponse.setStatus(401);
                return;
            }

            try {
                authFromSession(httpServletRequest);
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

    private void sendHeaders(HttpServletResponse servletResponse) {
        SecurityUser currentUser = securityService.currentUser();
        if (currentUser != null) {
            servletResponse.addHeader("X-Authorized-User", currentUser.getLogin());

            Set<Class<? extends SecurityRole>> allRoles = new HashSet<Class<? extends SecurityRole>>();
            for (Class<? extends SecurityRole> role : currentUser.getRoles()) {
                allRoles.addAll(resolveAll(role));
            }

            for (Class<? extends SecurityRole> r1 : allRoles) {
                servletResponse.addHeader("X-Authorized-Role", roleConverter.toString(r1));
            }
        }
    }

    @Override public void destroy() {
    }

    private void authenticateToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        token = securityService.authenticate(token);
        currentResponse.get().setHeader(HEADER_NAME, token);
    }

    private void authBasicHeader(final HttpServletRequest request) {
        final String auth = request.getHeader("Authorization");

        if (auth == null || auth.toLowerCase().indexOf("basic ") != 0) {
            return;
        }

        String[] u = new String(parseBase64Binary(auth.substring(6))).split(":");
        final SecurityUser user = userService.findUser(u[0], u[1]);
        String token = user == null ? null : securityService.authenticate(user);
        currentResponse.get().setHeader(HEADER_NAME, token);
    }

    private void authFromSession(HttpServletRequest httpServletRequest) {
        final HttpSession session = httpServletRequest.getSession(false);
        if (session == null) {
            return;
        }

        authenticateToken((String) session.getAttribute(SESSION_TOKEN));
    }

    public void setSessionToken(String token) {
        currentRequest.get().getSession(true).setAttribute(SESSION_TOKEN, token);
        currentResponse.get().setHeader(HEADER_NAME, token);
    }

    public void logout() {
        HttpServletRequest request = currentRequest.get();

        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_TOKEN);
        }

        securityService.clearAuthentication();
    }
}
