package org.icescene.materials.water;

import org.icelib.Color;
import org.iceui.IceUI;

import com.jme3.math.ColorRGBA;

public class SwampWaterWaterFilter implements WaterFilterCapable {
    
    public ColorRGBA getColor() {
        return IceUI.toRGBA(new Color(92, 151, 28));
    }

    public ColorRGBA getDeepWaterColor() {
        return IceUI.toRGBA(new Color(28, 35, 20));
    }

    public float getTransparency() {
        return .6f;
    }

    public float getMaxAmplitude() {
       return 1;
    }

    public float getWaveScale() {
        return 0.005f;
    }

    public float getSunScale() {
        return 3;
    }

    public ColorRGBA getSunColor() {
        return null;
    }

    public float getShininess() {
        return 0.6f;
    }

    public float getSpeed() {
        return 0.8f;
    }

    public float getRefraction() {
        return 0.3f;
    }
}
