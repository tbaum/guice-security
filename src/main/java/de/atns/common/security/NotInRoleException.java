package de.atns.common.security;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public class NotInRoleException extends SecurityException {
// ------------------------------ FIELDS ------------------------------

    private List<String> roles;

// --------------------------- CONSTRUCTORS ---------------------------

    public NotInRoleException(Secured secured, final String methodName) {
        super("invalid role to access " + methodName);
        this.roles = asList(secured.value());
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<String> getRoles() {
        return roles;
    }
}
