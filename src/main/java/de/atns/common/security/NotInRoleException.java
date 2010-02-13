package de.atns.common.security;

import de.atns.common.security.client.Secured;

import java.lang.reflect.Method;
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

    public NotInRoleException(final Method method, Secured secured) {
        super("invalid role to access " + method.toString());
        this.roles = asList(secured.value());
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<String> getRoles() {
        return roles;
    }
}
