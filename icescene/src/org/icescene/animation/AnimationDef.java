package org.icescene.animation;

import java.util.Map;

import org.icelib.beans.MappedMap;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.scene.AlignToFloorMode;

public class AnimationDef implements IcesceneService {

	private Map<String, AnimationCategory> categories = new MappedMap<>(String.class, AnimationCategory.class);
	private Map<String, AnimationOption> animations = new MappedMap<>(String.class, AnimationOption.class);
	private Map<String, AnimationAction> actions = new MappedMap<>(String.class, AnimationAction.class);
	private AlignToFloorMode alignToFloorMode;
	private float blend;

	public AnimationDef() {
	}

	public AlignToFloorMode getAlignToFloorMode() {
		return alignToFloorMode;
	}

	public void setAlignToFloorMode(AlignToFloorMode alignToFloorMode) {
		this.alignToFloorMode = alignToFloorMode;
	}

	public float getBlend() {
		return blend;
	}

	public void setBlend(float blend) {
		this.blend = blend;
	}

	public Map<String, AnimationAction> getActions() {
		return actions;
	}

	public Map<String, AnimationOption> getAnimations() {
		return animations;
	}

	public Map<String, AnimationCategory> getCategories() {
		return categories;
	}

	@Override
	public void init(IcesceneApp app) {
	}

}
