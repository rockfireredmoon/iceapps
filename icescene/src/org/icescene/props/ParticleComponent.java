package org.icescene.props;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.icescene.propertyediting.Property;
import org.icescene.scene.Buildable;

import com.google.common.base.Objects;
import com.jme3.math.Vector3f;

public class ParticleComponent implements Buildable {
	public static final String ATTR_SCALE = "Scale";
	public static final String ATTR_POSITION = "Position";
	public static final String ATTR_TEMPLATE = "Template";

	private String template;
	private Vector3f position = new Vector3f(Vector3f.ZERO);
	private Vector3f scale = new Vector3f(1, 1, 1);

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public ParticleComponent(String template) {
		this.template = template;
	}

	public ParticleComponent clone() {
		ParticleComponent xRef = new ParticleComponent(template);
		xRef.position = position.clone();
		xRef.scale = scale.clone();
		return xRef;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	@Property(label = "Template", weight = 0)
	public String getTemplate() {
		return template;
	}

	@Property
	public void setTemplate(String template) {
		String old = getTemplate();
		if (!Objects.equal(old, template)) {
			this.template = template;
			changeSupport.firePropertyChange(ATTR_TEMPLATE, old, template);
		}
	}

	@Property
	public void setPosition(Vector3f location) {
		Vector3f oldLoc = this.position;
		if (!Objects.equal(oldLoc, location)) {
			this.position.set(location);
			changeSupport.firePropertyChange(ATTR_POSITION, oldLoc, location);
		}
	}

	@Property(label = "Position", weight = 10, hint = Property.Hint.WORLD_POSITION)
	public Vector3f getPosition() {
		return position;
	}

	@Property
	public void setScale(Vector3f scale) {
		Vector3f oldScale = this.scale;
		if (!Objects.equal(oldScale, scale)) {
			this.scale.set(scale);
			changeSupport.firePropertyChange(ATTR_SCALE, oldScale, scale);
		}
	}

	@Property(label = "Scale", weight = 20, hint = Property.Hint.SCALE)
	public Vector3f getScale() {
		return scale;
	}
}
