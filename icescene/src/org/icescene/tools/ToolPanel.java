package org.icescene.tools;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.iceui.controls.FancyDialogBox;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.UIUtil;
import org.iceui.controls.ZMenu;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.RadioButton;
import icetone.controls.buttons.RadioButtonGroup;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.listeners.MouseButtonListener;

public class ToolPanel extends Panel implements MouseButtonListener {

	private final static Logger LOG = Logger.getLogger(ToolPanel.class.getName());

	private ZMenu layoutMenu;
	private final DragContext dragContext;
	private final ToolBox toolBox;

	public ToolPanel(final Preferences prefs, DragContext dragContext, final ToolBox toolBox, String toolBoxName,
			final ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		super(screen, UID, position, LUtil.LAYOUT_SIZE,
				screen.getStyle(toolBox.getStyle().getWindowStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(toolBox.getStyle().getWindowStyleName()).getString("defaultImg"));
		this.dragContext = dragContext;
		this.toolBox = toolBox;
		UIUtil.cleanUpWindow(this);
		final String lockKey = toolBoxName + SceneConfig.WINDOW_LOCK;
		final boolean lock = prefs.getBoolean(lockKey, false);

		// Layout Menu
		if (toolBox.isConfigurable()) {
			layoutMenu = new ZMenu(screen) {
			};
			final int[] sizes = new int[] { 1, 8, 2, 4 };
			CheckBox lockEl = new CheckBox(screen) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					ToolPanel.this.setIsMovable(!toggled);
					prefs.putBoolean(lockKey, toggled);
					System.out.println("locked@: " + toggled);
					layoutMenu.close();
				}
			};
			lockEl.setIsCheckedNoCallback(lock);
			layoutMenu.addMenuItem("Lock", lockEl, lock);
			RadioButtonGroup bg = new RadioButtonGroup(screen) {
				@Override
				public void onSelect(int paramInt, Button paramButton) {
					setLayoutManager(new MigLayout(screen, "ins 0, fill, gap 0, gap 0, wrap " + sizes[paramInt]));
					ToolPanel.this.sizeToContent();
					toolBox.setHorizontalCells(sizes[paramInt]);
					layoutMenu.close();
				}
			};
			for (int sz : sizes) {
				RadioButton rb = new RadioButton(screen);
				if (sz == toolBox.getHorizontalCells()) {
					rb.setIsCheckedNoCallback(true);
				}
				bg.addButton(rb);
				layoutMenu.addMenuItem(sz + "x" + (8 / sz), rb, sz);
			}
			layoutMenu.setDestroyOnHide(false);
			screen.addElement(layoutMenu);
		}

		setIsResizable(false);
		setIsMovable(!lock && toolBox.isMoveable());
		setLayoutManager(new MigLayout(screen, "ins 0, wrap " + toolBox.getHorizontalCells() + ", fill, gap 0"));
		// Show menu

	}

	@Override
	public void moveTo(float x, float y) {
		// TODO Auto-generated method stub
		super.moveTo(x, y);
	}

	public void addTool(final Tool tool, int s) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Adding tool %s", tool == null ? "EMPTY" : tool.getName()));
		}
		addChild(new ToolDroppable(dragContext, screen, toolBox, tool, s) {

			@Override
			protected void onDragDropComplete(MouseButtonEvent evt) {
				onToolDragDropComplete(evt);
			}

			@Override
			protected boolean doClick(MouseButtonEvent evt) {
				tool.actionPerformed(new ActionData((IcesceneApp) app, evt.getX(), evt.getY()));
				return true;
			}

			@Override
			protected boolean doEndDraggableDrag(MouseButtonEvent mbe, Element elmnt) {
				// Look at what is at target element
				final ToolDroppable thisTool = this;
				if (elmnt == null) {
					if (getTool().isTrashable()) {
						final FancyDialogBox dialog = new FancyDialogBox(screen, new Vector2f(15, 15), FancyWindow.Size.LARGE,
								true) {
							@Override
							public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
								hideWindow();
							}

							@Override
							public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
								getToolBox().trash(getTool());
								screen.removeElement(thisTool.getDraggable());
								hideWindow();
							}
						};
						dialog.setDestroyOnHide(true);
						dialog.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
						dialog.setWindowTitle("Trash Tool");
						dialog.setButtonOkText("Trash");
						dialog.setMsg("Are you sure you want to trash " + getTool().getName());
						dialog.sizeToContent();
						dialog.setIsResizable(false);
						dialog.setIsMovable(false);
						dialog.sizeToContent();
						UIUtil.center(screen, dialog);
						screen.addElement(dialog, null, true);
						dialog.showAsModal(true);

						return false;
					}
				} else if (elmnt instanceof ToolDroppable) {
					ToolDroppable toolDroppable = (ToolDroppable) elmnt;
					final Tool existingTool = toolDroppable.getTool();
					if (existingTool == null || getTool().isSwappable(toolDroppable.getToolBox(), existingTool)) {
						toolDroppable.getToolBox().swap(toolDroppable.getSlot(), getSlot(), getToolBox());
						return true;
					}
				}

				return false;
			}
		});
	}

	public void onMouseLeftPressed(MouseButtonEvent evt) {
	}

	public void onMouseLeftReleased(MouseButtonEvent evt) {
	}

	public void onMouseRightPressed(MouseButtonEvent evt) {
	}

	public void onMouseRightReleased(MouseButtonEvent evt) {
		if (layoutMenu != null) {
			layoutMenu.showMenu(null, evt.getX(), evt.getY());
		}
	}

	protected void onToolDragDropComplete(MouseButtonEvent evt) {
	}

	@Override
	public String toString() {
		return "ToolPanel [toolBox=" + toolBox + "]";
	}
}