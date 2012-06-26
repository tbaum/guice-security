package de.atns.common.security;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author tbaum
 * @since 25.10.2009
 */
@Retention(RUNTIME) @Target({METHOD, TYPE})
public @interface Secured {

    public abstract Class<? extends SecurityRole>[] value() default {};
}
