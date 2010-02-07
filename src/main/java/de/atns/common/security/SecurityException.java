package de.atns.common.security;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public abstract class SecurityException extends RuntimeException {
// --------------------------- CONSTRUCTORS ---------------------------

    protected SecurityException() {
    }

    protected SecurityException(final String s) {
        super(s);
    }
}
