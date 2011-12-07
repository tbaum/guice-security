package de.atns.common.security;

import com.google.inject.Inject;
import net.customware.gwt.dispatch.shared.Result;

/**
 * @author mwolter
 * @since 01.07.11 12:05
 */
public class SecurityRolePresentation implements Result, Comparable<SecurityRolePresentation> {
// ------------------------------ FIELDS ------------------------------

    @Inject
    private static RoleConverter roleConverter;
    private String role;

// --------------------------- CONSTRUCTORS ---------------------------

    protected SecurityRolePresentation() {
    }

    public SecurityRolePresentation(Class<? extends SecurityRole> role) {
        this.role = roleConverter.toString(role);
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Comparable ---------------------

    @Override public int compareTo(SecurityRolePresentation r2) {
        return role.compareTo(r2.role);
    }

// -------------------------- OTHER METHODS --------------------------

    public Class<? extends SecurityRole> getRole() {
        return roleConverter.toRole(role);
    }
}
