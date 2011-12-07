package de.atns.common.security;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public class NotInRoleException extends SecurityException implements IsSerializable {
// ------------------------------ FIELDS ------------------------------

    private List<String> roles = new ArrayList<String>();

// --------------------------- CONSTRUCTORS ---------------------------

    public NotInRoleException(final Secured secured, final String methodName) {
        super("invalid role to access " + methodName);
        for (Class<? extends SecurityRole> role : secured.value()) {
            roles.add(role.getSimpleName());
        }
    }

    public NotInRoleException() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<String> getRoles() {
        return roles;
    }
}
