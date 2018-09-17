package org.icescene.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.icescene.IcesceneApp;
import org.icescene.io.KeyMapManager;
import org.icescene.io.ModifierKeysAppState;
import org.iceui.controls.UIUtil;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;

import icetone.controls.containers.Panel;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ToolKit;
import icetone.core.event.ElementEvent.Type;
import icetone.core.layout.BasicLayout;
import icetone.extras.util.ExtrasUtil;

public class ToolBoxLayer extends Element {

	private DragContext dragContext;
	private PreferenceChangeListener toolBoxPreferenceListener;
	private ToolManager toolManager;

	public class DropDetails {

		ToolManager.DragOperation op;
		ToolBox sourceToolBox;
		Tool draggedTool;
		int sourceSlot;
		ToolBox targetToolBox;
		Tool currentTool;
		int slot;

		public DropDetails(ToolManager.DragOperation op, ToolBox sourceToolBox, Tool draggedTool, int sourceSlot,
				ToolBox targetToolBox, Tool currentTool, int slot) {
			this.op = op;
			this.sourceToolBox = sourceToolBox;
			this.draggedTool = draggedTool;
			this.sourceSlot = sourceSlot;
			this.targetToolBox = targetToolBox;
			this.currentTool = currentTool;
			this.slot = slot;
		}
	}

	private final static Logger LOG = Logger.getLogger(ToolBoxLayer.class.getName());
	private final HudType hudType;
	private Map<ToolBox, ToolPanel> toolPanels = new HashMap<ToolBox, ToolPanel>();
	private Map<ToolBox, ToolBoxListener> toolListeners = new HashMap<ToolBox, ToolBoxListener>();
	private Preferences prefs;

