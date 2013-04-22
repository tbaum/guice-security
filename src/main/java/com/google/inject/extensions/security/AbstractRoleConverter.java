package com.google.inject.extensions.security;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Singleton public abstract class AbstractRoleConverter implements RoleConverter {
    private Map<String, Class<? extends SecurityRole>> roles = new HashMap<String, Class<? extends SecurityRole>>();
    private Map<Class<? extends SecurityRole>, String> rolesReverse;

    public AbstractRoleConverter() {
        roles.putAll(allRoles());
        rolesReverse = createReversedMap(roles);
    }

    protected abstract Map<String, Class<? extends SecurityRole>> allRoles();

    private Map<Class<? extends SecurityRole>, String> createReversedMap(
            Map<String, Class<? extends SecurityRole>> roles) {
        Map<Class<? extends SecurityRole>, String> rolesReverse = new HashMap<Class<? extends SecurityRole>, String>();
        for (Map.Entry<String, Class<? extends SecurityRole>> entry : roles.entrySet()) {
            rolesReverse.put(entry.getValue(), entry.getKey());
        }
        return rolesReverse;
    }

    @Override public Iterator<String> iterator() {
        return roles.keySet().iterator();
    }

    @Override public Class<? extends SecurityRole> toRole(String role) {
        return roles.get(role);
    }

    @Override public String toString(Class<? extends SecurityRole> role) {
        return rolesReverse.get(role);
    }
}
