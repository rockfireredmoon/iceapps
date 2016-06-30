package org.icescene.animation;

import org.icelib.beans.MappedList;

public class AnimationActionElement extends MappedList<Integer> {
	private static final long serialVersionUID = 1L;
	private String name;

	public AnimationActionElement(String name) {
		super(Integer.class);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}