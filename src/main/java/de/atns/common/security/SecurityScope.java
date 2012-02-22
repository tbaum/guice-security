package de.atns.common.security;

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
        if (values.get() != null) {
            throw new IllegalStateException(String.valueOf("already in security-scope"));
        }
        values.set(new HashMap<Key<?>, Object>());
    }

    public void exit() {
        if (values.get() == null) {
            throw new IllegalStateException(String.valueOf("not in security-scope"));
        }
        values.remove();
    }

    @SuppressWarnings("unchecked") public <T> T get(final Key<T> key) {
        final Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);
        return (T) scopedObjects.get(key);
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(final Key<T> key) {
        final Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return scopedObjects;
    }

    public <T> T get(final Class<T> clazz) {
        return get(Key.get(clazz));
    }

    public <T> void put(final Class<T> clazz, final T value) {
        put(Key.get(clazz), value);
    }

    public <T> void put(final Key<T> key, final T value) {
        final Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);
        boolean expression = !scopedObjects.containsKey(key);
        if (!expression) {
            throw new IllegalStateException(String.valueOf(value));
        }
        scopedObjects.put(key, value);
    }
}
 
