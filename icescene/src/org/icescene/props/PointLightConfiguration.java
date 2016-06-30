package org.icescene.props;

import com.jme3.math.ColorRGBA;

public class PointLightConfiguration extends Light {
    float radius;

    public PointLightConfiguration(ColorRGBA defaultColor, float radius) {
        super(LightType.POINT, defaultColor);
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
    
}
