package org.icetests;

import org.icelib.AppInfo;
import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.assets.MeshLoader;
import org.icescene.props.EntityFactory;
import org.iceui.controls.color.ColorFieldControl;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.Table;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

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
		panel.addChild(table);

		table.addRow(createRow(1, "Prop/Prop-Clutter/Prop-Clutter-Grass2.csm.xml"));
		table.addRow(createRow(2, "Prop/Prop-Clutter/Prop-Clutter-Grass4.csm.xml"));
		table.addRow(createRow(3, "Prop/Prop-Clutter/Prop-Clutter-Flowers2.csm.xml"));
		table.addRow(createRow(4, "Prop/Prop-Clutter/Prop-Clutter-Flowers1.csm.xml"));
		table.addRow(createRow(5, "Prop/Prop-Clutter/Prop-Clutter-Flowers3.csm.xml"));
		table.addRow(createRow(6, "Prop/Prop-Clutter/Prop-Clutter-Flowers4.csm.xml"));

		// TODO this currently has to be done last or division by zero errors
		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);
		// table.setHeadersVisible(false);
		screen.addElement(panel);

	}

	private Table.TableRow createRow(int row, String propName) {
		Table.TableRow row1 = new Table.TableRow(screen, table);
		Table.TableCell cell1 = new Table.TableCell(screen, Icelib.getBaseFilename(propName), "Model " + row);
		// cell1.setHeight(100);
		row1.addChild(cell1);

		Table.TableCell cell4 = new Table.TableCell("Model " + row);
		// cell4.setHeight(100);
		cell4.setVAlign(BitmapFont.VAlign.Center);
		ColorFieldControl cfc = new ColorFieldControl(ColorRGBA.White) {
			@Override
			protected void onChangeColor(ColorRGBA newColor) {
			}
		};
		cell4.addChild(cfc);
		row1.addChild(cell4);

		return row1;
	}
}
