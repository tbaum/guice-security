package com.google.inject.extensions.security.jersey;

import com.google.inject.extensions.security.Secured;
import com.google.inject.extensions.security.SecurityInterceptor;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @author tbaum
 * @since 19.03.2014
 */
@Service
public class SecurityInterceptionService implements InterceptionService {

    private final List<MethodInterceptor> interceptor;

    @Inject
    public SecurityInterceptionService(SecurityInterceptor securityInterceptor) {
        interceptor = singletonList(securityInterceptor);
    }

    public Filter getDescriptorFilter() {
        return BuilderHelper.allFilter();
    }

    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (method.isAnnotationPresent(Secured.class)) {
            return interceptor;
        }
        return null;
    }

    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        return null;
    }
}