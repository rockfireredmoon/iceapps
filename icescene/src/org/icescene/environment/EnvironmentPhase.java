package org.icescene.environment;

import org.icelib.Icelib;

import com.jme3.math.Vector3f;

public enum EnvironmentPhase {

	SUNRISE, DAY, SUNSET, NIGHT, NONE;

	public EnvironmentPhase getNextCycle() {
		switch (this) {
		case SUNRISE:
			return DAY;
		case DAY:
			return SUNSET;
		case SUNSET:
			return NIGHT;
		case NIGHT:
			return SUNRISE;
		}
		return NONE;
	}

	public static String getEnvironmentName(String name) {
		for (EnvironmentPhase t : EnvironmentPhase.values()) {
			final String eng = Icelib.toEnglish(t);
			if (name.endsWith(eng)) {
				return name.substring(0, name.length() - eng.length());
			}
		}
		return name;
	}

	public static EnvironmentPhase fromName(String name) {
		for (EnvironmentPhase t : EnvironmentPhase.values()) {
			final String eng = Icelib.toEnglish(t);
			if (!name.equals(eng) && name.endsWith(eng)) {
				return t;
			}
		}
		return EnvironmentPhase.NONE;
	}

	public static EnvironmentPhase[] phases() {
		return new EnvironmentPhase[] { SUNRISE, DAY, SUNSET, NIGHT };
	}

	public Vector3f getLightDirection() {
		switch(this) {
		case SUNRISE:
			return new Vector3f(-1,-0.5f,0);
		case DAY:
			return new Vector3f(0,-1,0);
		case SUNSET:
			return new Vector3f(1,-0.5f,0);
		case NIGHT:
			return new Vector3f(1,0,0);
		default:
			return new Vector3f(0,-1,0);
		}
	}
}