	public ToolBoxLayer(BaseScreen screen, Preferences prefs, HudType hudType, ToolManager toolManager,
			DragContext dragContext) {
		super(screen);
		setAsContainerOnly();
		setLayoutManager(new BasicLayout());

		this.prefs = prefs;
		this.toolManager = toolManager;
		this.dragContext = dragContext;
		this.hudType = hudType;

		toolBoxPreferenceListener = new PreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent pce) {
				if (pce.getKey().equals("visible")) {
					String toolBoxName = pce.getNode().name();
					String hudName = pce.getNode().parent().name();
					if (hudName.equals(ToolBoxLayer.this.hudType.name())) {
						ToolBox tb = ToolBoxLayer.this.toolManager.getToolBox(ToolBoxLayer.this.hudType, toolBoxName);
						Panel tp = toolPanels.get(tb);
						if (pce.getNewValue().equals(String.valueOf(true))) {
							tp.show();
						} else {
							tp.hide();
						}
					}
				}
			}
		};
	}

	public HudType getHudType() {
		return hudType;
	}

	public final void close() {
		final List<ToolBox> toolBoxes = toolManager.getToolBoxes(hudType);
		if (toolBoxes != null) {
			for (final ToolBox toolBox : toolBoxes) {
				Panel tp = toolPanels.get(toolBox);
				LOG.info(String.format("Removing tool box %s", tp.getStyleId()));
				screen.removeElement(tp);
				toolBox.getPreferencesNode().removePreferenceChangeListener(toolBoxPreferenceListener);
				cleanUpTools(toolBox);
			}
		}
	}

	public void init() {
		final List<ToolBox> toolBoxes = toolManager.getToolBoxes(hudType);
		if (toolBoxes != null) {
			for (final ToolBox toolBox : toolBoxes) {
				toolBox(toolBox);
			}
		}
	}

	protected void rebuildAll() {
		final List<ToolBox> toolBoxes = toolManager.getToolBoxes(hudType);
		if (toolBoxes != null) {
			for (final ToolBox toolBox : toolBoxes) {
				rebuildTools(toolBox);
			}
		}
	}

	protected void toolBox(final ToolBox toolBox) {

		// TODO remove
		toolBox.getPreferencesNode().addPreferenceChangeListener(toolBoxPreferenceListener);

		LOG.info(String.format("Adding toolbox %s", toolBox));
		final String toolBoxName = "Tools:" + toolBox.getName();
		// Create the window for the tools
		ToolPanel toolPanel = new ToolPanel(prefs, dragContext, toolBox, toolBoxName, screen) {
			@Override
			protected void onToolDragDropComplete(MouseButtonEvent evt) {
				rebuildAll();
			}
		};
		toolPanel.onElementEvent(evt -> ExtrasUtil.saveWindowPosition(toolBox.getPreferencesNode(), this, toolBoxName),
				Type.MOVED);
		toolPanel.setStyleClass(toolBox.getStyle());
		toolPanels.put(toolBox, toolPanel);
		rebuildTools(toolBox);
		positionToolBox(toolBox, toolBoxName, toolPanel);
	}

	protected void positionToolBox(final ToolBox toolBox, final String toolBoxName, ToolPanel toolPanel) {
		toolPanel.sizeToContent();
		// Work out it's default position (or restore it's last position)
		Vector2f defaultPosition = new Vector2f(toolPanel.getPixelPosition());
		if (toolBox.getDefaultVerticalPosition() != null) {
			switch (toolBox.getDefaultVerticalPosition()) {
			case Bottom:
				defaultPosition.y = screen.getHeight() - toolPanel.getHeight() - 8;
				break;
			case Center:
				defaultPosition.y = (screen.getHeight() / 2 - (toolPanel.getHeight() / 2));
				break;
			case Top:
				defaultPosition.y = 8;
				break;
			}
		}

		if (toolBox.getDefaultHorizontalPosition() != null) {
			switch (toolBox.getDefaultHorizontalPosition()) {
			case Right:
				defaultPosition.x = screen.getWidth() - toolPanel.getWidth() - 8;
				break;
			case Center:
				defaultPosition.x = (screen.getWidth() / 2) - (toolPanel.getWidth() / 2);
				break;
			case Left:
				defaultPosition.x = 8;
				break;
			}
		}
		addElement(toolPanel, null, !toolBox.isVisible(), -1);
		if (toolBox.isVisible()) {
			toolPanel.show();
		} else {
			toolPanel.hide();
		}
		if (toolBox.isPersistent())
			UIUtil.position(toolBox.getPreferencesNode(), toolPanel, toolBoxName, defaultPosition);
		else
			toolPanel.setPosition(defaultPosition);

	}

	/**
	 * Each toolbox has it's own ActionListener to make it easier to remove when
	 * the toolbox gets rebuilt
	 */
	class ToolBoxListener implements ActionListener {

		public void onAction(String name, boolean isPressed, float tpf) {
			if (!isPressed) {
				String toolName = name;
				LOG.info(String.format("Looking for tool %s", toolName));
				Tool tool = toolManager.getTool(hudType, toolName);
				if (tool != null) {
					LOG.info(String.format("Got tool %s", toolName));
					int mask = ToolKit.get().getApplication().getStateManager().getState(ModifierKeysAppState.class)
							.getMask();
					if (((IcesceneApp) ToolKit.get().getApplication()).getKeyMapManager().isMapped(name,
							tool.getName())) {
						tool.actionPerformed(new ActionData((IcesceneApp) ToolKit.get().getApplication(), 0, 0));
					}
				}
			}
		}
	}

	protected void cleanUpTools(ToolBox toolBox) {
		Panel toolPanel = toolPanels.get(toolBox);

		// Remove existing key mappings
		ToolBoxListener l = toolListeners.get(toolBox);
		if (l != null) {
			((IcesceneApp) ToolKit.get().getApplication()).getKeyMapManager().removeListener(l);
		}
		for (Spatial s : toolPanel.getChildren()) {
			if (s instanceof ToolDroppable) {
				ToolDroppable td = (ToolDroppable) s;
				Tool tool = td.getTool();
				if (tool != null) {
					if (((IcesceneApp) ToolKit.get().getApplication()).getKeyMapManager().hasMapping(tool.getName())) {
						((IcesceneApp) ToolKit.get().getApplication()).getKeyMapManager().deleteMapping(tool.getName());
					}
				}
			}
		}

		toolPanel.removeAllChildren();
	}

	public ToolPanel getTools(ToolBox toolBox) {
		return toolPanels.get(toolBox);
	}

	public void rebuildTools(ToolBox toolBox) {
		ToolPanel toolPanel = toolPanels.get(toolBox);
		cleanUpTools(toolBox);
		ToolBoxListener l = toolListeners.get(toolBox);
		if (l == null) {
			l = new ToolBoxListener();
			toolListeners.put(toolBox, l);
		}
		int s = 0;
		KeyMapManager keyMapManager = ((IcesceneApp) ToolKit.get().getApplication()).getKeyMapManager();
		for (final Tool tool : toolBox.getTools()) {

			if (tool != null && keyMapManager != null) {
				if (keyMapManager.hasMapping(tool.getName())) {
					keyMapManager.addMapping(tool.getName());
					keyMapManager.addListener(l, tool.getName());
				}
			}

			toolPanel.addTool(tool, s++);
		}

	}
}
