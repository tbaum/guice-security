package com.google.inject.extensions.security.jersey;

import com.google.inject.extensions.security.SecurityEndpoint;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;


/**
 * @author tbaum
 * @since 28.04.2014
 */
public class SecurityFeature implements Feature {

    @Override public boolean configure(FeatureContext context) {
        context.register(SecurityEndpoint.class);
        context.register(SecurityNotInRoleExceptionMapper.class);
        context.register(SecurityNotLogginExceptionMapper.class);
        context.register(new SecurityBinder());
        return true;
    }
}
