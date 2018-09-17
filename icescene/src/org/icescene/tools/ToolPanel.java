package org.icescene.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.iceui.controls.ElementStyle;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonGroup;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.RadioButton;
import icetone.controls.containers.Panel;
import icetone.controls.menuing.Menu;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.windows.DialogBox;

public class ToolPanel extends Panel implements PropertyChangeListener {

	private final static Logger LOG = Logger.getLogger(ToolPanel.class.getName());

	private Menu<Integer> layoutMenu;
	private final DragContext dragContext;
	private final ToolBox toolBox;

	public ToolPanel(final Preferences prefs, DragContext dragContext, final ToolBox toolBox, String toolBoxName,
			final BaseScreen screen) {
		super(screen, toolBoxName);
		this.dragContext = dragContext;
		this.toolBox = toolBox;
		final String lockKey = toolBoxName + SceneConfig.WINDOW_LOCK;
		final boolean lock = prefs.getBoolean(lockKey, false);

		// Layout Menu
		if (toolBox.isConfigurable()) {
			layoutMenu = new Menu<Integer>(screen);
			final int[] sizes = new int[] { 1, 8, 2, 4 };
			CheckBox lockEl = new CheckBox(screen);
			lockEl.setChecked(lock);
			lockEl.onChange(evt -> {
				ToolPanel.this.setMovable(!evt.getNewValue());
				prefs.putBoolean(lockKey, evt.getNewValue());
				layoutMenu.close();
			});
			layoutMenu.addMenuItem("Lock", lockEl, null);
			ButtonGroup<RadioButton<Integer>> bg = new ButtonGroup<>();
			for (int sz : sizes) {
				RadioButton<Integer> rb = new RadioButton<>(screen, sz);
				bg.addButton(rb);
				int vsz = (8 / sz);
				if (sz == toolBox.getHorizontalCells() && vsz == toolBox.getVerticalCells()) {
					rb.setState(true);
				}
				layoutMenu.addMenuItem(sz + "x" + vsz, rb, sz);
			}
			layoutMenu.setDestroyOnHide(false);
			screen.addElement(layoutMenu);
			bg.onChange(evt -> {
				Integer val = evt.getSource().getSelected().getValue();
				setLayoutManager(new MigLayout(screen, "ins 0, fill, gap 0, gap 0, wrap " + val));
				ToolPanel.this.sizeToContent();
				toolBox.setHorizontalCells(val);
				layoutMenu.close();
			});
		}

		setResizable(false);
		setLockToParentBounds(true);
		setMovable(!lock && toolBox.isMoveable());
		setLayoutManager(new MigLayout(screen, "ins 0, wrap " + toolBox.getHorizontalCells() + ", fill, gap 0"));

		onMouseReleased(evt -> {
			if (layoutMenu != null) {
				layoutMenu.showMenu(null, evt.getX(), evt.getY());
			}
		}, MouseUIButtonEvent.RIGHT);

		// Watch for changes in the toolbox
		toolBox.addPropertyChangeListener(this);
	}

	@Override
	public void controlCleanupHook() {
		toolBox.removePropertyChangeListener(this);
	}

	public void addTool(final Tool tool, int s) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Adding tool %s", tool == null ? "EMPTY" : tool.getName()));
		}
		addElement(new ToolDroppable(dragContext, screen, toolBox, tool, s) {

			@Override
			protected void onDragDropComplete(MouseButtonEvent evt) {
				onToolDragDropComplete(evt);
			}

			@Override
			protected boolean doClick(MouseButtonEvent evt) {
				tool.actionPerformed(
						new ActionData((IcesceneApp) ToolKit.get().getApplication(), evt.getX(), evt.getY()));
				return true;
			}

			@Override
			protected boolean doEndDraggableDrag(MouseButtonEvent mbe, BaseElement elmnt) {
				// Look at what is at target element
				final ToolDroppable thisTool = this;
				if (elmnt == null) {
					if (getTool().isTrashable()) {
						final DialogBox dialog = new DialogBox(screen, new Vector2f(15, 15), true) {
							{
								setStyleClass("large");
							}

							@Override
							public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
								hide();
							}

							@Override
							public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
								getToolBox().trash(getTool());
								screen.removeElement(thisTool.getDraggable());
								hide();
							}
						};
						dialog.setDestroyOnHide(true);
						ElementStyle.warningColor(dialog.getDragBar());
						dialog.setWindowTitle("Trash Tool");
						dialog.setButtonOkText("Trash");
						dialog.setText("Are you sure you want to trash " + getTool().getName());
						dialog.setResizable(false);
						dialog.setMovable(false);
						dialog.setModal(true);
						screen.showElement(dialog, ScreenLayoutConstraints.center);

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

	protected void onToolDragDropComplete(MouseButtonEvent evt) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ToolBox.PROP_VISIBLE)) {
			if (Boolean.TRUE.equals(evt.getNewValue()))
				show();
			else
				hide();
		}
	}

}