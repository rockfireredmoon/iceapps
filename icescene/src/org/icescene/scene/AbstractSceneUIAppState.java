package org.icescene.scene;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icelib.Icelib;
import org.icelib.UndoManager;
import org.icescene.IcemoonAppState;
import org.icescene.SceneConfig;
import org.icescene.SceneConstants;
import org.iceui.IceUI;
import org.iceui.controls.FancyButton;
import org.iceui.controls.color.ColorFieldControl;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.CheckBox;
import icetone.core.Container;
import icetone.core.Element.ZPriority;
import icetone.core.layout.mig.MigLayout;

public abstract class AbstractSceneUIAppState extends IcemoonAppState<IcemoonAppState> {

    private static final Logger LOG = Logger.getLogger(AbstractSceneUIAppState.class.getName());
    protected Container layer;
    private ColorFieldControl background;
    private CheckBox showGrid;
    private final UndoManager undoManager;
    private FancyButton undoButton;
    private FancyButton redoButton;

    public AbstractSceneUIAppState(UndoManager undoManager, Preferences prefs) {
        super(prefs);
        this.undoManager = undoManager;
    }

    @Override
    protected void postInitialize() {

        layer = new Container(screen);
        layer.setLayoutManager(createLayout());

        // Anything else
        addBefore();


        // Background
        background = new ColorFieldControl(screen, app.getViewPort().getBackgroundColor(), false, true, true) {
            @Override
            protected void onChangeColor(ColorRGBA newColor) {
                prefs.put(SceneConfig.DEBUG_VIEWPORT_COLOUR,
                        Icelib.toHexString(IceUI.fromRGBA(newColor)));
            }
        };
        background.setToolTipText("Background colour");
        layer.addChild(background);

        // Show grid
        showGrid = new CheckBox(screen) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                prefs.putBoolean(SceneConfig.DEBUG_GRID, toggled);
            }
        };
        showGrid.setIsCheckedNoCallback(prefs.getBoolean(SceneConfig.DEBUG_GRID, SceneConfig.DEBUG_GRID_DEFAULT));
        showGrid.setLabelText("Grid");
        layer.addChild(showGrid);

        // Undo
        undoButton = new FancyButton(screen) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                maybeDoUndo();
            }

            @Override
            public void onButtonStillPressedInterval() {
                maybeDoUndo();
            }
        };
        undoButton.setInterval(SceneConstants.UNDO_REDO_REPEAT_INTERVAL);
        undoButton.setText("Undo");
        layer.addChild(undoButton);

        // Redo
        redoButton = new FancyButton(screen) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                maybeDoRedo();
            }

            @Override
            public void onButtonStillPressedInterval() {
                maybeDoRedo();
            }
        };
        redoButton.setInterval(SceneConstants.UNDO_REDO_REPEAT_INTERVAL);
        redoButton.setText("Redo");
        layer.addChild(redoButton);

        // Anything else
        addAfter();

        //
        app.getLayers(ZPriority.NORMAL).addChild(layer);
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
        app.getLayers(ZPriority.NORMAL).removeChild(layer);
    }

    protected void addAfter() {
    }

    protected void addBefore() {
    }

    protected MigLayout createLayout() {
        return new MigLayout(screen, "fill", "push[][][][]", "[]push");
    }
}
