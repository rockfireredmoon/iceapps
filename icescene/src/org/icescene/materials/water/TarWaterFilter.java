package org.icescene.materials.water;

import org.icelib.Color;
import org.iceui.IceUI;

import com.jme3.math.ColorRGBA;

public class TarWaterFilter implements WaterFilterCapable {

	public ColorRGBA getColor() {
		return IceUI.toRGBA(new Color(10, 10, 10));
	}

	public ColorRGBA getDeepWaterColor() {
		return IceUI.toRGBA(new Color(0, 0, 0));
	}

	public float getTransparency() {
		return 1f;
	}

	public float getMaxAmplitude() {
		return .2f;
	}

	public float getWaveScale() {
		return 3;
	}

	public float getSunScale() {
		return 1f;
	}

	public ColorRGBA getSunColor() {
		return IceUI.toRGBA(new Color(64, 64, 64));
	}

	public float getShininess() {
		return 0.1f;
	}

	public float getSpeed() {
		return 0.1f;
	}

	public float getRefraction() {
		return 0.7f;
	}
}
