package de.atns.common.security.client;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tbaum
 * @since 25.10.2009
 */
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
public @interface Secured {
// -------------------------- OTHER METHODS --------------------------

    public abstract String[] value() default {};
}
