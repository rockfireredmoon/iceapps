package org.icescene.props;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.icescene.propertyediting.Property;
import org.icescene.scene.Buildable;

import com.google.common.base.Objects;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class XRef implements Buildable {
	public static final String ATTR_ROTATION = "Rotation";
	public static final String ATTR_SCALE = "Scale";
	public static final String ATTR_TRANSLATION = "Translation";
	public static final String ATTR_CREF = "CRef";
	public static final String ATTR_FLOOR_ANCHOR = "FloorAnchor";

	private String cref;
	private Quaternion rotation = Quaternion.IDENTITY.clone();
	private Vector3f location = new Vector3f(Vector3f.ZERO);
	private int floorAnchor;

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public XRef(String cref) {
		this.cref = cref;
	}

	public XRef clone() {
		XRef xRef = new XRef(cref);
		xRef.rotation = rotation.clone();
		xRef.location = location.clone();
		xRef.floorAnchor = floorAnchor;
		return xRef;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	@Property(label = "CRef", weight = 0)
	public String getCRef() {
		return cref;
	}

	@Property
	public void setCRef(String cref) {
		String old = getCRef();
		if (!Objects.equal(old, cref)) {
			this.cref = cref;
			changeSupport.firePropertyChange(ATTR_CREF, old, cref);
		}
	}

	@Property(label = "Floor Anchor", weight = 60)
	public int getFloorAnchor() {
		return floorAnchor;
	}

	@Property
	public void setFloorAnchor(int floorAnchor) {
		int old = getFloorAnchor();
		if (!Objects.equal(old, floorAnchor)) {
			this.floorAnchor = floorAnchor;
			changeSupport.firePropertyChange(ATTR_FLOOR_ANCHOR, old, floorAnchor);
		}
	}

	@Property(label = "Location", weight = 10, hint = Property.Hint.WORLD_POSITION)
	public Vector3f getLocation() {
		return location;
	}

	@Property
	public void setLocation(Vector3f location) {
		Vector3f oldLoc = this.location;
		if (!Objects.equal(oldLoc, location)) {
			this.location.set(location);
			changeSupport.firePropertyChange(ATTR_TRANSLATION, oldLoc, location);
		}
	}

	@Property
	public void setRotation(Quaternion rotation) {
		Quaternion oldRot = this.rotation;
		if (!Objects.equal(oldRot, rotation)) {
			this.rotation = rotation;
			changeSupport.firePropertyChange(ATTR_ROTATION, oldRot, rotation);
		}
	}

	@Property(label = "Rotation", weight = 30, hint = Property.Hint.ROTATION_DEGREES)
	public Quaternion getRotation() {
		return rotation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cref == null) ? 0 : cref.hashCode());
		result = prime * result + floorAnchor;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((rotation == null) ? 0 : rotation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XRef other = (XRef) obj;
		if (cref == null) {
			if (other.cref != null)
				return false;
		} else if (!cref.equals(other.cref))
			return false;
		if (floorAnchor != other.floorAnchor)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (rotation == null) {
			if (other.rotation != null)
				return false;
		} else if (!rotation.equals(other.rotation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XRef [cref=" + cref + ", rotation=" + rotation + ", location=" + location + ", floorAnchor=" + floorAnchor + "]";
	}

}
