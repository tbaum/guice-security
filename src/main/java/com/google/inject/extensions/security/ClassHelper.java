package com.google.inject.extensions.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassHelper {

    private static final Map<Class<? extends SecurityRole>, Set<Class<? extends SecurityRole>>> cache = new HashMap<>();

    public static Set<Class<? extends SecurityRole>> resolveAll(Set<Class<? extends SecurityRole>> roles) {
        Set<Class<? extends SecurityRole>> allRoles = new HashSet<>();
        if (roles != null) {
            for (Class<? extends SecurityRole> role : roles) {
                allRoles.addAll(resolveAll(role));
            }
        }
        return allRoles;
    }

    public static Set<Class<? extends SecurityRole>> resolveAll(Class<? extends SecurityRole> aClass) {
        if (cache.containsKey(aClass)) return cache.get(aClass);

        Set<Class<? extends SecurityRole>> classes = resolveAll(aClass, new HashSet<Class<? extends SecurityRole>>());
        cache.put(aClass, classes);
        return classes;
    }

    private static Set<Class<? extends SecurityRole>> resolveAll(Class<? extends SecurityRole> aClass,
                                                                 HashSet<Class<? extends SecurityRole>> allRoles) {
        if (allRoles.contains(aClass) || aClass == SecurityRole.class) {
            return allRoles;
        }
        allRoles.add(aClass);
        for (Class<?> aClass1 : aClass.getInterfaces()) {
            if (SecurityRole.class.isAssignableFrom(aClass1)) {
                //noinspection unchecked
                resolveAll((Class<? extends SecurityRole>) aClass1, allRoles);
            }
        }
        return allRoles;
    }
}
