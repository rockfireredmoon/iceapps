package org.icetests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.assets.MeshLoader;
import org.icescene.controls.Rotator;
import org.icescene.props.AbstractProp;
import org.icescene.props.EntityFactory;

import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.OSRViewPort;
import icetone.controls.containers.Panel;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.table.Table;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.core.BaseElement;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ColorFieldControl;
import icetone.extras.debug.GUIExplorerAppState;

public class TestModelTable extends IcesceneApp {

	public static void main(String[] args) {
		TestModelTable app = new TestModelTable();
		app.start();
	}

	private Table table;
	private EntityFactory pf;
	private ArrayList<TableRow> lastSelected;

	public TestModelTable() {
		setUseUI(true);
		MeshLoader.setTexturePathsRelativeToMesh(true);
	}

	@Override
	public void onSimpleInitApp() {
		pf = new EntityFactory(this, rootNode);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		// rootNode.attachChild(geom);

		// AbstractProp prop =
		// pf.getProp("Props/Prop-Paintings1/Prop-Painting1-WantedShadow.csm.xml");
		// prop.scale(6f);
		// prop.rotate(0, -FastMath.QUARTER_PI, 0);
		// prop.setLocalTranslation(400,400, 0);
		// getGuiNode().attachChild(prop);

		// geom.scale(64f);
		// geom.setLocalTranslation(400,400, 0);
		// getGuiNode().attachChild(geom);

		Panel panel = new Panel(screen, "Panel", new Vector2f(400, 200), new Size(550f, 420));
		panel.setLayoutManager(new MigLayout(screen, "", "[fill, grow]", "[fill, grow]"));

		table = new Table(screen);
		table.onChanged(evt -> {

			if (lastSelected != null) {
				for (TableRow r : lastSelected) {
					final Iterator<BaseElement> iterator = r.getElements().iterator();
					iterator.next();
					OSRViewPort vp = (OSRViewPort) iterator.next().getElements().iterator().next();
					vp.getOSRBridge().getRootNode().getChild(0).removeControl(Rotator.class);
				}
			}
			final List<TableRow> selectedRows = table.getSelectedRows();
			for (TableRow r : selectedRows) {
				final Iterator<BaseElement> iterator = r.getElements().iterator();
				iterator.next();
				OSRViewPort vp = (OSRViewPort) iterator.next().getElements().iterator().next();
				vp.getOSRBridge().getRootNode().getChild(0).addControl(new Rotator());
			}
			lastSelected = new ArrayList<TableRow>(selectedRows);
		});
		table.addColumn("Name");
		table.addColumn("Model");
		table.addColumn("Density");
		table.addColumn("Colour");
		panel.addElement(table);

		// table.addRow(createRow("Prop/Prop-Paintings1/Prop-Painting1-WantedShadow.csm.xml"));
		table.addRow(createRow(1, "Prop-Clutter#Prop-Clutter-Grass2"));
		// table.pack();
		// table.addRow(createRow(2,
		// "Prop/Prop-Clutter/Prop-Clutter-Grass4.csm.xml"));
		// table.addRow(createRow(3,
		// "Prop/Prop-Clutter/Prop-Clutter-Flowers2.csm.xml"));
		// table.addRow(createRow(4,
		// "Prop/Prop-Clutter/Prop-Clutter-Flowers1.csm.xml"));
		// table.addRow(createRow(5,
		// "Prop/Prop-Clutter/Prop-Clutter-Flowers3.csm.xml"));
		// table.addRow(createRow(6,
		// "Prop/Prop-Clutter/Prop-Clutter-Flowers4.csm.xml"));

		// TODO this currently has to be done last or division by zero errors
		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);
		// table.setHeadersVisible(false);
		screen.addElement(panel);
		
		getStateManager().attach(new GUIExplorerAppState());

		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(3));		
		screen.getGUINode().addLight(al);

	}

	private TableRow createRow(int row, String propName) {
		TableRow row1 = new TableRow(screen, table);

		TableCell cell1 = new TableCell(screen, Icelib.getBaseFilename(propName), "Name " + row);
		// cell1.setHeight(100);
		row1.addElement(cell1);

		TableCell cell2 = new TableCell(screen, null, "Model " + row);
		// cell2.setHeight(100);

		AbstractProp prop = pf.getProp(propName);
		prop.getSpatial().rotate(0, -FastMath.HALF_PI, 0);
		prop.getSpatial().scale(0.25f);
//
		Node n = new Node();
//		AmbientLight al = new AmbientLight();
//		al.setColor(ColorRGBA.White.mult(3));
//		n.addLight(al);

		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White);		
		n.addLight(al);
		n.attachChild(prop.getSpatial());

		OSRViewPort vp = new OSRViewPort(screen, new Size(100, 100));
		vp.setOSRBridge(n, 100, 100);
		vp.setIgnoreMouse(true);
		cell2.addElement(vp);

		row1.addElement(cell2);
		TableCell cell3 = new TableCell(screen, null, "Density " + row);
		Spinner<Float> sp1 = new Spinner<Float>(screen, Orientation.HORIZONTAL, true);
		cell3.setVAlign(BitmapFont.VAlign.Center);
		sp1.setSpinnerModel(new FloatRangeSpinnerModel(0, 10, 1, row));
		cell3.addElement(sp1);
		row1.addElement(cell3);

		TableCell cell4 = new TableCell(screen, null, "Colour " + row);
		cell4.setHAlign(BitmapFont.Align.Right);
		ColorFieldControl cfc = new ColorFieldControl(screen, ColorRGBA.White);
		cell4.addElement(cfc);
		row1.addElement(cell4);

		return row1;
	}
}
