package com.google.inject.extensions.security;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public class SecurityScope implements Scope {

    public static final Key<SecurityUser> KEY = Key.get(SecurityUser.class);
    private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<Map<Key<?>, Object>>();

    @Override public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            @Override public T get() {
                final Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

                @SuppressWarnings("unchecked")
                T current = (T) scopedObjects.get(key);
                if (current == null && !scopedObjects.containsKey(key)) {
                    current = unscoped.get();
                    scopedObjects.put(key, current);
                }
                return current;
            }
        };
    }

    public void enter() {
        if (inScope()) {
            throw new IllegalStateException("already in a security-scope block");
        }
        values.set(new HashMap<Key<?>, Object>());
    }

    public boolean inScope() {
        return values.get() != null;
    }

    public void exit() {
        if (!inScope()) {
            throw new IllegalStateException("outside of a security-scope block");
        }
        values.remove();
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(final Key<T> key) {
        final Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a security-scope block");
        }
        return scopedObjects;
    }

    public SecurityUser get() {
        final Map<Key<?>, Object> scopedObjects = getScopedObjectMap(KEY);
        return (SecurityUser) scopedObjects.get(KEY);
    }

    public void put(SecurityUser value) {
        final Map<Key<?>, Object> scopedObjects = getScopedObjectMap(KEY);
        scopedObjects.put(KEY, value);
    }
}
