package com.google.inject.extensions.security.jersey;

import com.google.inject.extensions.security.NotLogginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.status;

/**
 * @author tbaum
 * @since 19.03.2014
 */
@Provider @Singleton
public class SecurityNotLogginExceptionMapper implements ExceptionMapper<NotLogginException> {

    private final Logger LOG = LoggerFactory.getLogger(SecurityNotLogginExceptionMapper.class);

    @Override
    public Response toResponse(NotLogginException exception) {
        LOG.warn(exception.getMessage(), exception);
        return status(401).build();
    }
}
