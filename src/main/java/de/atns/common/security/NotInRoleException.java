package de.atns.common.security;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public class NotInRoleException extends SecurityException implements IsSerializable {
// ------------------------------ FIELDS ------------------------------

    private  List<String> roles;

// --------------------------- CONSTRUCTORS ---------------------------

    public NotInRoleException(final Secured secured, final String methodName) {
        super("invalid role to access " + methodName);
        this.roles = asList(secured.value());
    }

    public NotInRoleException() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<String> getRoles() {
        return roles;
    }
}
