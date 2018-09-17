package org.icescene.tools;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.scene.Spatial;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.PseudoStyles;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.layout.FillLayout;

/**
 * Place for (tool-able) inventory items or tools to be dropped.
 */
public abstract class ToolDroppable extends Element {

	private final Tool tool;
	private final ToolBox toolBox;
	private final int slot;

	public ToolDroppable(DragContext dragContext, BaseScreen screen, ToolBox toolBox, Tool tool, int slot) {
		super(screen);
		setLayoutManager(new FillLayout());
		setTextPadding(2, 2, 2, 2);
		if (tool != null) {
			ToolDraggable toolDraggable = new ToolDraggable(dragContext, screen, toolBox, tool, slot) {

				@Override
				protected boolean doOnDragEnd(MouseButtonEvent mbe, BaseElement elmnt) {
					return doEndDraggableDrag(mbe, elmnt);
				}

				@Override
				protected boolean doOnClick(MouseButtonEvent evt) {
					return doClick(evt);
				}
			};
			toolDraggable.onComplete(evt -> onDragDropComplete(evt));
			addElement(toolDraggable);
			toolDraggable.setMovable(toolBox.isMoveable() && tool != null && tool.isMayDrag());
			toolDraggable.setDragDropDropElement(!toolBox.isConfigurable() && tool != null && tool.isMayDrag());
		} else {
			setIgnoreMouseButtons(true);
		}

		addElement(new StyledContainer(screen) {
			{
				setStyleClass("tool-overlay");
				setUseParentPseudoStyles(true);
			}
		}.setIgnoreMouse(true));

		setFocusRootOnly(false);
		setHoverable(true);
//		setMouseFocusable(true);
		setDragDropDropElement(toolBox.isConfigurable());
		this.toolBox = toolBox;
		this.tool = tool;
		this.slot = slot;
	}

	@Override
	protected void onPsuedoStateChange() {
		/*
		 * TODO only do this if any children are using parent pseudo styles.
		 * Button does the same thing
		 */
		dirtyLayout(true, LayoutType.styling);
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

	protected abstract boolean doEndDraggableDrag(MouseButtonEvent mbe, BaseElement elmnt);
}
