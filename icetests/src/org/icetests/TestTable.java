package org.icetests;

import java.util.logging.Logger;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.Table;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;

public class TestTable extends IcesceneApp {

	static {
		AppInfo.context = TestTable.class;
	}

	private static final Logger LOG = Logger.getLogger(TestTable.class.getName());

	public static void main(String[] args) {
		TestTable app = new TestTable();
		app.start();
	}

	private Table table;
	private boolean treeMode;
	private boolean singleColumn;

	public TestTable() {
		setUseUI(true);
		// setStylePath("icetone/style/def/style_map.gui.xml");

	}

	@Override
	public void onSimpleInitApp() {

		// File dir = new File(System.getProperty("user.home") + File.separator
		// + "IceClientVideos");
		// if (!dir.exists() && !dir.mkdirs()) {
		// LOG.severe(String.format("Failed to create videos directory %s",
		// dir));
		// } else {
		// File recordingFile = new File(dir, new
		// SimpleDateFormat("ddMMyy-HHmmss").format(new Date()) + ".mjpeg");
		// LOG.info(String.format("Recording to %s", recordingFile));
		// stateManager.attach(new VideoRecorderAppState(recordingFile));
		// }

		screen.setUseToolTips(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		table = new Table();
		// table.setHeadersVisible(false);
		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_FIRST);

		// Resize mode
		ComboBox<Table.ColumnResizeMode> resizeMode = new ComboBox<Table.ColumnResizeMode>(Table.ColumnResizeMode.values()) {
			@Override
			public void onChange(int selectedIndex, Table.ColumnResizeMode value) {
				table.setColumnResizeMode(value);
			}
		};
		resizeMode.setSelectedByValue(table.getColumnResizeMode(), false);

		// Selection mode
		ComboBox<Table.SelectionMode> selectionMode = new ComboBox<Table.SelectionMode>(Table.SelectionMode.values()) {
			@Override
			public void onChange(int selectedIndex, Table.SelectionMode value) {
				table.setSelectionMode(value);
			}
		};
		selectionMode.setSelectedByValue(table.getSelectionMode(), false);

		//
		CheckBox sortable = new CheckBox() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				table.setSortable(toggled);
			}
		};
		sortable.setIsCheckedNoCallback(table.getIsSortable());
		sortable.setLabelText("Sortable");
		sortable.setDocking(Element.Docking.SW);

		//
		CheckBox headers = new CheckBox("Show headers") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				table.setHeadersVisible(toggled);
			}
		};
		headers.setIsCheckedNoCallback(table.isHeadersVisible());

		//
		final CheckBox initialPack = new CheckBox("Pack as you go") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				rebuild(toggled);
			}
		};
		initialPack.setIsCheckedNoCallback(false);

		final CheckBox debug = new CheckBox("Debug") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				table.getScrollableArea().setColorMap(toggled ? "Interface/bgw.jpg" : null);
			}
		};

		//
		CheckBox tree = new CheckBox("Tree Mode") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				treeMode = toggled;
				rebuild(initialPack.getIsChecked());
			}
		};

		//
		CheckBox single = new CheckBox("Single Column") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				singleColumn = toggled;
				rebuild(initialPack.getIsChecked());
			}
		};
		CheckBox keyboard = new CheckBox("Keyboard") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				table.setEnableKeyboardNavigation(toggled);
			}
		};
		keyboard.setIsChecked(table.isEnableKeyboardNavigation());

		// Panel for actions and character selection
		Panel panel = new Panel();
		panel.setLockToParentBounds(true);
		panel.setLayoutManager(new MigLayout("wrap 3, fill", "[][][]", "[grow][shrink 0][shrink 0][shrink 0]"));
		panel.addChild(table, "growx,growy,span 3");

		panel.addChild(resizeMode);
		panel.addChild(initialPack);
		panel.addChild(selectionMode);

		panel.addChild(debug);
		panel.addChild(keyboard);
		panel.addChild(sortable);

		panel.addChild(headers);
		panel.addChild(single);
		panel.addChild(tree);
		panel.sizeToContent();

		rebuild(initialPack.getIsChecked());

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

	private void rebuild(boolean dopack) {
		table.removeAllColumns();
		table.addColumn("Column 1");
		if (!singleColumn) {
			table.addColumn("Column 2");
			table.addColumn("Column 3");
		}
		for (int i = 0; i < 1; i++) {
			Table.TableRow row = new Table.TableRow(screen, table, "TableRow" + i, null) {

				@Override
				public void onBeforeLayout() {
					super.onBeforeLayout();
				}

			};
			
			// Set that the row can have children
			row.setLeaf(!treeMode);

			row.addCell(String.format("Row %d, Cell 1", i), i);
			
			if (!singleColumn) {
				row.addCell(String.format("Row %d, Cell 2", i), i);
				// if (Math.random() > 0.5f) {
				// LTable.LTableCell ltc = new LTable.LTableCell(screen, table);
				// ltc.setPreferredDimensions(new Vector2f(100, 100));
				// row.addChild(ltc);
				// } else {
				row.addCell(String.format("Row %d, Cell 3", i), i);
				// }
			}

			table.addRow(row, dopack);

			// Add some child rows
			if (treeMode) {
				for (int j = 0; j < 3; j++) {

					Table.TableRow childRow = new Table.TableRow(screen, table, "TreeTableRow" + j, null);

					childRow.addCell(String.format("Child %d, Cell 1", j), j);
					if (!singleColumn) {
						childRow.addCell(String.format("Child %d, Cell 2", j), j);
						childRow.addCell(String.format("Child %d, Cell 3", j), j);
					}

					if (j == 1) {
						// For one of the child rows, lets add a further level
						childRow.setLeaf(false);

						Table.TableRow childChildRow = new Table.TableRow(screen, table);

						childChildRow.addCell(String.format("Child-Child %d, Cell 1", 99), 99);
						if (!singleColumn) {
							childChildRow.addCell(String.format("Child-Child %d, Cell 2", 99), 99);
							childChildRow.addCell(String.format("Child-Child %d, Cell 3", 99), 99);
						}

						childRow.addRow(childChildRow, dopack);
					}

					row.addRow(childRow, dopack);
				}
			}
		}
		if (!dopack) {
			table.pack();
		}
	}
}
