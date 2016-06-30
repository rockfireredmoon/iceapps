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
import org.iceui.controls.FancyButton;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.FancyWindow.Listener;
import org.iceui.controls.FancyWindow.State;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.buttons.Button;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.Element.ZPriority;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;

public class WindowManagerAppState extends IcemoonAppState<IcemoonAppState<?>> implements Listener {
	final static Logger LOG = Logger.getLogger(WindowManagerAppState.class.getName());

	private List<FancyWindow> windows = new ArrayList<FancyWindow>();
	private Map<FancyWindow, Button> restorers = new HashMap<FancyWindow, Button>();
	private Element windowBar;
	private Container layer;
	private FancyWindow selectedWindow;
	private boolean used;
	private Stack<FancyWindow> history = new Stack<FancyWindow>();

	public WindowManagerAppState(Preferences prefs) {
		super(prefs);
	}

	@Override
	protected void postInitialize() {
		List<FancyWindow> allWindows = FancyWindow.getWindows();
		for (FancyWindow w : allWindows) {
			if(w.isManagedHint())
				addWindow(w);
		}
		FancyWindow.addGlobalListener(this);
		deselectAllWindows();
		if (!allWindows.isEmpty()) {
			allWindows.get(0).setIsSelected(true);
		}

		if (used)
			throw new IllegalStateException("Cannot reuse a window manager.");

		layer = new Container(screen);
		layer.setLayoutManager(new BorderLayout());

		windowBar = new Container(screen);
		windowBar.setLayoutManager(new FlowLayout(4, BitmapFont.Align.Right));
		layer.addChild(windowBar, BorderLayout.Border.SOUTH);

		app.getLayers(ZPriority.MENU).addChild(layer);
	}

	public void deselectAllWindows() {
		for (FancyWindow w : windows) {
			w.setIsSelected(false);
		}
		selectedWindow = null;
	}

	@Override
	protected void onCleanup() {
		used = true;

		app.getLayers(ZPriority.MENU).removeChild(layer);
		FancyWindow.removeGlobalListener(this);
	}

	public FancyWindow getSelectedWindow() {
		return selectedWindow;
	}

	public List<FancyWindow> getWindows() {
		return Collections.unmodifiableList(windows);
	}

	@Override
	public void destroyed(FancyWindow window) {
		windows.remove(window);
		restorers.remove(window);
		history.remove(window);
		if (window.getState() == FancyWindow.State.MINIMIZED) {
			for (Element el : windowBar.getElements()) {
				if (el.getElementUserData().equals(window)) {
					windowBar.removeChild(el, false);
					break;
				}
			}
		}
		screen.dirtyLayout();
		screen.layoutChildren();
	}

	@Override
	public void closed(FancyWindow window) {
		if (window == selectedWindow) {
			if (history.size() > 0)
				history.pop().setIsSelected(true);
		}
	}

	@Override
	public void stateChanged(FancyWindow window, State oldState, State newState) {
		if (newState.equals(FancyWindow.State.MINIMIZED)) {
			FancyButton restorer = new FancyButton(screen) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					window.restore();
				}
			};
			if (window == selectedWindow) {
				history.remove(selectedWindow);
				if (history.size() > 0) {
					history.pop().setIsSelected(true);
				}
				selectedWindow = null;
			}
			restorer.setElementUserData(window);
			restorer.setText(window.getDragBar().getText());
			restorers.put(window, restorer);
			windowBar.addChild(restorer);
			screen.dirtyLayout();
			screen.layoutChildren();
		} else if (newState.equals(FancyWindow.State.NORMAL)) {
			Button b = restorers.get(window);
			if (b != null) {
				windowBar.removeChild(b);
				screen.dirtyLayout();
				screen.layoutChildren();
			}
		}
	}

	@Override
	public void windowTitleChanged(FancyWindow window, String oldTitle, String newTitle) {
		Button b = restorers.get(window);
		if (b != null) {
			b.setText(newTitle);
		}
	}

	@Override
	public void selected(FancyWindow window) {
		this.selectedWindow = window;
		if (window != null) {
			history.remove(window);
			history.push(window);
			LOG.info(String.format("%s is now selected (history is now %d).", window, history.size()));
		}
		for (FancyWindow w : windows) {
			if (w != window) {
				LOG.info(String.format("Deselecting %s", w));
				w.setIsSelected(false);
			}
		}
	}

	@Override
	public void created(FancyWindow window) {
		if(window.isManagedHint())
			addWindow(window);
	}

	@Override
	public void opened(FancyWindow window) {
	}

	protected void removeWindow(FancyWindow window) {
		windows.remove(window);
	}

	protected void addWindow(FancyWindow window) {
		if (windows.contains(window)) {
			LOG.warning(String.format("Window %s is already managed.", window));
		} else {
			windows.add(window);
		}
	}
}
