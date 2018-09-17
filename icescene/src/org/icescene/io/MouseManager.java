package org.icescene.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.RenderState;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;

import icetone.core.ToolKit;
import icetone.core.utils.Alarm;

/**
 * Centralises all mouse handling outside of the GUI. For example, keeps track
 * of whether camera drags are in action, if spatials are being dragged (and
 * which ones), and if spatials are being clicked.
 * <p>
 * Clients are expected to use
 * {@link #addListener(org.icemoon.io.MouseManager.SceneQueueListener)} and
 * react to the various events.
 *
 */
public class MouseManager extends AbstractAppState implements AnalogListener, ActionListener {

	/**
	 * User data to signal the spatial should be ignored when determining what
	 * objects are clicked
	 */
	public static final String IGNORE = null;

	private final static Trigger TRIGGER_LEFT = new MouseAxisTrigger(MouseInput.AXIS_X, false);
	private final static Trigger TRIGGER_RIGHT = new MouseAxisTrigger(MouseInput.AXIS_X, true);
	private final static Trigger TRIGGER_DOWN = new MouseAxisTrigger(MouseInput.AXIS_Y, false);
	private final static Trigger TRIGGER_UP = new MouseAxisTrigger(MouseInput.AXIS_Y, true);
	private final static String MAPPING_UP = "EditControlMoveUp";
	private final static String MAPPING_DOWN = "EditControlMoveDown";
	private final static String MAPPING_LEFT = "EditControlMoveLeft";
	private final static String MAPPING_RIGHT = "EditControlMoveRight";
	private final static List<String> MOVEMENT_MAPPINGS = Arrays.asList(MAPPING_UP, MAPPING_DOWN, MAPPING_LEFT,
			MAPPING_RIGHT);
	private Quaternion pointStartCameraLocation;
	private Node rootNode;
	private ModifierKeysAppState mods;
	private Vector3f last3d;
	private Vector2f click2d;
	private Vector3f click3d;
	private Vector3f dir;
	private Spatial hovering;
	private Listener hoveringTargetListener;
	private boolean leftMouseDown;
	private Vector3f maybeDragStart;
	private Listener dragListener;
	private Spatial draggingSpatial;
	private boolean flyCamWasEnabled;
	private int startModsMask;
	private CollisionResults results;
	private Vector3f targetContactPoint;
	private Alarm.AlarmTask repeatTask;

	public enum Mode {

		NORMAL, PLACE
	}

	public enum SelectResult {

		NO, AS_FALLBACK, YES, AS_PRIORITY
	}

	public enum Action {

		HOVERING, START_DRAG, MOVE
	}

	public interface Listener {

		SelectResult isSelectable(MouseManager manager, Spatial spatial, Action action);

		void place(MouseManager manager, Vector3f location);

		void hover(MouseManager manager, Spatial spatial, ModifierKeysAppState mods);

		void click(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask,
				Vector3f contactPoint, CollisionResults results, float tpf, boolean repeat);

		void defaultSelect(MouseManager manager, ModifierKeysAppState mods, CollisionResults collision, float tpf);

		void dragEnd(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask);

		void dragStart(Vector3f click3d, MouseManager manager, Spatial spatial, ModifierKeysAppState mods,
				Vector3f direction);

		void drag(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, Vector3f click3d,
				Vector3f lastClick3d, float tpf, int startModsMask, CollisionResults results, Vector3f lookDir);
	}

	public static class ListenerAdapter implements Listener {

		public SelectResult isSelectable(MouseManager manager, Spatial spatial, Action action) {
			return SelectResult.NO;
		}

		public void place(MouseManager manager, Vector3f location) {
		}

		public void hover(MouseManager manager, Spatial spatial, ModifierKeysAppState mods) {
		}

		public void click(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask,
				Vector3f contactPoint, CollisionResults results, float tpf, boolean repeat) {
		}

		public void defaultSelect(MouseManager manager, ModifierKeysAppState mods, CollisionResults collision,
				float tpf) {
		}

		public void dragEnd(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask) {
		}

