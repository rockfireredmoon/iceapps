package org.icescene.scene;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.icescene.NodeVisitor;
import org.icescene.NodeVisitor.VisitResult;
import org.icescene.materials.WireframeWidget;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.WireBox;

public abstract class AbstractBuildableControl<T extends Buildable> extends AbstractControl
		implements PropertyChangeListener {

	private final static Logger LOG = Logger.getLogger(AbstractBuildableControl.class.getName());
	private AssetManager assetManager;
	private List<Spatial> selectionShapes = new ArrayList<Spatial>();
	private boolean selected;
	private final Node toolsNode;
	private T buildable;

	protected Vector3f startScale = null;
	protected Quaternion startRotation;
	protected Vector3f startLocation;

	public AbstractBuildableControl(AssetManager assetManager, Node toolsNode, T buildable) {
		this.assetManager = assetManager;
		this.toolsNode = toolsNode;
		this.buildable = buildable;
	}

	public T getEntity() {
		return buildable;
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		// BuildableControl bc = new BuildableControl(assetManager, client,
		// entity, toolsNode) {
		// @Override
		// protected void onApply(BuildableControl actualBuildable) {
		// BuildableControl.this.onApply(this);
		// }
		// };
		// bc.setSpatial(spatial);
		// bc.assetManager = assetManager;
		// bc.setEnabled(isEnabled());
		// return bc;
		throw new UnsupportedOperationException();
	}

	public boolean isSelected() {
		return !selectionShapes.isEmpty();
	}

	public void setSelected(boolean selected) {
		LOG.info("Setting buildable selected to " + selected);
		this.selected = selected;
		checkSelection();
	}

	public void rotateBuildableTo(Quaternion rotation) {
		if (startRotation == null) {
			startRotation = spatial.getLocalRotation().clone();
		}
		spatial.setLocalRotation(rotation);
		updateBounds();
	}

	public void rotateBuildable(float radx, float rady, float radz) {
		if (startRotation == null) {
			startRotation = spatial.getLocalRotation().clone();
		}
		spatial.rotate(radx, rady, radz);
		updateBounds();
	}

	public void scaleBuildableTo(float x, float y, float z) {
		if (startScale == null) {
			startScale = spatial.getLocalScale().clone();
		}
		spatial.setLocalScale(x, y, z);
		updateBounds();
	}

	public void scaleBuildable(float x, float y, float z) {
		if (startScale == null) {
			startScale = spatial.getLocalScale().clone();
		}
		spatial.scale(x, y, z);
		updateBounds();
	}

	public void scaleBuildable(float factor) {
		if (startScale == null) {
			startScale = spatial.getLocalScale().clone();
		}
		Vector3f newScale = spatial.getLocalScale().add(factor, factor, factor);
		LOG.info(String.format("New scale is %s (start as %s)", newScale, startScale));
		spatial.setLocalScale(newScale);
		updateBounds();
	}

	public void moveBuildable(Vector3f offset) {
		if (startLocation == null) {
			startLocation = spatial.getLocalTranslation();
		}
		spatial.move(offset);
		updateBounds();
	}

	public void moveBuildableTo(Vector3f location) {
		if (startLocation == null) {
			startLocation = spatial.getLocalTranslation();
		}
		spatial.setLocalTranslation(location);
		updateBounds();
	}

	public boolean hasChanged() {
		return hasMoved() || hasRotated() || hasScaled();
	}

	public boolean hasMoved() {
		return startLocation != null;
	}

	public boolean hasRotated() {
		return startRotation != null;
	}

	public boolean hasScaled() {
		return startScale != null;
	}

	public void resetChanges() {
		boolean changed = hasChanged();
		if (startScale != null) {
			LOG.info(String.format("Resetting scale to %s", startScale));
			spatial.setLocalScale(startScale);
			startScale = null;
		}
		if (startRotation != null) {
			LOG.info(String.format("Resetting rotation to %s", startRotation));
			spatial.setLocalRotation(startRotation);
			startRotation = null;
		}
		if (startLocation != null) {
			LOG.info(String.format("Resetting location to %s", startLocation));
			spatial.setLocalTranslation(startLocation);
			startLocation = null;
		}
	}

	public void applyChanges() {
		boolean changed = hasChanged();
		if (changed) {
			onApply(this);
			startScale = null;
			startRotation = null;
			startLocation = null;
			detachSelectionShape();
			checkSelection();
		}
	}

	@Override
	public void setSpatial(Spatial spatial) {
		if (this.spatial != null) {
			buildable.removePropertyChangeListener(this);
		}
		super.setSpatial(spatial);
		if (spatial != null) {
			buildable.addPropertyChangeListener(this);
		}
	}

	protected void attachGeometrySelection(Geometry geom) {
		LOG.info("Attaching geometry selection");
		Mesh mesh = geom.getMesh();
		if (mesh == null) {
			LOG.info("No mesh");
			return;
		}
		final Geometry selectionGeometry = new Geometry("selection_geometry_sceneviewer", mesh);
		selectionGeometry.setMaterial(new WireframeWidget(assetManager, ColorRGBA.Yellow));
		selectionGeometry.setLocalTransform(geom.getWorldTransform());
		selectionShapes.add(selectionGeometry);
		toolsNode.attachChild(selectionGeometry);
	}

	protected abstract void onApply(AbstractBuildableControl<T> actualBuildable);

	protected void checkSelection() {
		final boolean shouldActivate = shouldActivate();
		if (shouldActivate() && selectionShapes.isEmpty()) {
			LOG.info(String.format("Attaching selection shape to %s", spatial.getName()));
			attachSelectionShape(spatial);
		} else if (!shouldActivate && !selectionShapes.isEmpty()) {
			LOG.info(String.format("Detaching selection shape from %s", selectionShapes.get(0).getParent().getName()));
			detachSelectionShape();
		} else {
			LOG.info("No changeing sel state (should activate = " + shouldActivate + ", enabled = " + isEnabled()
					+ ", selected = " + selected + ")");
		}

	}

	protected void attachBoxSelection(Spatial geom) {
		BoundingVolume bound = geom.getWorldBound();
		if (bound instanceof BoundingBox) {
			BoundingBox bbox = (BoundingBox) bound;
			Vector3f extent = new Vector3f();
			bbox.getExtent(extent);

			final Geometry selectionGeometry = WireBox.makeGeometry(bbox);
			;
			LOG.info("Attaching box selection of " + extent + " to geom at " + geom.getWorldTranslation() + " tools @ "
					+ toolsNode.getWorldTranslation() + " BBOX CENT: " + bbox.getCenter() + " BBX: " + bbox);
			selectionGeometry.setMaterial(new WireframeWidget(assetManager, ColorRGBA.Orange));
			// selectionGeometry.setLocalRotation(geom.getWorldRotation());
			selectionGeometry.setLocalTranslation(geom.getWorldTranslation());
			// selectionGeometry.setLocalTranslation(geom.getWorldTranslation().subtract(bbox.getCenter()));
			// selectionGeometry.setLocalTranslation(bbox.getCenter().subtract(geom.getWorldTranslation()));
			// Vector3f buildScale = new Vector3f(1,1,1);
			// scale.x = 1/geom.getWorldScale().x;
			// scale.y = 1/geom.getWorldScale().y;
			// scale.z = 1/geom.getWorldScale().z;
			Node selectionShape = new Node("SelectionParent");
			((Node) selectionShape).attachChild(selectionGeometry);
			// selectionShape.setLocalTransform(geom.getWorldTransform());
			// selectionShape.setLocalTranslation(geom.getWorldTranslation());
			// selectionGeometry.setLocalScale(buildScale);

			toolsNode.attachChild(selectionShape);
			selectionShapes.add(selectionShape);

		} else {
			LOG.info("Not attaching shape because not a bounding box (" + bound.getClass() + " = " + bound + ")");
		}
	}

	protected void detachSelectionShape() {
		for (Spatial s : selectionShapes)
			s.removeFromParent();
		selectionShapes.clear();
	}

	protected void attachSelectionShape(Spatial spat) {
		detachSelectionShape();
		if (spat instanceof Geometry) {
			attachGeometrySelection((Geometry) spat);
		} else if (spat instanceof Node) {
			NodeVisitor nv = new NodeVisitor((Node) spat);
			nv.visit(new NodeVisitor.Visit() {
				@Override
				public VisitResult visit(Spatial node) {
					if (node instanceof Geometry) {
						attachGeometrySelection((Geometry) node);
						return VisitResult.END;
					}
					return VisitResult.CONTINUE;
				}
			});
		} else {
			attachBoxSelection(spat);
		}
	}

	protected boolean shouldActivate() {
		return isEnabled() && selected;
	}

	@Override
	protected void controlUpdate(float tpf) {
		// if (selectionShape != null) {
		// selectionShape.setLocalTranslation(spatial.getWorldTranslation());
		// selectionShape.setLocalRotation(spatial.getWorldRotation());
		// }

	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

	private void updateBounds() {
	}

	private BoundingBox getBoundingBox() {
		BoundingVolume bound = spatial instanceof Geometry ? ((Geometry) spatial).getModelBound()
				: spatial.getWorldBound();
		BoundingBox bbox = (BoundingBox) bound;
		return bbox;
	}
}
