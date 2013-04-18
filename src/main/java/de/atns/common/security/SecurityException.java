package de.atns.common.security;

import java.io.Serializable;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public abstract class SecurityException extends RuntimeException implements Serializable {

    protected SecurityException() {
    }

    protected SecurityException(final String s) {
        super(s);
    }
}
