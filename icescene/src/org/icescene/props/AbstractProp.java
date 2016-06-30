package org.icescene.props;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Point3D;
import org.icelib.Point4D;
import org.icelib.SceneryItem;
import org.icescene.entities.AbstractEntity;
import org.icescene.entities.EntityContext;
import org.icescene.propertyediting.Property;
import org.icescene.propertyediting.PropertyBean;
import org.icescene.scene.Buildable;
import org.iceui.IceUI;

import com.google.common.base.Objects;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;

public abstract class AbstractProp extends AbstractEntity implements PropertyBean, Buildable {

	public static final String ATTR_PRIMARY = "Primary";
	public static final String ATTR_LOCKED = "Locked";
	public static final String ATTR_LAYER = "Layer";
	public static final String ATTR_ROTATION = "Rotation";
	public static final String ATTR_ASSET = "Asset";
	public static final String ATTR_ASSET_NAME = "AssetName";
	public static final String ATTR_SCALE = "Scale";
	public static final String ATTR_TRANSLATION = "Translation";
	public static final String ATTR_SCENERY_ITEM = "SceneryItem";
	public static final String ATTR_NAME = "Name";
	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private static final Logger LOG = Logger.getLogger(AbstractProp.class.getName());
	protected boolean buildMode;
	protected final EntityContext context;
	protected List<Spatial> propSpatials = new ArrayList<Spatial>();
	protected SceneryItem sceneryItem = new SceneryItem();
	protected Node lightingParent;
	protected List<com.jme3.light.Light> lightNodes = new ArrayList<>();

	public enum Visibility {

		BUILD_MODE, GAME, BOTH
	}

	protected Node spatial;

	public AbstractProp(String name, EntityContext app) {
		this.context = app;
		spatial = new Node(name) {

			@Override
			protected void setParent(Node parent) {
				super.setParent(parent);
				if (parent == null) {
					LightControl lc;
					while ((lc = spatial.getControl(LightControl.class)) != null) {
						LOG.info(String.format("Removing light %s from %s", lc.getClass().getName(), getName()));
						spatial.removeControl(lc);
						lightingParent.removeLight(lc.getLight());
					}
				} else {
					for (com.jme3.light.Light l : lightNodes) {
						LOG.info(String.format("Adding light %s to the scene (%s)", l, lightingParent));
						lightingParent.addLight(l);
						spatial.addControl(new LightControl(l));
					}
				}
				onSpatialAttachedOrDetached();
			}

		};
	}

	protected void updateFromVariables() {
		// Update the prop based on the asset name variables
	}

	protected void onSpatialAttachedOrDetached() {
		// Invoked when spatial is attached to or detached from scene
	}

	public abstract AbstractProp clone();

	public Spatial getSpatial() {
		Node node;
		return spatial;
	}

	public SceneryItem getSceneryItem() {
		return sceneryItem;
	}

