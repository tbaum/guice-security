package com.google.inject.extensions.security;

import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author tbaum
 * @since 23.12.2013
 */
public class InRoleDecisionMakerTest {

    @Test
    public void testFailingAuthenticatedSpecial() {
        InRoleDecisionMaker maker = new InRoleDecisionMaker();

        TestSecured secured = new TestSecured();
        TestSecured securedSpecialRole = new TestSecured(SpecialRole.class);
        TestSecured securedExtendedRole = new TestSecured(ExtendedRole.class);

        SimpleUser simpleUser = new SimpleUser("");
        assertTrue(maker.hasAccessTo(simpleUser, secured, null));
        assertFalse(maker.hasAccessTo(simpleUser, securedSpecialRole, null));
        assertFalse(maker.hasAccessTo(simpleUser, securedExtendedRole, null));

        SimpleUser specialUser = new SimpleUser("", SpecialRole.class);
        assertTrue(maker.hasAccessTo(specialUser, secured, null));
        assertTrue(maker.hasAccessTo(specialUser, securedSpecialRole, null));
        assertFalse(maker.hasAccessTo(specialUser, securedExtendedRole, null));

        SimpleUser extendedUser = new SimpleUser("", ExtendedRole.class);
        assertTrue(maker.hasAccessTo(extendedUser, secured, null));
        assertTrue(maker.hasAccessTo(extendedUser, securedSpecialRole, null));
        assertTrue(maker.hasAccessTo(extendedUser, securedExtendedRole, null));
    }

    public interface SpecialRole extends SecurityRole {
    }

    public interface ExtendedRole extends SpecialRole {

    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class TestSecured implements Secured {

        private Class<? extends SecurityRole>[] roles;

        @SafeVarargs private TestSecured(Class<? extends SecurityRole>... roles) {
            this.roles = roles;
        }

        @Override public Class<? extends SecurityRole>[] value() {
            return roles;
        }

        @Override public Class<? extends SecurityDecisionMaker> decisionMaker() {
            return InRoleDecisionMaker.class;
        }

        @Override public Class<? extends Annotation> annotationType() {
            return Secured.class;
        }
    }
}
