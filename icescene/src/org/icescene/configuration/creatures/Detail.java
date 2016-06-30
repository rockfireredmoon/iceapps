package org.icescene.configuration.creatures;

import java.io.Serializable;

import org.icelib.AttachmentPoint;

public class Detail implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String mesh;
	private AttachmentPoint point;
	private String texture;
	private boolean animated;

	public String getName() {
		return name;
	}

	public String getMesh() {
		return mesh;
	}

	public AttachmentPoint getPoint() {
		return point;
	}

	public String getTexture() {
		return texture;
	}

	public boolean isAnimated() {
		return animated;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	public void setPoint(AttachmentPoint point) {
		this.point = point;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	@Override
	public String toString() {
		return "Detail{" + "name=" + name + ", mesh=" + mesh + ", point=" + point + ", texture=" + texture + ", animated="
				+ animated + '}';
	}
}