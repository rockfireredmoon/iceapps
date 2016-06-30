package org.icescene.tools;

import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.io.KeyMapManager;
import org.icescene.io.UserKeyMapping;

import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.listeners.MouseFocusListener;

/**
 * Draggable for tools items.
 */
public abstract class ToolDraggable extends AbstractDraggable implements MouseFocusListener {
	private Element hover;

	public ToolDraggable(DragContext dragContext, ElementManager screen, Vector2f dimensions, Vector4f resizeBorders,
			ToolBox toolBox, Tool tool, int slot) {
		super(dragContext, screen, dimensions, resizeBorders, tool.getIcon(), null);
		init(toolBox, tool, slot);
	}

	public ToolDraggable(DragContext dragContext, ElementManager screen, ToolBox toolBox, Tool tool, int slot) {
		super(dragContext, screen, UIDUtil.getUID(), tool.getIcon());
		init(toolBox, tool, slot);
	}

	private void init(ToolBox toolBox, Tool tool, int slot) {
		StringBuilder tt = new StringBuilder();
		if (Icelib.isNotNullOrEmpty(tool.getHelp())) {
			tt.append(tool.getHelp());
		}
		KeyMapManager mgr = ((IcesceneApp) app).getKeyMapManager();
		if (mgr != null && mgr.hasMapping(tool.getName())) {
			UserKeyMapping m = mgr.getMapping(tool.getName());
			String desc = m.getDescription();
			if (desc.length() > 0) {
				tt.append(" (");
				tt.append(desc);
				tt.append(")");
			}
			if (tt.length() > 0) {
				setToolTipText(tt.toString());
			}
		}
		setIsDragDropDragElement(tool.isMayDrag() && toolBox.isMoveable());

		//
		String hoverImg = screen.getStyle(toolBox.getStyle().getDroppableStyleName()).getString("hoverImg");
		if (hoverImg != null) {
			hover = new Element(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, Vector4f.ZERO, hoverImg);
			hover.setIgnoreMouse(true);
		}
	}

	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		if (hover != null) {
			addChild(hover);
		}
	}

	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		if (hover != null) {
			removeChild(hover);
		}
	}
}
