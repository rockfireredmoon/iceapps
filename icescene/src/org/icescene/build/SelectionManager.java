package org.icescene.build;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.icescene.io.ModifierKeysAppState;
import org.icescene.io.MouseManager;
import org.icescene.scene.AbstractBuildableControl;
import org.icescene.scene.Buildable;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Tracks currently selected {@link BuildableControl} controls. The
 * {@link MouseManager} is used to monitor mouse motion and set selections
 * accordingly.
 */
public class SelectionManager<S extends Buildable, T extends AbstractBuildableControl<S>> implements MouseManager.Listener {

	private final MouseManager mouseManager;
	private boolean selectBecauseDragged;

	public interface Listener<S extends Buildable, T extends AbstractBuildableControl<S>> {
		void selectionChanged(SelectionManager<S, T> selectionManager);
	}

	private final static Logger LOG = Logger.getLogger(SelectionManager.class.getName());
	private final List<T> selected = new ArrayList<T>();
	private List<Listener<S, T>> listeners = new ArrayList<>();
	private Class<T> controlClass;
	private boolean mouseEnabled = true;

	public SelectionManager(MouseManager mouseManager, Class<T> controlClass) {
		this.mouseManager = mouseManager;
		this.controlClass = controlClass;
		mouseManager.addListener(this);
	}

	public boolean isMouseEnabled() {
		return mouseEnabled;
	}

	public void setMouseEnabled(boolean enabled) {
		if (enabled != this.mouseEnabled) {
			if (!enabled) {
				clearSelection();
			}
			this.mouseEnabled = enabled;
		}
	}

	public MouseManager getMouseManager() {
		return mouseManager;
	}

	public void cleanup() {
		mouseManager.removeListener(this);
	}

	public void addListener(Listener<S, T> listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener<S, T> listener) {
		listeners.remove(listener);
	}

	public void setSelection(List<T> selected) {
		for (T c : new ArrayList<T>(this.selected)) {
			this.selected.remove(c);
			c.setSelected(false);
		}
		this.selected.clear();
		for (T c : selected) {
			this.selected.add(c);
			c.setSelected(true);
		}
		fireSelectionChanged();
	}

	public void select(T control, int mods) {
		if (ModifierKeysAppState.isCtrl(mods)) {
			// Toggle, don't deselect others
			if (control.isSelected()) {
				selected.remove(control);
				LOG.info(String.format("Deselected %s", control.getSpatial().getName()));
				control.setSelected(false);
			} else {
				selected.add(control);
				control.setSelected(true);
				LOG.info(String.format("Selected %s", control.getSpatial().getName()));
			}
		} else {
			// Select just this one
			clearSelection();
			selected.add(control);
			control.setSelected(true);
			LOG.info(String.format("Selected %s", control.getSpatial().getName()));
		}
		fireSelectionChanged();
	}

	public void clearSelection() {
		if (selected.size() > 0) {
			LOG.info("Clearing selection");
			for (T c : selected) {
				c.setSelected(false);
			}
			selected.clear();
			fireSelectionChanged();
		}
	}

	/**
	 * Get if the current select is the result of a "Drag Select". I.e, nothing
	 * was selected, and the user then attempted to drag a selectable.
	 * 
	 * @return select because dragged
	 */
	public boolean isSelectBecauseDragged() {
		return selectBecauseDragged;
	}

	public boolean isSelected(T control) {
		return selected.contains(control);
	}

	public List<T> getSelection() {
		return selected;
	}

	public boolean isAnySelected() {
		return !selected.isEmpty();
	}

	protected void fireSelectionChanged() {
		for (Listener<S, T> l : listeners) {
			l.selectionChanged(this);
		}
	}

	public S getFirstSelectedProp() {
		if (!selected.isEmpty()) {
			return selected.get(0).getEntity();
		}
		return null;
	}

	public MouseManager.SelectResult isSelectable(MouseManager manager, Spatial spatial, MouseManager.Action hovering) {
		if (mouseEnabled) {
			Spatial buildableSpatial = getBuildable(spatial);
			if (buildableSpatial != null) {
				return MouseManager.SelectResult.AS_FALLBACK;
			}
		}
		return MouseManager.SelectResult.NO;
	}

	public void place(MouseManager manager, Vector3f location) {
	}

	public void hover(MouseManager manager, Spatial spatial, ModifierKeysAppState mods) {
	}

	public void click(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask, Vector3f contactPoint,
			CollisionResults results, float tpf, boolean repeat) {
		if (!mouseEnabled) {
			return;
		}
		System.err.println("[CLICK] " + spatial + " / " + contactPoint);
		Spatial buildableSpatial = getBuildable(spatial);
		selectBecauseDragged = false;
		select(buildableSpatial.getControl(controlClass), startModsMask);
	}

	public void defaultSelect(MouseManager manager, ModifierKeysAppState mods, CollisionResults collision, float tpf) {
		selectBecauseDragged = false;
		clearSelection();
	}

	public void dragEnd(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask) {
	}

	public void dragStart(Vector3f click3d, MouseManager manager, Spatial spatial, ModifierKeysAppState mods, Vector3f direction) {
		if (!mouseEnabled) {
			return;
		}
		Spatial buildable = getBuildable(spatial);
		if (buildable != null && !selected.contains(buildable.getControl(controlClass))) {
			selectBecauseDragged = true;
			select(buildable.getControl(controlClass), mods.getMask());
		} else {
			selectBecauseDragged = false;
		}
	}

	public void drag(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, Vector3f click3d, Vector3f lastClick3d,
			float tpf, int startModsMask, CollisionResults results, Vector3f lookDir) {
	}

	private Spatial getBuildable(Spatial spatial) {
		while (spatial != null) {
			if (spatial.getControl(controlClass) != null) {
				return spatial;
			}
			spatial = spatial.getParent();
		}
		return null;
	}
}
