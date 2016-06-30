package org.icescene.animation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.icelib.beans.MappedMap;
import org.icescene.configuration.creatures.SoundOption;

public class AnimationOption implements Serializable, Comparable<AnimationOption> {
	private static final long serialVersionUID = 1L;
	private List<Float> impacts = new ArrayList<>();
	private Map<String, SoundOption> sounds = new MappedMap<>(String.class, SoundOption.class);
	private List<Float> stepSounds = new ArrayList<>();
	private String category;
	private float blend = 0.2f;
	private boolean hideWeapons;
	private String effect;
	private String key;

	public AnimationOption(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public float getBlend() {
		return blend;
	}

	public void setBlend(float blend) {
		this.blend = blend;
	}

	public boolean isHideWeapons() {
		return hideWeapons;
	}

	public void setHideWeapons(boolean hideWeapons) {
		this.hideWeapons = hideWeapons;
	}

	public List<Float> getImpacts() {
		return impacts;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public Map<String, SoundOption> getSounds() {
		return sounds;
	}

	public List<Float> getStepSounds() {
		return stepSounds;
	}

	@Override
	public String toString() {
		return "AnimationOptions [impacts=" + impacts + ", sounds=" + sounds + ", stepSounds=" + stepSounds + "]";
	}

	@Override
	public int compareTo(AnimationOption o) {
		return key == null ? (o.key == null ? 0 : -1) : key.compareTo(o.key);
	}

}
