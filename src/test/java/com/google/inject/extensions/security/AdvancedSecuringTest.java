package com.google.inject.extensions.security;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author tbaum
 * @since 23.12.2013
 */
public class AdvancedSecuringTest {

    private A a;
    private SecurityScope securityScope;
    private SecurityService securityService;

    @Before
    public void cleanUp() {
        Injector injector = Guice.createInjector(new TestSecurityModule());

        a = injector.getInstance(A.class);
        securityService = injector.getInstance(SecurityService.class);
        securityScope = injector.getInstance(SecurityScope.class);
    }

    @Test(expected = NotInRoleException.class)
    public void testFailedAuthenticated() {
        try (SecurityScope ignored = securityScope.enter()) {
            securityService.authenticate(new SimpleUser("Foo"));
            a.anyRole("failing");
            fail();
        }
    }

    @Test
    public void testAuthenticated() {
        try (SecurityScope ignored = securityScope.enter()) {
            securityService.authenticate(new SimpleUser("Foo"));
            a.anyRole("allowed");
        }
    }


    public static class A {

        @Secured(decisionMaker = SpecialDecisionMaker.class)
        protected void anyRole(String name) {
        }
    }


    public static class SpecialDecisionMaker extends InRoleDecisionMaker {

        @Override public boolean hasAccessTo(SecurityUser user, Secured secured, MethodInvocation invocation) {
            if (!super.hasAccessTo(user, secured, invocation)) return false;

            Object[] arguments = invocation.getArguments();

            // this depends on the called method!!
            String name = (String) arguments[0];
            return "allowed".equals(name);
        }
    }
}
