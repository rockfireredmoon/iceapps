package org.icescene.build;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.EditDirection;
import org.icescene.entities.EntityContext;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.io.MouseManager;
import org.icescene.materials.MaterialUtil;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * Control that when attached to a spatial will attach a <b>Manipulator</b> to
 * the scene the allows the spatial to be moved, rotated and scaled.
 * <p>
 * When the control is detached, the manipulator will also be removed from the
 * scene.
 * 
 * http://hub.jmonkeyengine.org/forum/topic/3d-dragging-not-working-at-all/
 * http://hub.jmonkeyengine.org/wiki/doku.php/jme3:math
 */
public class ObjectManipulatorControl extends AbstractControl implements MouseManager.Listener {

	static final Logger LOG = Logger.getLogger(ObjectManipulatorControl.class.getName());
	// TODO I have NO idea why this compensation is needed or if it will work
	// everwhere
	private final static float ROTATE_SPEED = 10f;
	private final static float PULSE_SPEED = 5f;
	private final static ColorRGBA HIGHLIGHT_COLOR = ColorRGBA.White.mult(1.5f);
	final static ColorRGBA DESELECTED_COLOR = ColorRGBA.Cyan.mult(0.75f);

	static {
		HIGHLIGHT_COLOR.a = 0.85f;
		DESELECTED_COLOR.a = 1f;
	}
	private final Node rootNode;
	private final MouseManager mouseManager;
	private final SimpleApplication app;
	private Manipulator manipulator;
	private ColorRGBA currentColor;
	private ColorRGBA targetColor;
	private Plane dragPlane;
	private float time;
	private EditDirection direction;
	private Node handle;
	private boolean dragJustStarted;
	private Vector3f lastPlaneClick;