	public void setSceneryItem(SceneryItem sceneryItem) {
		final Vector3f location = IceUI.toVector3f(sceneryItem.getLocation());
		final Vector3f scale = IceUI.toVector3f(sceneryItem.getScale());
		final Quaternion rotation = IceUI.toQ(sceneryItem.getRotation());

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Updating prop: %s @%s X %s (%s)", sceneryItem.getAssetName(), location, scale, rotation));
		}
		spatial.setName(sceneryItem.getName());
		spatial.setLocalTranslation(location);
		spatial.setLocalScale(scale);
		spatial.setLocalRotation(rotation);
		this.sceneryItem = sceneryItem;
		changeSupport.firePropertyChange(ATTR_SCENERY_ITEM, null, sceneryItem);
	}

	public void setLightingParent(Node lightingParent) {
		this.lightingParent = lightingParent;
	}

	protected com.jme3.light.Light addLight(Light light, Node gameNode) {
		switch (light.getType()) {
		case POINT:
			PointLight pl = new PointLight();
			pl.setColor(light.getDefaultColor());
			final float radius = ((PointLightConfiguration) light).getRadius();
			pl.setRadius(radius);
			LOG.info(String.format("Adding point light of colour %s and radius of %5.3f", light.getDefaultColor(), radius));
			lightNodes.add(pl);
			return pl;
		case SPOT:
			SpotLight sl = new SpotLight();
			sl.setColor(light.getDefaultColor());
			final SpotLightConfiguration def = (SpotLightConfiguration) light;
			sl.setSpotRange(def.getRange());
			sl.setSpotInnerAngle(def.getInner() * FastMath.DEG_TO_RAD);
			sl.setSpotOuterAngle(def.getOuter() * FastMath.DEG_TO_RAD);
			LOG.info(String.format("Adding spot light of colour %s and range of %5.3f", light.getDefaultColor(), def.getRange(),
					def.getInner(), def.getOuter()));
			lightNodes.add(sl);
			return sl;
		}
		return null;
	}

	public List<Spatial> getSpatials() {
		return propSpatials;
	}

	public final void configureProp() {
		onConfigureProp();
		updateFromVariables();
	}

	protected void onConfigureProp() {

	}

	@Property(label = "Location", weight = 10, hint = Property.Hint.WORLD_POSITION)
	public Vector3f getTranslation() {
		return spatial.getLocalTranslation();
	}

	@Property
	public void setTranslation(Vector3f translation) {
		Point3D oldLoc = sceneryItem.getLocation();
		Point3D newLoc = IceUI.toPoint3D(translation);
		if (!Objects.equal(oldLoc, newLoc)) {
			sceneryItem.setLocation(newLoc);
			spatial.setLocalTranslation(translation);
			changeSupport.firePropertyChange(ATTR_TRANSLATION, IceUI.toVector3f(oldLoc), translation);
		}
	}

	@Property(label = "Scale", weight = 20, hint = Property.Hint.SCALE)
	public Vector3f getScale() {
		return spatial.getLocalScale();
	}

	@Property
	public void setScale(Vector3f scale) {
		Point3D oldScale = sceneryItem.getScale();
		Point3D newScale = IceUI.toPoint3D(scale);
		if (!Objects.equal(oldScale, newScale)) {
			sceneryItem.setScale(newScale);
			spatial.setLocalScale(scale);
			changeSupport.firePropertyChange(ATTR_SCALE, IceUI.toVector3f(oldScale), scale);
		}
	}

	@Property(label = "Asset", weight = 5)
	public String getAssetName() {
		return sceneryItem.getAssetName();
	}

	@Property
	public void setAssetName(String asset) {
		String oldName = sceneryItem.getAssetName();
		String old = sceneryItem.getAsset();
		sceneryItem.setAssetName(asset);
		changeSupport.firePropertyChange(ATTR_ASSET_NAME, oldName, asset);
		changeSupport.firePropertyChange(ATTR_ASSET, old, sceneryItem.getAsset());
	}

	public void setAsset(String asset) {
		String old = sceneryItem.getAsset();
		sceneryItem.setAsset(asset);
		updateFromVariables();
		changeSupport.firePropertyChange(ATTR_ASSET, old, sceneryItem.getAsset());
	}

	@Property(label = "Name", weight = 0)
	public String getName() {
		return spatial.getName();
	}

	@Property
	public void setName(String name) {
		String old = getName();
		spatial.setName(name);
		if (sceneryItem != null) {
			sceneryItem.setName(name);
		}
		changeSupport.firePropertyChange(ATTR_NAME, old, name);
	}

	@Property
	public void setRotation(Quaternion rotation) {
		Point4D oldRot = sceneryItem.getRotation();
		Point4D newRot = IceUI.toPoint4D(rotation);
		if (!Objects.equal(oldRot, newRot)) {
			sceneryItem.setRotation(newRot);
			spatial.setLocalRotation(rotation);
			changeSupport.firePropertyChange(ATTR_ROTATION, IceUI.toQ(oldRot), rotation);
		}
	}

	@Property(label = "Rotation", weight = 30, hint = Property.Hint.ROTATION_DEGREES)
	public Quaternion getRotation() {
		return spatial.getLocalRotation();
	}

	// @Property(label = "Rotation", weight = 30, hint =
	// Property.Hint.ROTATION_DEGREES)
	// public Vector3f getRotationAngles() {
	// Quaternion rot = spatial.getLocalRotation();
	// float[] ang = rot.toAngles(null);
	// return new Vector3f(ang[0] * FastMath.RAD_TO_DEG, ang[1] *
	// FastMath.RAD_TO_DEG, ang[2] * FastMath.RAD_TO_DEG);
	// }
	//
	// @Property
	// public void setRotationAngles(Vector3f rotation) {
	// Vector3f oldAng = getRotationAngles();
	// Quaternion old = spatial.getLocalRotation().clone();
	// float[] radAng = { rotation.x * FastMath.DEG_TO_RAD, rotation.y *
	// FastMath.DEG_TO_RAD, rotation.z * FastMath.DEG_TO_RAD };
	// spatial.setLocalRotation(spatial.getLocalRotation().fromAngles(radAng));
	// updateSceneryItemFromSpatial();
	// changeSupport.firePropertyChange(ATTR_ROTATION_ANGLES, oldAng, rotation);
	// changeSupport.firePropertyChange(ATTR_ROTATION, old,
	// spatial.getLocalRotation());
	// }

	@Property(label = "Layer", weight = 60)
	public int getLayer() {
		return getSceneryItem().getLayer();
	}

	@Property
	public void setLayer(int layer) {
		int old = getLayer();
		getSceneryItem().setLayer(layer);
		changeSupport.firePropertyChange(ATTR_LAYER, old, layer);
	}

	@Property(label = "Locked", weight = 70)
	public boolean isLocked() {
		return getSceneryItem().isLocked();
	}

	@Property
	public void setLocked(boolean locked) {
		boolean old = isLocked();
		getSceneryItem().setLocked(locked);
		changeSupport.firePropertyChange(ATTR_LOCKED, old, locked);
	}

	@Property(label = "Primary", weight = 80)
	public boolean isPrimary() {
		return getSceneryItem().isPrimary();
	}

	@Property
	public void setPrimary(boolean primary) {
		boolean old = isPrimary();
		getSceneryItem().setPrimary(primary);
		changeSupport.firePropertyChange(ATTR_PRIMARY, old, primary);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	private void updateSceneryItemFromSpatial() {
		if (sceneryItem != null) {
			sceneryItem.setLocation(IceUI.toPoint3D(spatial.getLocalTranslation()));
			sceneryItem.setRotation(IceUI.toPoint4D(spatial.getLocalRotation()));
			sceneryItem.setScale(IceUI.toPoint3D(spatial.getLocalScale()));
		}
	}
}