		public void dragStart(Vector3f click3d, MouseManager manager, Spatial spatial, ModifierKeysAppState mods,
				Vector3f direction) {
		}

		public void drag(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, Vector3f click3d,
				Vector3f lastClick3d, float tpf, int startModsMask, CollisionResults results, Vector3f lookDir) {
		}
	}

	private final static Trigger TRIGGER_LMB = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
	private final static String MAPPING_LMB = "LMB";
	private SimpleApplication app;
	private List<Listener> listeners = new ArrayList<Listener>();
	private Mode mode = Mode.NORMAL;
	private Picture centerMark;
	private Spatial spatialAtCursor;
	private Listener targetListener;
	private float repeatDelay = 0;
	private float repeatInterval = 0.05f;

	public MouseManager(Node rootNode) {
		this.rootNode = rootNode;
	}

	public float getRepeatDelay() {
		return repeatDelay;
	}

	public void setRepeatDelay(float repeatDelay) {
		this.repeatDelay = repeatDelay;
	}

	public float getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(float repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			if (mode.equals(Mode.PLACE)) {
				centerMark = new Picture("CenterMark");
				centerMark.setImage(app.getAssetManager(), "Interface/crosshair.png", false);
				centerMark.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
				centerMark.scale(32f);
				centerMark.setLocalTranslation(app.getContext().getSettings().getWidth() / 2,
						app.getContext().getSettings().getHeight() / 2, 0);
				app.getGuiNode().attachChild(centerMark);
			} else {
				if (centerMark != null) {
					centerMark.removeFromParent();
				}
			}
		}
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (SimpleApplication) app;

		mods = stateManager.getState(ModifierKeysAppState.class);
		if (mods == null) {
			throw new IllegalStateException(
					getClass().getName() + " requires " + ModifierKeysAppState.class.getName() + " to be attached.");
		}

