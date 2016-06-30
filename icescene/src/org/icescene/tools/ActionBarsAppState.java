package org.icescene.tools;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.prefs.Preferences;

import org.icescene.IcemoonAppState;
import org.iceui.UIConstants;

import icetone.core.Container;
import icetone.core.Element.ZPriority;
import icetone.core.layout.mig.MigLayout;

public class ActionBarsAppState extends IcemoonAppState<IcemoonAppState<?>> {

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
	private Container mainToolsLayer;

	public ActionBarsAppState(Preferences prefs, HudType hudType, DragContext dragContext, ToolManager toolManager) {
		super(prefs);
		this.hudType = hudType;
		this.dragContext  =dragContext;
		this.toolManager =toolManager;
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

				if (toolBox.getStyle().equals(ToolBox.Style.Tools)) {
					super.positionToolBox(toolBox, toolBoxName, toolPanel);
				} else {
					toolPanel.setPosition(screen.getStyle(toolBox.getStyle().getWindowStyleName()).getVector2f("defaultPosition"));
					toolPanel.setDimensions(screen.getStyle(toolBox.getStyle().getWindowStyleName()).getVector2f("defaultSize"));
					mainToolArea.addToolBar(toolPanel);
					if (toolBox.isVisible()) {
						toolPanel.show();
					} else {
						toolPanel.hide();
					}
				}
			}

		};
		mainToolsLayer = new Container(screen);
		mainToolsLayer.setLayoutManager(new MigLayout(screen, "ins 0, gap 0", "push[]push", "push[]"));

		// If there is an options or primary abilities toolbar, then show the
		// main toolbar
		final List<ToolBox> toolBoxes = toolManager.getToolBoxes(hudType);
		for (ToolBox t : toolBoxes) {
			if (t.getStyle().equals(ToolBox.Style.Options) || t.getStyle().equals(ToolBox.Style.PrimaryAbilities) || t.getStyle().equals(ToolBox.Style.BuildTools)) {
				mainToolArea = hudType.createToolArea(toolManager, screen);
				mainToolsLayer.addChild(mainToolArea);
				break;
			}
		}

		toolLayer.init();
		
		screen.addElement(toolLayer);

//		app.getLayers().addChild(mainToolsLayer);
//		app.getLayers().addChild(toolLayer);
		
		// TODO something still screwy about zorder
//		screen.updateZOrder(app.getLayers());
//		mainToolsLayer.bringToFront();
	}

	@Override
	protected final void onCleanup() {
		toolLayer.close();
		if (mainToolArea != null) {
			mainToolArea.destroy();
			mainToolArea.hideWithEffect();
		}
		try {
			app.getAlarm().timed(new Callable<Void>() {
				public Void call() throws Exception {
					app.getLayers(ZPriority.NORMAL).removeChild(toolLayer);
					app.getLayers(ZPriority.NORMAL).removeChild(mainToolsLayer);
					return null;
				}
			}, UIConstants.UI_EFFECT_TIME + 0.1f);
		} catch (RejectedExecutionException ree) {
			// Happens on shutdown
			app.getLayers(ZPriority.NORMAL).removeChild(toolLayer);
			app.getLayers(ZPriority.NORMAL).removeChild(mainToolsLayer);
		}

	}

	public void rebuildTools(ToolBox toolBox) {
		toolLayer.rebuildTools(toolBox);
	}

}
