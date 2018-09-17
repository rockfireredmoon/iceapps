package org.icescene.scene;

import java.util.prefs.Preferences;

import org.icelib.Icelib;
import org.icescene.IcemoonAppState;
import org.icescene.SceneConfig;
import org.icescene.SceneConstants;
import org.iceui.IceUI;
import org.iceui.actions.ActionAppState;
import org.iceui.actions.ActionMenu;
import org.iceui.actions.ActionMenuBar;
import org.iceui.actions.AppAction;
import org.iceui.actions.AppAction.Style;

import icetone.core.StyledContainer;
import icetone.core.ZPriority;
import icetone.core.layout.mig.MigLayout;
import icetone.core.undo.UndoManager;
import icetone.extras.chooser.ColorFieldControl;

public abstract class AbstractSceneUIAppState extends IcemoonAppState<IcemoonAppState<?>> {

	protected StyledContainer layer;
	protected ActionMenuBar menuBar;

	private final UndoManager undoManager;
	private ActionMenu editMenu;
	private ActionMenu viewMenu;
	private AppAction showGridAction;
	private AppAction redoAction;
	private AppAction undoAction;

	public AbstractSceneUIAppState(UndoManager undoManager, Preferences prefs) {
		super(prefs);
		this.undoManager = undoManager;
	}

	@Override
	protected void postInitialize() {

		ActionAppState appState = app.getStateManager().getState(ActionAppState.class);
		menuBar = appState == null ? null : appState.getMenuBar();

		if (menuBar != null) {
			menuBar.invalidate();
			menuBar.addActionMenu(editMenu = new ActionMenu("Edit", 5));
			menuBar.addActionMenu(viewMenu = new ActionMenu("View", 7));
			menuBar.addAction(undoAction = new AppAction("Undo", evt -> maybeDoUndo()).setMenu("Edit")
					.setInterval(SceneConstants.UNDO_REDO_REPEAT_INTERVAL));
			menuBar.addAction(redoAction = new AppAction("Redo", evt -> maybeDoRedo()).setMenu("Edit")
					.setInterval(SceneConstants.UNDO_REDO_REPEAT_INTERVAL));
			menuBar.addAction(showGridAction = new AppAction("Show Grid", evt -> {
				prefs.putBoolean(SceneConfig.DEBUG_GRID, evt.getSourceAction().isActive());
			}).setMenu("View").setStyle(Style.TOGGLE)
					.setActive(prefs.getBoolean(SceneConfig.DEBUG_GRID, SceneConfig.DEBUG_GRID_DEFAULT)));

			menuBar.addAction(showGridAction = new AppAction("Background").setMenu("View")
					.setElement(new ColorFieldControl(screen, app.getViewPort().getBackgroundColor(), true, true)
							.onChange(evt -> prefs.put(SceneConfig.DEBUG_VIEWPORT_COLOUR,
									Icelib.toHexString(IceUI.fromRGBA(evt.getNewValue()))))));
			menuBar.validate();
		}

		layer = new StyledContainer(screen);
		layer.setLayoutManager(createLayout());

		// Anything else
		addBefore();

		// Anything else
		addAfter();

		//
		app.getLayers(ZPriority.NORMAL).addElement(layer);
	}

	protected void maybeDoUndo() {
		if (undoManager.isUndoAvailable()) {
			undoManager.undo();
		} else {
			info("Nothing more to undo.");
		}
	}

	protected void maybeDoRedo() {
		if (undoManager.isRedoAvailable()) {
			undoManager.redo();
		} else {
			info("Nothing more to redo.");
		}
	}

	@Override
	protected void onCleanup() {
		if (menuBar != null) {
			menuBar.removeAction(undoAction);
			menuBar.removeAction(redoAction);
			menuBar.removeAction(showGridAction);
			menuBar.removeActionMenu(editMenu);
			menuBar.removeActionMenu(viewMenu);
		}
		app.getLayers(ZPriority.NORMAL).removeElement(layer);
	}

	protected void addAfter() {
	}

	protected void addBefore() {
	}

	protected abstract MigLayout createLayout();
}
