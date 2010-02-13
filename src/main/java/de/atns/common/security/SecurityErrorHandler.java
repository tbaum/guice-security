package de.atns.common.security;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael Hunger
 * @since 24.01.2010
 */
@ImplementedBy(SecurityErrorHandler.Default.class)
public interface SecurityErrorHandler {
// -------------------------- OTHER METHODS --------------------------

    void handleException(SecurityException e);

// -------------------------- INNER CLASSES --------------------------

    @Singleton class Default implements SecurityErrorHandler {
        final Log LOG = LogFactory.getLog(SecurityErrorHandler.class);

        @Override public void handleException(final SecurityException e) {
            LOG.error(e, e);
        }
    }
}
