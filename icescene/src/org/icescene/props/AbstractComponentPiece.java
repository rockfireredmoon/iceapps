package org.icescene.props;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.icescene.propertyediting.Property;

import com.google.common.base.Objects;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public abstract class AbstractComponentPiece implements ComponentPiece {

	public static final String ATTR_ROTATION = "Rotation";
	public static final String ATTR_SCALE = "Scale";
	public static final String ATTR_TRANSLATION = "Translation";

	protected Quaternion rotation = Quaternion.IDENTITY.clone();
	protected Vector3f translation = new Vector3f(Vector3f.ZERO);

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	protected AbstractComponentPiece populateClone(AbstractComponentPiece piece) {
		piece.rotation = rotation.clone();
		piece.translation = translation.clone();
		return piece;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	@Property(label = "Translation", weight = 10, hint = Property.Hint.WORLD_POSITION)
	public Vector3f getTranslation() {
		return translation;
	}

	@Property
	public void setTranslation(Vector3f location) {
		Vector3f oldLoc = this.translation.clone();
		if (!Objects.equal(oldLoc, location)) {
			this.translation.set(location);
			changeSupport.firePropertyChange(ATTR_TRANSLATION, oldLoc, location);
		}
	}

	@Property
	public void setRotation(Quaternion rotation) {
		Quaternion oldRot = this.rotation.clone();
		if (!Objects.equal(oldRot, rotation)) {
			this.rotation = rotation;
			changeSupport.firePropertyChange(ATTR_ROTATION, oldRot, rotation);
		}
	}

	@Property(label = "Rotation", weight = 30, hint = Property.Hint.ROTATION_DEGREES)
	public Quaternion getRotation() {
		return rotation;
	}

}
