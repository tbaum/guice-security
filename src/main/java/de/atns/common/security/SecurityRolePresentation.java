package de.atns.common.security;

import com.google.inject.Inject;
import net.customware.gwt.dispatch.shared.Result;

/**
 * @author mwolter
 * @since 01.07.11 12:05
 */
public class SecurityRolePresentation implements Result, Comparable<SecurityRolePresentation> {

    @Inject private static RoleConverter roleConverter;
    private String role;

    protected SecurityRolePresentation() {
    }

    public SecurityRolePresentation(Class<? extends SecurityRole> role) {
        this.role = roleConverter.toString(role);
    }

    @Override public int compareTo(SecurityRolePresentation r2) {
        return role.compareTo(r2.role);
    }

    public Class<? extends SecurityRole> getRole() {
        return roleConverter.toRole(role);
    }

    public String getRoleName() {
        return role;
    }
}
