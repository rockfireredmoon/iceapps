package org.icescene.materials.water;

import org.icelib.Color;
import org.iceui.IceUI;

import com.jme3.math.ColorRGBA;

public class WaterWaterFilter implements WaterFilterCapable {

	public ColorRGBA getColor() {
		return IceUI.toRGBA(new Color(155, 234, 234));
	}

	public ColorRGBA getDeepWaterColor() {
		return IceUI.toRGBA(new Color(60, 103, 102));
	}

	public float getTransparency() {
		return .3f;
	}

	public float getMaxAmplitude() {
		return 5;
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
		return 0.7f;
	}

	public float getSpeed() {
		return 1.0f;
	}

	public float getRefraction() {
		return 0f;
	}
}
