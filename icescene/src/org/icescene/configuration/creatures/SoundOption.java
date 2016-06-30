package org.icescene.configuration.creatures;

import java.io.Serializable;

public class SoundOption implements Serializable {

	private static final long serialVersionUID = 1L;
	private float time;
	private String name;

	public SoundOption(String name) {
		this.name = name;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SoundOption [time=" + time + ", name=" + name + "]";
	}

}
