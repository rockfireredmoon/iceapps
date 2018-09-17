package org.icescene.tools;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icescene.IcemoonAppState;
import org.iceui.UIConstants;

import icetone.core.StyledContainer;
import icetone.core.ZPriority;
import icetone.core.layout.mig.MigLayout;

public class ActionBarsAppState extends IcemoonAppState<IcemoonAppState<?>> {
	final static Log LOG = LogFactory.getLog(ActionBarsAppState.class);

	public static final String TOOL_ACTION_PREFIX = "ToolAction-";

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

	private DragContext dragContext;
	private ToolBoxLayer toolLayer;
	private AbstractToolArea mainToolArea;
	private ToolManager toolManager;
	private final HudType hudType;
	private StyledContainer mainToolsLayer;

	public ActionBarsAppState(Preferences prefs, HudType hudType, DragContext dragContext, ToolManager toolManager) {
		super(prefs);
		this.hudType = hudType;
		this.dragContext = dragContext;
		this.toolManager = toolManager;
	}

	public HudType getHudType() {
		return hudType;
	}

	@Override
	protected final void postInitialize() {

		// Iceclient tool bar container
		toolLayer = new ToolBoxLayer(screen, prefs, hudType, toolManager, dragContext) {

			@Override
			protected void positionToolBox(ToolBox toolBox, String toolBoxName, ToolPanel toolPanel) {
				if (toolBox.getStyle() == null || toolBox.getStyle().startsWith("quickbar")) {
					super.positionToolBox(toolBox, toolBoxName, toolPanel);
				} else {
					mainToolArea.addToolBar(toolPanel);
					if (toolBox.isVisible()) {
						toolPanel.show();
					} else {
						toolPanel.hide();
					}
				}
			}

		};
		mainToolsLayer = new StyledContainer(screen);
		mainToolsLayer.setLayoutManager(new MigLayout(screen, "ins 0, gap 0", "push[]push", "push[]"));

		// If there is an options or primary abilities toolbar, then show the
		// main toolbar
		final List<ToolBox> toolBoxes = toolManager.getToolBoxes(hudType);
		if (toolBoxes == null)
			LOG.warn(String.format("No toolboxes for %s", hudType));
		else {
			for (ToolBox t : toolBoxes) {
				if (t.getStyle() != null) {
					mainToolArea = hudType.createToolArea(toolManager, screen);
					mainToolsLayer.addElement(mainToolArea);
					break;
				}
			}
		}

		toolLayer.init();

		app.getLayers(ZPriority.LAYERS).addElement(toolLayer);
		app.getLayers(ZPriority.LAYERS).addElement(mainToolsLayer);

		// app.getLayers().addChild(toolLayer);

		// TODO something still screwy about zorder
		// screen.updateZOrder(app.getLayers());
		// mainToolsLayer.bringToFront();
	}

	@Override
	protected final void onCleanup() {
		toolLayer.close();
		if (mainToolArea != null) {
			mainToolArea.destroy();
			mainToolArea.hide();
		}
		try {
			app.getAlarm().timed(new Callable<Void>() {
				public Void call() throws Exception {
					app.getLayers(ZPriority.NORMAL).removeElement(toolLayer);
					app.getLayers(ZPriority.NORMAL).removeElement(mainToolsLayer);
					return null;
				}
			}, UIConstants.UI_EFFECT_TIME + 0.1f);
		} catch (RejectedExecutionException ree) {
			// Happens on shutdown
			app.getLayers(ZPriority.NORMAL).removeElement(toolLayer);
			app.getLayers(ZPriority.NORMAL).removeElement(mainToolsLayer);
		}

	}

	public void rebuildTools(ToolBox toolBox) {
		toolLayer.rebuildTools(toolBox);
	}

}
