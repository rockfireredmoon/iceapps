package org.icescene.controls;

import org.icescene.animation.AnimationOption;

public class AnimationRequest {

	private AnimationOption name;
	private Boolean loop;

	public AnimationRequest(AnimationOption name) {
		this(name, null);
	}

	public AnimationRequest(AnimationOption name, Boolean loop) {
		if (name == null)
			throw new IllegalArgumentException("Animation is required.");
		this.name = name;
		this.loop = loop;
	}

	public AnimationOption getName() {
		return name;
	}

	public void setName(AnimationOption name) {
		this.name = name;
	}

	public Boolean getLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

}