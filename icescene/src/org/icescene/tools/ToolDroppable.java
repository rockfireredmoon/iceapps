package org.icescene.tools;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.scene.Spatial;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

/**
 * Place for (tool-able) inventory items or tools to be dropped.
 */
public abstract class ToolDroppable extends Element {

	private final Tool tool;
	private final ToolBox toolBox;
	private final int slot;

	public ToolDroppable(DragContext dragContext, ElementManager screen, ToolBox toolBox, Tool tool, int slot) {
		super(screen, UIDUtil.getUID(), screen.getStyle(toolBox.getStyle().getDroppableStyleName()).getVector2f("defaultSize"),
				screen.getStyle(toolBox.getStyle().getDroppableStyleName()).getVector4f("resizeBorders"), screen.getStyle(
						toolBox.getStyle().getDroppableStyleName()).getString("defaultImg"));
		setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[]", "[]"));
		if (tool != null) {
			ToolDraggable toolDraggable = new ToolDraggable(dragContext, screen, screen.getStyle(
					toolBox.getStyle().getDroppableStyleName()).getVector2f("toolDefaultSize"), screen.getStyle(
					toolBox.getStyle().getDroppableStyleName()).getVector4f("toolResizeBorders"), toolBox, tool, slot) {

				@Override
				public void onMouseLeftReleased(MouseButtonEvent evt) {
					super.onMouseLeftReleased(evt);
					onDragDropComplete(evt);
				}

				@Override
				protected boolean doOnDragEnd(MouseButtonEvent mbe, Element elmnt) {
					return doEndDraggableDrag(mbe, elmnt);
				}

				@Override
				protected boolean doOnClick(MouseButtonEvent evt) {
					return doClick(evt);
				}
			};
			addChild(toolDraggable);
			
			toolDraggable.setIsMovable(toolBox.isMoveable() && tool != null && tool.isMayDrag());
//			toolDraggable.setIsDragDropDropElement(!toolBox.isConfigurable() && tool != null && tool.isMayDrag());
		} else {
			setIgnoreMouseButtons(true);
		}
//		setIsDragDropDropElement(toolBox.isConfigurable());
		this.toolBox = toolBox;
		this.tool = tool;
		this.slot = slot;
	}

	public ToolDraggable getDraggable() {
		for (Spatial s : getChildren()) {
			if (s instanceof ToolDraggable) {
				return ((ToolDraggable) s);
			}
		}
		return null;
	}

	public int getSlot() {
		return slot;
	}

	public ToolBox getToolBox() {
		return toolBox;
	}

	public Tool getTool() {
		return tool;
	}

	protected void onDragDropComplete(MouseButtonEvent evt) {

	}

	protected abstract boolean doClick(MouseButtonEvent evt);

	protected abstract boolean doEndDraggableDrag(MouseButtonEvent mbe, Element elmnt);
}
