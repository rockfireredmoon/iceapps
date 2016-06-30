package org.icescene.configuration.attachments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AttachmentPlace implements Serializable {

	private static final long serialVersionUID = 1L;
	private String bone;
	private Vector3f position;
	private Quaternion orientation;
	private Vector3f scale;
	private Vector3f size;
	private List<String> hidden = new ArrayList<String>();

	public AttachmentPlace() {
	}

	public Vector3f getSize() {
		return size;
	}

	public void setSize(Vector3f size) {
		this.size = size;
	}

	public void setBone(String bone) {
		this.bone = bone;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setOrientation(Quaternion orientation) {
		this.orientation = orientation;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public void setHidden(List<String> hidden) {
		this.hidden = hidden;
	}

	public String getBone() {
		return bone;
	}

	public Vector3f getScale() {
		return scale;
	}

	public List<String> getHidden() {
		return hidden;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Quaternion getOrientation() {
		return orientation;
	}

	@Override
	public String toString() {
		return "AttachmentPlace{" + "bone=" + bone + ", position=" + position + ", orientation=" + orientation + ", scale=" + scale
				+ ", hidden=" + hidden + '}';
	}
}