package org.icescene.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icescene.IcemoonAppState;

import com.jme3.font.BitmapFont;

import icetone.controls.buttons.Button;
import icetone.controls.containers.Frame;
import icetone.controls.containers.Frame.Listener;
import icetone.controls.containers.Frame.State;
import icetone.core.BaseElement;
import icetone.core.Container;
import icetone.core.ZPriority;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;

public class WindowManagerAppState extends IcemoonAppState<IcemoonAppState<?>> implements Listener {
	final static Logger LOG = Logger.getLogger(WindowManagerAppState.class.getName());

	private List<Frame> windows = new ArrayList<Frame>();
	private Map<Frame, Button> restorers = new HashMap<Frame, Button>();
	private BaseElement windowBar;
	private Container layer;
	private Frame selectedWindow;
	private boolean used;
	private Stack<Frame> history = new Stack<Frame>();

	public WindowManagerAppState(Preferences prefs) {
		super(prefs);
	}

	@Override
	protected void postInitialize() {
		List<Frame> allWindows = Frame.getWindows();
		for (Frame w : allWindows) {
			if (w.isManagedHint())
				addWindow(w);
		}
		Frame.addGlobalListener(this);
		deselectAllWindows();
		if (!allWindows.isEmpty()) {
			allWindows.get(0).setSelected(true);
		}

		if (used)
			throw new IllegalStateException("Cannot reuse a window manager.");

		layer = new Container(screen);
		layer.setLayoutManager(new BorderLayout());

		windowBar = new Container(screen);
		windowBar.setLayoutManager(new FlowLayout(4, BitmapFont.Align.Right));
		layer.addElement(windowBar, Border.SOUTH);

		app.getLayers(ZPriority.MENU).addElement(layer);
	}

	public void deselectAllWindows() {
		for (Frame w : windows) {
			w.setSelected(false);
		}
		selectedWindow = null;
	}

	@Override
	protected void onCleanup() {
		used = true;

		app.getLayers(ZPriority.MENU).removeElement(layer);
		Frame.removeGlobalListener(this);
	}

	public Frame getSelectedWindow() {
		return selectedWindow;
	}

	public List<Frame> getWindows() {
		return Collections.unmodifiableList(windows);
	}

	@Override
	public void destroyed(Frame window) {
		windows.remove(window);
		restorers.remove(window);
		history.remove(window);
		if (window.getState() == Frame.State.MINIMIZED) {
			for (BaseElement el : windowBar.getElements()) {
				if (el.getElementUserData().equals(window)) {
					windowBar.removeElement(el);
					break;
				}
			}
		}
		screen.dirtyLayout(true);
		screen.layoutChildren();
	}

	@Override
	public void closed(Frame window) {
		if (window == selectedWindow) {
			if (history.size() > 0)
				history.pop().setSelected(true);
		}
	}

	@Override
	public void stateChanged(Frame window, State oldState, State newState) {
		if (newState.equals(Frame.State.MINIMIZED)) {
			Button restorer = new Button(screen);
			restorer.onMouseReleased((e) -> {
				if (e.isLeft())
					window.restore();
			});
			if (window == selectedWindow) {
				history.remove(selectedWindow);
				if (history.size() > 0) {
					history.pop().setSelected(true);
				}
				selectedWindow = null;
			}
			restorer.setElementUserData(window);
			restorer.setText(window.getDragBar().getText());
			restorers.put(window, restorer);
			windowBar.addElement(restorer);
			screen.dirtyLayout(true);
			screen.layoutChildren();
		} else if (newState.equals(Frame.State.NORMAL)) {
			Button b = restorers.get(window);
			if (b != null) {
				windowBar.removeElement(b);
				screen.dirtyLayout(true);
				screen.layoutChildren();
			}
		}
	}

	@Override
	public void windowTitleChanged(Frame window, String oldTitle, String newTitle) {
		Button b = restorers.get(window);
		if (b != null) {
			b.setText(newTitle);
		}
	}

	@Override
	public void selected(Frame window) {
		this.selectedWindow = window;
		if (window != null) {
			history.remove(window);
			history.push(window);
			LOG.info(String.format("%s is now selected (history is now %d).", window, history.size()));
		}
		for (Frame w : windows) {
			if (w != window) {
				LOG.info(String.format("Deselecting %s", w));
				w.setSelected(false);
			}
		}
	}

	@Override
	public void created(Frame window) {
		if (window.isManagedHint())
			addWindow(window);
	}

	@Override
	public void opened(Frame window) {
	}

	protected void removeWindow(Frame window) {
		windows.remove(window);
	}

	protected void addWindow(Frame window) {
		if (windows.contains(window)) {
			LOG.warning(String.format("Window %s is already managed.", window));
		} else {
			windows.add(window);
		}
	}
}
