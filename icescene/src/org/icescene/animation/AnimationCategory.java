package org.icescene.animation;

public class AnimationCategory {

	private final String name;
	private int top;
	private int bottom;

	public AnimationCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

}
