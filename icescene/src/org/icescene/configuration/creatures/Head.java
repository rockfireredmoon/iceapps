package org.icescene.configuration.creatures;

import java.io.Serializable;

public class Head implements Serializable {

	private static final long serialVersionUID = 1L;
	private String texture;
	private String mesh;
	private boolean helmetHead;

	public Head() {
	}

	public boolean isHelmetHead() {
		return helmetHead;
	}

	public void setHelmetHead(boolean helmetHead) {
		this.helmetHead = helmetHead;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	public String getMesh() {
		return mesh;
	}

	public String getTexture() {
		return texture;
	}

}