package com.google.inject.extensions.security.jersey;

import com.google.inject.extensions.security.NotInRoleException;
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
@Provider
@Singleton
public class SecurityNotInRoleExceptionMapper implements ExceptionMapper<NotInRoleException> {

    private final Logger LOG = LoggerFactory.getLogger(SecurityNotInRoleExceptionMapper.class);

    @Override
    public Response toResponse(NotInRoleException exception) {
        LOG.debug(exception.getMessage());
        return status(403).build();
    }
}
