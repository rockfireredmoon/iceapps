package org.icescene.configuration.creatures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.icelib.beans.MappedMap;
import org.icescene.animation.AnimationOption;

public abstract class AbstractCreatureDefinition implements Serializable {

	private static final long serialVersionUID = 1L;
	private CreatureKey key;
	private List<String> texture = new ArrayList<>();
	private Map<String, Skin> skin = new MappedMap<String, Skin>(new LinkedHashMap<String, Skin>(), String.class, Skin.class);
	private List<String> ambient = new ArrayList<>();

	public AbstractCreatureDefinition(CreatureKey key) {
		this.key = key;
	}

	public CreatureKey getKey() {
		return key;
	}

	public Map<String, Skin> getSkin() {
		return skin;
	}

	public List<String> getTexture() {
		return texture;
	}

	public List<String> getAmbient() {
		return ambient;
	}

	public abstract Map<String, AnimationOption> getAnimations();

}