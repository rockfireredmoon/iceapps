package org.icescene.audio;

public enum AudioQueue {

	DEFAULT, AMBIENT2, AMBIENT, MUSIC, WEATHER, COMBAT, NOISE, PREVIEWS, INTERFACE;

	public int getMax() {

		switch (this) {
		case AMBIENT:
			return 2;
		case AMBIENT2:
			return 2;
		case WEATHER:
			return 2;
		case COMBAT:
			return 8;
		case MUSIC:
			return 1;
		default:
			return 1;
		}
	}

	public float getBaseGain() {
		switch (this) {
		case AMBIENT:
		case AMBIENT2:
		case WEATHER:
		case COMBAT:
			return AudioAppState.get().getActualAmbientVolume();
		case MUSIC:
			return AudioAppState.get().getActualMusicVolume();
		default:
			return AudioAppState.get().getActualMasterVolume();
		}
	}

}
