package de.atns.common.security;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author mwolter
 * @since 08.12.11 11:28
 */
public interface RoleConverter extends Serializable, Iterable<String> {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Iterable ---------------------

    @Override Iterator<String> iterator();

// -------------------------- OTHER METHODS --------------------------

    Class<? extends SecurityRole> toRole(String role);

    String toString(Class<? extends SecurityRole> role);
}
