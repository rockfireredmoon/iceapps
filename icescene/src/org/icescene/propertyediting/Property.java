package org.icescene.propertyediting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Inherited()
@Target(value = ElementType.METHOD)
public @interface Property {

    public enum Hint {

        SCALE, WORLD_POSITION, RADIUS, ANGLE, LABEL, GENERIC, DISTANCE, UNSET, ROTATION_DEGREES, ROTATION, DIRECTION, TEXTURE_PATH, SOUND_PATH, RGBA
    }

    Hint hint() default Hint.UNSET;

    String label() default "";
    
    String eventId() default "";

    int weight() default -1;
}