		app.getInputManager().addMapping(MAPPING_UP, TRIGGER_UP);
		app.getInputManager().addMapping(MAPPING_DOWN, TRIGGER_DOWN);
		app.getInputManager().addMapping(MAPPING_LEFT, TRIGGER_LEFT);
		app.getInputManager().addMapping(MAPPING_RIGHT, TRIGGER_RIGHT);
		app.getInputManager().addMapping(MAPPING_LMB, TRIGGER_LMB);
		app.getInputManager().addListener(this, MAPPING_LMB, MAPPING_UP, MAPPING_DOWN, MAPPING_LEFT, MAPPING_RIGHT);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		app.getFlyByCamera().setMotionAllowedListener(null);
		app.getInputManager().deleteMapping(MAPPING_LMB);
		app.getInputManager().deleteMapping(MAPPING_LEFT);
		app.getInputManager().deleteMapping(MAPPING_RIGHT);
		app.getInputManager().deleteMapping(MAPPING_UP);
		app.getInputManager().deleteMapping(MAPPING_DOWN);
		app.getInputManager().removeListener(this);
	}

	public void onAnalog(String name, float value, float tpf) {
		if (MOVEMENT_MAPPINGS.contains(name)) {
			switch (mode) {
			case PLACE:
				onPlaceAnalog(name, value, tpf);
				break;
			case NORMAL:
				onNormalAnalog(name, value, tpf);
				break;
			}
		}
	}

	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals(MAPPING_LMB)) {
			leftMouseDown = isPressed;
			switch (mode) {
			case PLACE:
				onPlaceAction(name, isPressed, tpf);
				break;
			case NORMAL:
				onNormalAction(name, isPressed, tpf);
				break;
			}
		}
	}

	public Spatial getSpatialAtCursor() {
		checkSpatialAtCursor(Action.MOVE);
		return spatialAtCursor;
	}

	public boolean isDraggingSpatial() {
		return draggingSpatial != null;
	}

	public boolean isDraggingCamera() {
		return leftMouseDown && hasCameraMoved();
	}

	public ModifierKeysAppState getMods() {
		return mods;
	}

	public void startDrag(Spatial spatialAtCursor, Listener targetListener) {
		// Tell the listener we are starting a drag
		dragListener = targetListener;
		draggingSpatial = spatialAtCursor;
		dragListener.dragStart(click3d, this, draggingSpatial, mods, dir);

		// Don't need to check again
		maybeDragStart = null;
	}

	private void onPlaceAnalog(String name, float value, float tpf) {
		// Noop right now
	}

	private void onNormalAnalog(String name, float value, float tpf) {
		loadLocations();

		// See if it appears we are starting the drag of a spatial
		if (maybeDragStart != null) {
			if (!click3d.equals(maybeDragStart)) {
				// The mouse has been dragged on the spatial for first time
				checkSpatialAtCursor(Action.START_DRAG);
				if (spatialAtCursor != null) {
					// We won't be repeating once dragging
					cancelRepeat();

					// Disable fly camera while dragging spatials
					flyCamWasEnabled = app.getFlyByCamera().isEnabled();
					app.getFlyByCamera().setEnabled(false);

					startDrag(spatialAtCursor, targetListener);
				}
			}
		}

		if (draggingSpatial != null) {
			// Do a collision as well, but only to pass on to whatever is
			// handling
			// the drag, we don't actually need it internally

			results = new CollisionResults();
			Ray ray = new Ray(click3d, dir);
			rootNode.collideWith(ray, results);

			// Dragging a spatial
			dragListener.drag(this, draggingSpatial, mods, click3d, last3d, tpf, startModsMask, results, dir);

			// Schedule repeat as well, if more dragging occurs this will get
			// cancelled
			cancelRepeat();
			if (repeatDelay > 0) {
				scheduleRepeat(repeatDelay, dragListener);
			}
		} else {
			// Either dragging camera or hovering over a spatial
			if (!isDraggingCamera()) {
				// Just check for hovering
				checkSpatialAtCursor(Action.HOVERING);
				if (spatialAtCursor != null && (hovering == null || !hovering.equals(spatialAtCursor))) {
					// Hovering over a different spatial
					hovering = spatialAtCursor;
					hoveringTargetListener = targetListener;
					targetListener.hover(this, spatialAtCursor, mods);
				} else if (spatialAtCursor == null && hovering != null) {
					// Hovering over nothing, tell the previously informed
					// listener we are no longer hovering
					hovering = null;
					hoveringTargetListener.hover(this, null, mods);
					hoveringTargetListener = null;
				}
			}
		}
		last3d = click3d.clone();
	}

	private void loadLocations() {
		click2d = app.getInputManager().getCursorPosition();
		click3d = app.getCamera().getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 0);
		dir = app.getCamera().getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 1f)
				.subtractLocal(click3d).normalizeLocal();
	}

	private void onNormalAction(String name, boolean isPressed, float tpf) {
		loadLocations();
		checkSpatialAtCursor(Action.MOVE);
		if (!isPressed) {
			cancelRepeat();
		}
		if (spatialAtCursor != null || draggingSpatial != null) {
			// A spatial was under the click
			if (!isPressed) {

				// Click released, either a "click" type click or end drag
				maybeDragStart = null;
				if (draggingSpatial != null) {
					// Drag has ended
					dragListener.dragEnd(this, draggingSpatial, mods, startModsMask);
					resetDraggingAndFlycam();
				} else {
					// Spatial clicked
					targetListener.click(this, spatialAtCursor, mods, startModsMask, targetContactPoint, results, tpf,
							false);
				}

				startModsMask = 0;
			} else {
				if (draggingSpatial == null) {
					maybeStartDrag();
				}
				if (repeatDelay > 0) {
					scheduleRepeat(repeatDelay, targetListener);
				}
			}
		} else {
			// Nothing was under click
			resetDraggingAndFlycam();
			if (isPressed) {
				startModsMask = mods.getMask();

				// "Nothing" has been clicked on. Record the camera position to
				// determine
				// if this is a "default" select when the mouse is released
				snapshotCameraLocation();
			} else if (!isPressed) {
				// Stop repeating
				cancelRepeat();

				if (!hasCameraMoved()) {
					// Click was released when "Nothing" was clicked on
					// previously,
					// and the camera hasn't moved, so this is a "default"
					// select.

					// Find where we collid, this may be useful in some contexts
					// for a default select
					// CollisionResults results = new CollisionResults();
					// Ray ray = new Ray(click3d, dir);
					// System.err.println("Collide " + click3d + " " + dir);
					// rootNode.collideWith(ray, results);

					for (Listener l : listeners) {
						l.defaultSelect(this, mods, results, tpf);
					}

					startModsMask = 0;
				}
			}
		}
	}

	private void scheduleRepeat(float delay, final Listener listener) {
		repeatTask = ToolKit.get().getAlarm().timed(new Callable<Void>() {
			public Void call() throws Exception {
				// Spatial clicked
				listener.click(MouseManager.this, spatialAtCursor, mods, startModsMask, targetContactPoint, results,
						0.1f, true);

				// Scedule another, this time at the interval
				if (repeatTask != null) {
					scheduleRepeat(repeatInterval, listener);
				}
				return null;
			}
		}, delay);
	}

	private void cancelRepeat() {
		if (repeatTask != null) {
			repeatTask.cancel();
			repeatTask = null;
		}
	}

	private void checkSpatialAtCursor(Action action) {

		results = new CollisionResults();
		Ray ray = new Ray(click3d, dir);
		rootNode.collideWith(ray, results);

		SelectResult finalResult = SelectResult.NO;
		Listener newTargetListener = null;
		Spatial targetSpatial = null;

		for (int i = 0; i < results.size(); i++) {
			final CollisionResult collision = results.getCollision(i);

			/*
			 * Ask all of the listeners if they are interested in the Collision,
			 * they can either signal definite interest, or as fallback if no
			 * other listener handles it.
			 */
			Spatial geometry = collision.getGeometry();
			if (!Boolean.TRUE.equals(geometry.getUserData(IGNORE))) {
				for (Listener l : listeners) {
					if (finalResult.equals(SelectResult.AS_PRIORITY)) {
						break;
					}
					Vector3f contactPoint = collision.getContactPoint();
					SelectResult result = l.isSelectable(this, geometry, action);
					if (result.compareTo(finalResult) > 0) {
						finalResult = result;
						newTargetListener = l;
						targetSpatial = geometry;
						targetContactPoint = contactPoint;
						if (result.equals(SelectResult.AS_PRIORITY)) {
							break;
						}
					}
				}
			}
		}

		if (newTargetListener != null) {
			spatialAtCursor = targetSpatial;
			targetListener = newTargetListener;
			return;
		}

		// Nothing selected
		spatialAtCursor = null;
		targetListener = null;
	}

	private void onPlaceAction(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			if (!hasCameraMoved()) {

				Camera cam = app.getCamera();
				Vector3f camLoc = cam.getLocation();
				Vector3f loc;

				if (results.size() > 0) {
					final CollisionResult closestCollision = results.getClosestCollision();
					loc = closestCollision.getContactPoint();
				} else {
					loc = camLoc.clone();
					loc.z -= 50f;
				}

				for (Listener l : listeners) {
					l.place(this, loc);
				}
			} else {
				// User dragged while point, wait for the next click
				snapshotCameraLocation();
			}
		} else {
			snapshotCameraLocation();
		}
	}

	private boolean hasCameraMoved() {
		return !app.getCamera().getRotation().equals(pointStartCameraLocation);
	}

	private void snapshotCameraLocation() {
		pointStartCameraLocation = app.getCamera().getRotation().clone();
	}

	private void resetDraggingAndFlycam() {
		maybeDragStart = null;
		if (draggingSpatial != null) {
			draggingSpatial = null;
			dragListener = null;
			app.getFlyByCamera().setEnabled(flyCamWasEnabled);
		}
	}

	private void maybeStartDrag() {
		// Spatial click, mark location and prepare to detect drag
		startModsMask = mods.getMask();
		draggingSpatial = null;
		dragListener = null;
		maybeDragStart = click3d.clone();
	}
}
