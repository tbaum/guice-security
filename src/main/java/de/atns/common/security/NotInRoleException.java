package de.atns.common.security;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public class NotInRoleException extends SecurityException implements IsSerializable {

    private List<String> roles = new ArrayList<String>();

    public NotInRoleException() {
    }

    public NotInRoleException(final String methodName, List<String> roles) {
        super("invalid role to access " + methodName);
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }
}
