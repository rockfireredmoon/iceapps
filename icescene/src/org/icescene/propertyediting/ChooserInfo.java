package org.icescene.propertyediting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Inherited()
@Target(value = ElementType.METHOD)
public @interface ChooserInfo {

    String root();

    String pattern() default ".*";
}