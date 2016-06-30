package org.icescene.materials.water;

import com.jme3.math.ColorRGBA;

public interface WaterFilterCapable {

    ColorRGBA getColor();

    ColorRGBA getDeepWaterColor();

    ColorRGBA getSunColor();

    float getTransparency();

    float getMaxAmplitude();

    float getWaveScale();

    float getSunScale();

    float getShininess();

    public float getSpeed();

    public float getRefraction();
}
