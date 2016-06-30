package org.icescene.props;

import com.jme3.math.ColorRGBA;

public class Light {
    private LightType type;
    private ColorRGBA defaultColor;

    public Light(LightType type, ColorRGBA defaultColor) {
        this.type = type;
        this.defaultColor = defaultColor;
    }

    public LightType getType() {
        return type;
    }

    public ColorRGBA getDefaultColor() {
        return defaultColor;
    }
    
}
