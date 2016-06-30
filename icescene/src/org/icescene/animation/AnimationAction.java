package org.icescene.animation;

import org.icelib.beans.MappedMap;

public class AnimationAction extends MappedMap<String, AnimationActionElement> {

	private static final long serialVersionUID = 1L;

	private String name;

	public AnimationAction(String name) {
		super(String.class, AnimationActionElement.class);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
