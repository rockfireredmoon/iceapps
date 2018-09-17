package org.icescene.materials.water;

import org.icelib.Color;
import org.iceui.IceUI;

import com.jme3.math.ColorRGBA;

public class TropicalWaterFilter implements WaterFilterCapable {

	public ColorRGBA getColor() {
		return IceUI.toRGBA(new Color(65, 204, 227));
	}

	public ColorRGBA getDeepWaterColor() {
		return IceUI.toRGBA(new Color(42, 132, 147));
	}

	public float getTransparency() {
		return .1f;
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
		return 0.7f;
	}

	public float getSpeed() {
		return 1.0f;
	}

	public float getRefraction() {
		return 0f;
	}
}
