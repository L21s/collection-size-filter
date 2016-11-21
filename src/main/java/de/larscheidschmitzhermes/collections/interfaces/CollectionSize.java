package de.larscheidschmitzhermes.collections.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**
 * Use on JAX-RS Services to add collection size headers.
 */
@NameBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionSize {
    public static String DEFAULT_HEADER = "result-count";

    String headerName() default DEFAULT_HEADER;
}
