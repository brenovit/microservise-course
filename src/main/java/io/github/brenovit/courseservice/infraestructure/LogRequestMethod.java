package io.github.brenovit.courseservice.infraestructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogRequestMethod {

    String action() default "";

    boolean includeRequestBody() default true;

    boolean includeResponseBody() default false;

}