	public ObjectManipulatorControl(Node rootNode, MouseManager mouseManager, SimpleApplication app) {
		this.rootNode = rootNode;
		this.mouseManager = mouseManager;
		this.app = app;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		if (this.spatial != null) {
			manipulator.getSpatial().removeFromParent();
			mouseManager.removeListener(this);
			resetHovering();
		}
		super.setSpatial(spatial);
		if (spatial != null) {
			mouseManager.addListener(this);
			LOG.info(String.format("Attaching manipulator spatial to %s", rootNode));
			manipulator = new Manipulator(EntityContext.create(app));
			manipulator.configureProp();
			rootNode.attachChild(manipulator.getSpatial());
		}
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (spatial != null && manipulator != null) {
			manipulator.getSpatial().setLocalTranslation(spatial.getLocalTranslation().clone());
		}
		if (handle != null) {
			time += tpf * PULSE_SPEED;
			currentColor.interpolateLocal(targetColor, tpf * PULSE_SPEED);
			if (time >= 1) {
				time = 0;
				// Can interpolate no further
				if (targetColor.equals(HIGHLIGHT_COLOR)) {
					targetColor = DESELECTED_COLOR;
				} else {
					targetColor = HIGHLIGHT_COLOR;
				}
			}
			setColorsForDirection(currentColor, direction);
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

	private void setColorsForDirection(ColorRGBA col, EditDirection dir) {
		for (Spatial s : manipulator.getSpatialsForDirection(direction)) {
			final Geometry name1 = (Geometry) (((Node) s).getChild(0));
			MaterialUtil.setColor(name1, col);
		}
	}

	private void resetHovering() {
		if (direction != null) {
			setColorsForDirection(DESELECTED_COLOR, direction);
			handle = null;
			direction = null;
			targetColor = null;
			currentColor = null;
		}
	}

	@Override
	public final Control cloneForSpatial(Spatial spatial) {
		// We won't actually be using this
		ObjectManipulatorControl omc = new ObjectManipulatorControl(rootNode, mouseManager, app);
		omc.setEnabled(false);
		return omc;
	}

	/**
	 * Clone the actual spatial and/or allow sub-classes to hook into the clone
	 */
	protected void cloneSpatial() {
		Spatial newSpatial = spatial.clone(true);
		newSpatial.removeControl(ObjectManipulatorControl.class);
		rootNode.attachChild(newSpatial);
	}

	/**
	 * Apply any moves, rotations or scales.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 */
	protected void apply() {
	}

	/**
	 * Move the actual spatial and/or allow sub-classes to hook into the drag.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 */
	protected void moveSpatial(float x, float y, float z) {
		spatial.move(x, y, z);
	}

	/**
	 * Rotate the actual spatial and/or allow sub-classes to hook into the drag.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 */
	protected void rotateSpatial(float x, float y, float z) {
		spatial.rotate(x, y, z);
	}

	protected Spatial getDraggableSpatial(MouseManager manager, Spatial spatial) {
		while (spatial != null) {
			if (spatial.equals(this.spatial)) {
				return spatial;
			}
			spatial = spatial.getParent();
		}
		return null;
	}

	protected boolean isDraggableSpatial(MouseManager manager, Spatial spatial) {
		Spatial ds = getDraggableSpatial(manager, spatial);
		if (ds != null) {
			return true;
		}
		return false;
	}

	public MouseManager.SelectResult isSelectable(MouseManager manager, Spatial spatial, MouseManager.Action action) {
		if ("NoDepthShaded".equals(spatial.getName()) && spatial.getParent() != null
				&& spatial.getParent().getName().startsWith("EditCursor-")) {
			return MouseManager.SelectResult.AS_PRIORITY;
		}
		if (!action.equals(MouseManager.Action.HOVERING) && isDraggableSpatial(manager, spatial)) {
			return MouseManager.SelectResult.YES;
		}

		return MouseManager.SelectResult.NO;
	}

	public void place(MouseManager manager, Vector3f location) {
	}

	public void hover(MouseManager manager, Spatial spatial, ModifierKeysAppState mods) {
		checkSelectedSpatial(spatial);
	}

	public void click(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask,
			Vector3f contactPoint, CollisionResults results, float tpf, boolean repeat) {
	}

	public void defaultSelect(MouseManager manager, ModifierKeysAppState mods, CollisionResults collision, float tpf) {
	}

	public void dragEnd(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask) {
		dragJustStarted = false;
		apply();
		dragPlane = null;
	}

	public void dragStart(Vector3f click3d, MouseManager manager, Spatial spatial, ModifierKeysAppState mods,
			Vector3f lookDir) {
		dragJustStarted = true;
		Spatial ds = getDraggableSpatial(manager, spatial);
		if (direction == null && ds == null) {
			checkSelectedSpatial(spatial);
		}
		if (ds != null) {
			if (handle != null) {
				resetHovering();
			}
			LOG.info(String.format("Dragging actual target starting at %s", this.spatial.getWorldTranslation()));
			direction = EditDirection.XZ;
			createPlane(this.spatial, click3d, lookDir, Vector3f.UNIT_Y);
		} else {
			createPlane(spatial, click3d, lookDir, direction.toPlaneNormal());
		}
	}

	private void createPlane(Spatial spatial, Vector3f click3d, Vector3f lookDir, Vector3f normal) {
		float constant = spatial.getWorldTranslation().dot(normal);
		dragPlane = new Plane(normal, constant);
		lastPlaneClick = calcPlaneClick(lookDir, click3d);
	}

	private Vector3f calcPlaneClick(Vector3f lookDir, Vector3f origin) {
		Ray ray = new Ray(origin, lookDir);
		Vector3f where = new Vector3f();
		ray.intersectsWherePlane(dragPlane, where);
		// manage wrong direction because of object position
		if (where.equals(Vector3f.ZERO)) {
			ray.setDirection(lookDir.mult(-1));
			ray.intersectsWherePlane(dragPlane, where);
		}
		return where;
	}

	public void drag(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, Vector3f click3d,
			Vector3f lastClick3d, float tpf, int startModsMask, CollisionResults results, Vector3f lookDir) {
		Vector3f camLoc = app.getCamera().getLocation();
		Vector3f spcLoc = spatial.getWorldTranslation();
		float len = spcLoc.subtract(camLoc).length();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Drag " + click3d + " - " + lastClick3d + " = " + spatial + " cam loc: " + camLoc + " spcLoc: "
					+ spcLoc + " len:" + len);
		}

		if (dragJustStarted) {
			if (ModifierKeysAppState.isCtrl(startModsMask)) {
				cloneSpatial();
			}
			dragJustStarted = false;
		}

		// Calculate amount to move by
		Vector3f newPlaneClick = calcPlaneClick(lookDir, click3d);
		Vector3f moveBy = newPlaneClick.subtract(lastPlaneClick);
		lastPlaneClick = newPlaneClick;

		if (direction.equals(EditDirection.XZ)) {
			moveSpatial(moveBy.x, moveBy.y, moveBy.z);
		} else {
			if (direction.isMoveAxis()) {
				switch (direction) {
				case X:
					moveSpatial(moveBy.x, 0, 0);
					break;
				case Y:
					moveSpatial(0, moveBy.y, 0);
					break;
				default:
					moveSpatial(0, 0, moveBy.z);
					break;
				}
			} else if (direction.isRotate()) {

				float d1 = dragPlane.pseudoDistance(click3d);
				float d2 = dragPlane.pseudoDistance(lastClick3d);
				float di = d1 - d2;
				switch (direction) {
				case RY:
					rotateSpatial(0, di * ROTATE_SPEED, 0);
					break;
				case RP:
					rotateSpatial(di * ROTATE_SPEED * -1f, 0, 0);
					break;
				default:
					rotateSpatial(0, 0, di * ROTATE_SPEED * -1f);
					break;
				}
			}
		}
	}

	private void checkSelectedSpatial(Spatial spatial) throws IllegalStateException {
		if (spatial != null) {
			Spatial newHandle = spatial.getParent();
			if (handle == null || !handle.equals(newHandle)) {
				if (handle != null) {
					resetHovering();
				}
				String dirName = newHandle.getName().substring(11);
				if (dirName.equals("Up") || dirName.equals("Down")) {
					direction = EditDirection.Y;
				} else if (dirName.equals("Left") || dirName.equals("Right")) {
					direction = EditDirection.X;
				} else if (dirName.equals("Forward") || dirName.equals("Backward")) {
					direction = EditDirection.Z;
				} else if (dirName.startsWith("RotateYaw")) {
					direction = EditDirection.RY;
				} else if (dirName.startsWith("RotatePitch")) {
					direction = EditDirection.RP;
				} else if (dirName.startsWith("RotateRoll")) {
					direction = EditDirection.RR;
				} else {
					throw new IllegalStateException("Unexpected edit cursor handle found.");
				}

				handle = (Node) newHandle;
				targetColor = HIGHLIGHT_COLOR;
				currentColor = DESELECTED_COLOR.clone();
			}
		} else {
			resetHovering();
		}
	}
}
