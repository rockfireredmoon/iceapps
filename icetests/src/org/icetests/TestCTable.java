package org.icetests;

import org.icelib.AppInfo;
import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.assets.MeshLoader;
import org.icescene.props.EntityFactory;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.table.Table;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ColorFieldControl;

public class TestCTable extends IcesceneApp {
	static {
		AppInfo.context = TestCTable.class;
	}

	public static void main(String[] args) {
		TestCTable app = new TestCTable();
		app.start();
	}

	private Table table;

	public TestCTable() {
		setUseUI(true);
		MeshLoader.setTexturePathsRelativeToMesh(true);
	}

	@Override
	public void onSimpleInitApp() {
		new EntityFactory(this, rootNode);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		Panel panel = new Panel();
		panel.setLayoutManager(new MigLayout("", "[fill, grow]", "[fill, grow]"));

		table = new Table(screen);
		table.addColumn("Name");
		table.addColumn("Colour");
		panel.addElement(table);

		table.addRow(createRow(1, "Prop-Clutter#Prop-Clutter-Grass2"));
		table.addRow(createRow(2, "Prop-Clutter#Prop-Clutter-Grass4"));
		table.addRow(createRow(3, "Prop-Clutter#Prop-Clutter-Flowers2"));
		table.addRow(createRow(4, "Prop-Clutter#Prop-Clutter-Flowers1"));
		table.addRow(createRow(5, "Prop-Clutter#Prop-Clutter-Flowers3"));
		table.addRow(createRow(6, "Prop-Clutter#Prop-Clutter-Flowers4"));

		// TODO this currently has to be done last or division by zero errors
		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);
		// table.setHeadersVisible(false);
		screen.addElement(panel);

	}

	private TableRow createRow(int row, String propName) {
		TableRow row1 = new TableRow(screen, table);
		TableCell cell1 = new TableCell(screen, Icelib.getBaseFilename(propName), "Model " + row);
		// cell1.setHeight(100);
		row1.addElement(cell1);

		TableCell cell4 = new TableCell("Model " + row);
		// cell4.setHeight(100);
		cell4.setVAlign(BitmapFont.VAlign.Center);
		ColorFieldControl cfc = new ColorFieldControl(ColorRGBA.White);
		cell4.addElement(cfc);
		row1.addElement(cell4);

		return row1;
	}
}
