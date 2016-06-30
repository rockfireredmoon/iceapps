package org.icescene.propertyediting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Inherited()
@Target(value = ElementType.METHOD)
public @interface FloatRange {
    
    float min() default 0;
    float max() default Float.MAX_VALUE;
    float incr() default 1;
    int precision() default -1; 
}