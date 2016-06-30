package org.icescene.environment;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.icescene.SceneConfig;
import org.icescene.SceneConstants;

import com.google.common.base.Objects;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 * Manages any current directional or ambient light. Various systems need to
 * know light direction and colour (such as water and shadow post processors).
 * The sun light and direction will vary with the environment, and may be edited
 * in the various editor states. This object manages the current light values
 * and sends events to all interested parties when they change.
 */
public class EnvironmentLight implements PreferenceChangeListener {

	public final static Vector3f SUN_POSITION_DEFAULT = new Vector3f(0f, 100f, -100f);

	public static final String PROP_SUN_ENABLED = "sunEnabled";
	public static final String PROP_SUN_POSITION = "sunPosition";
	public static final String PROP_SUN_DIRECTION = "sunDirection";
	public static final String PROP_AMBIENT_ENABLED = "ambientEnabled";
	public static final String PROP_AMBIENT_COLOR = "ambientColor";
	public static final String PROP_SUN_COLOR = "sunColor";

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	private DirectionalLight sun;
	private AmbientLight ambient;
	private final Node gameNode;
	private boolean sunEnabled;
	private boolean ambientEnabled;
	private final Camera cam;
	private Vector3f sunPosition = SUN_POSITION_DEFAULT;
	private float lightMultiplier = 1f;
	private Preferences prefs;
	private ColorRGBA sunColor;
	private ColorRGBA ambientColor;

	private final static Logger LOG = Logger.getLogger(EnvironmentLight.class.getName());

	public EnvironmentLight(Camera cam, Node gameNode, Preferences prefs) {
		this.gameNode = gameNode;
		this.cam = cam;
		this.prefs = prefs;

		lightMultiplier = prefs.getFloat(SceneConfig.SCENE_LIGHT_MULTIPLIER, SceneConfig.SCENE_LIGHT_MULTIPLIER_DEFAULT);
		prefs.addPreferenceChangeListener(this);

		// Directional
		sun = new DirectionalLight();
		sunColor = sun.getColor().clone();
		sun.setColor(sunColor.mult(SceneConstants.DIRECTIONAL_BRIGHTNESS).mult(lightMultiplier));

		// Ambient
		ambient = new AmbientLight();
		ambientColor = ColorRGBA.Gray.clone();
		ambient.setColor(ambientColor.mult(SceneConstants.AMBIENT_BRIGHTNESS).mult(lightMultiplier));
	}

	public ColorRGBA getSunColor() {
		return sunColor;
	}

	public Vector3f getSunPosition() {
		return sunPosition.clone();
	}

	public DirectionalLight getSun() {
		return sun;
	}

	public Vector3f getSunDirection() {
		return sun.getDirection().clone();
	}

	public void setSunDirection(Vector3f dir) {
		Vector3f old = this.sun.getDirection().clone();
		if (!Objects.equal(old, dir)) {
			sun.getDirection().set(dir);
			firePropertyChange(PROP_SUN_DIRECTION, old, dir);
		}
	}

	public Vector3f getSunRepresentationPosition() {
		return sunPosition.clone();
	}

	public Vector3f getLightSourcePosition() {
		// TODO maybe adjust a little?
		return sunPosition.clone();
	}

	public void setSunToLocation(Vector3f sunLocation) {
		Vector3f old = this.sunPosition.clone();
		Vector3f oldDir = sun.getDirection().clone();

		this.sunPosition = sunLocation.clone();

		// Work out direction from current camera location
		// (TODO maybe have to update this more often)
		Vector3f camLoc = cam.getLocation();
		sun.setDirection(this.sunPosition.subtract(camLoc).negateLocal());

		firePropertyChange(PROP_SUN_POSITION, old, this.sunPosition);
		firePropertyChange(PROP_SUN_DIRECTION, oldDir, sun.getDirection());
	}

	public void setDirectionalEnabled(boolean sunEnabled) {
		if (sunEnabled && !this.sunEnabled) {
			LOG.info(String.format("Set sun on"));
			gameNode.addLight(sun);
			this.sunEnabled = sunEnabled;
			firePropertyChange(PROP_SUN_ENABLED, false, true);
		} else if (!sunEnabled && this.sunEnabled) {
			LOG.info(String.format("Set sun off"));
			gameNode.removeLight(sun);
			this.sunEnabled = sunEnabled;
			firePropertyChange(PROP_SUN_ENABLED, true, false);
		}
	}

	public void setDirectionalColor(ColorRGBA color) {
		// LOG.info("Set sun color to " + color);
		ColorRGBA old = sunColor.clone();
		sunColor.set(color);
		sun.setColor(sunColor.mult(lightMultiplier));
		firePropertyChange(PROP_SUN_COLOR, old, sunColor);
	}

	public void setAmbientEnabled(boolean ambientEnabled) {
		if (ambientEnabled && !this.ambientEnabled) {
			LOG.info("Setting ambient on");
			gameNode.addLight(ambient);
			this.ambientEnabled = ambientEnabled;
			firePropertyChange(PROP_AMBIENT_ENABLED, false, true);
		} else if (!ambientEnabled && this.ambientEnabled) {
			LOG.info("Setting ambient off");
			gameNode.removeLight(ambient);
			this.ambientEnabled = ambientEnabled;
			firePropertyChange("ambientEnabled", true, false);
		}
	}

	public void setAmbientColor(ColorRGBA color) {
		ColorRGBA old = ambientColor.clone();
		ambientColor.set(color);
		ambient.setColor(ambientColor.mult(SceneConstants.AMBIENT_BRIGHTNESS).mult(lightMultiplier));
		firePropertyChange(PROP_AMBIENT_COLOR, old, ambientColor);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		changeSupport.firePropertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public boolean isDirectionalEnabled() {
		return sunEnabled;
	}

	public boolean isAmbientEnabled() {
		return ambientEnabled;
	}

	public ColorRGBA getAmbientColor() {
		return ambientColor;
	}

	public boolean isDirectionalAllowed() {
		return isDirectionalEnabled() && !sunColor.equals(ColorRGBA.Black);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().equals(SceneConfig.SCENE_LIGHT_MULTIPLIER)) {
			lightMultiplier = prefs.getFloat(SceneConfig.SCENE_LIGHT_MULTIPLIER, SceneConfig.SCENE_LIGHT_MULTIPLIER_DEFAULT);
			LOG.info(String.format("New environment light multiplier is %f", lightMultiplier));
			sun.setColor(sunColor.mult(SceneConstants.DIRECTIONAL_BRIGHTNESS).mult(lightMultiplier));
			ambient.setColor(ambientColor.mult(SceneConstants.AMBIENT_BRIGHTNESS).mult(lightMultiplier));
		}
	}
}
