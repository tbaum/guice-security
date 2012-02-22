package de.atns.common.security;

import net.customware.gwt.dispatch.shared.ServiceException;

/**
 * @author tbaum
 * @since 27.11.2009
 */
public abstract class SecurityException extends ServiceException {

    protected SecurityException() {
    }

    protected SecurityException(final String s) {
        super(s);
    }
}
