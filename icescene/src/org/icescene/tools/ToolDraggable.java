package org.icescene.tools;

import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.io.KeyMapManager;
import org.icescene.io.UserKeyMapping;

import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.core.layout.FillLayout;

/**
 * Draggable for tools items.
 */
public abstract class ToolDraggable extends AbstractDraggable {

	public ToolDraggable(DragContext dragContext, BaseScreen screen, ToolBox toolBox, Tool tool, int slot) {
		super(dragContext, screen, null, tool.getIcon());
		init(toolBox, tool, slot);
	}

	private void init(ToolBox toolBox, Tool tool, int slot) {
		// setStyleClass(toolBox.getStyle());
		StringBuilder tt = new StringBuilder();
		if (Icelib.isNotNullOrEmpty(tool.getHelp())) {
			tt.append(tool.getHelp());
		}
		KeyMapManager mgr = ((IcesceneApp) ToolKit.get().getApplication()).getKeyMapManager();
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
		setUseParentPseudoStyles(true);
		setDragDropDragElement(tool.isMayDrag() && toolBox.isMoveable());
		setLayoutManager(new FillLayout());

	}
}
