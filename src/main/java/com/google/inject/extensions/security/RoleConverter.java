package com.google.inject.extensions.security;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author mwolter
 * @since 08.12.11 11:28
 */
public interface RoleConverter extends Serializable, Iterable<String> {

    @Override Iterator<String> iterator();

    Class<? extends SecurityRole> toRole(String role);

    String toString(Class<? extends SecurityRole> role);
}
