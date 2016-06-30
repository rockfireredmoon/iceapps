package org.icescene.props;

import com.jme3.math.ColorRGBA;

public class SpotLightConfiguration extends Light {
    float outer;
    float inner;
    float range;

    public SpotLightConfiguration(ColorRGBA defaultColor, float range, float outer, float inner) {
        super(LightType.SPOT, defaultColor);
        this.range = range;
        this.outer = outer;
        this.inner = inner;
    }

    public float getOuter() {
        return outer;
    }

    public float getInner() {
        return inner;
    }

    public float getRange() {
        return range;
    }
    
}
