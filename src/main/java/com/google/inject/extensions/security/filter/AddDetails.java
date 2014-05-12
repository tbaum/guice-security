package com.google.inject.extensions.security.filter;

import com.google.inject.extensions.security.RoleConverter;
import com.google.inject.extensions.security.SecurityRole;
import com.google.inject.extensions.security.SecurityService;
import com.google.inject.extensions.security.SecurityUser;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.google.inject.extensions.security.ClassHelper.resolveAll;

/**
 * @author tbaum
 * @since 12.05.2014
 */
public class AddDetails implements AuthFilterPlugin {

    private final SecurityService securityService;
    private final RoleConverter roleConverter;

    @Inject public AddDetails(SecurityService securityService, RoleConverter roleConverter) {
        this.securityService = securityService;
        this.roleConverter = roleConverter;
    }

    @Override public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        return false;
    }

    @Override public void postAuth(HttpServletRequest request, HttpServletResponse response) {
        SecurityUser currentUser = securityService.currentUser();
        response.setHeader("X-Authorized-User", null);
        response.setHeader("X-Authorized-Role", "foo!");
        response.setHeader("X-Authorized-Role", null);

        if (currentUser != null) {
            response.addHeader("X-Authorized-User", currentUser.getLogin());

            for (Class<? extends SecurityRole> role : resolveAll(currentUser.getRoles())) {
                response.addHeader("X-Authorized-Role", roleConverter.toString(role));
            }
        }
    }
}
