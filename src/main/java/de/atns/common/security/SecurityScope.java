package de.atns.common.security;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.Maps;

import java.util.Map;

import static com.google.inject.internal.Preconditions.checkState;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public class SecurityScope implements Scope {
// ------------------------------ FIELDS ------------------------------

    private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<Map<Key<?>, Object>>();

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Scope ---------------------

    @Override public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            @Override public T get() {
                Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

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

// -------------------------- OTHER METHODS --------------------------

    public void enter() {
        checkState(values.get() == null, "already in security-scope");
        values.set(Maps.<Key<?>, Object>newHashMap());
    }

    public void exit() {
        checkState(values.get() != null, "not in security-scope");
        values.remove();
    }

    @SuppressWarnings("unchecked") public <T> T get(Key<T> key) {
        Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);
        return (T) scopedObjects.get(key);
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return scopedObjects;
    }

    public <T> T get(Class<T> clazz) {
        return get(Key.get(clazz));
    }

    public <T> void put(Class<T> clazz, T value) {
        put(Key.get(clazz), value);
    }

    public <T> void put(Key<T> key, T value) {
        Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);
        checkState(!scopedObjects.containsKey(key), "A value for the key %s was already seeded in this scope. " +
                "Old value: %s New value: %s", key, scopedObjects.get(key), value);
        scopedObjects.put(key, value);
    }
}
 
