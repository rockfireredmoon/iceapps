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
import org.iceui.controls.color.ColorFieldControl;

import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import icetone.controls.extras.OSRViewPort;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.lists.Table;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.Element.Orientation;
import icetone.core.layout.mig.MigLayout;

public class TestModelTable extends IcesceneApp {

	public static void main(String[] args) {
		TestModelTable app = new TestModelTable();
		app.start();
	}

	private Table table;
	private EntityFactory pf;

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

		Panel panel = new Panel(screen, "Panel", new Vector2f(400, 200), new Vector2f(550f, 420));
		panel.setLayoutManager(new MigLayout(screen, "", "[fill, grow]", "[fill, grow]"));

		table = new Table(screen) {
			private ArrayList<Table.TableRow> lastSelected;

			@Override
			public void onChange() {
				if (lastSelected != null) {
					for (Table.TableRow r : lastSelected) {
						final Iterator<Element> iterator = r.getElements().iterator();
						iterator.next();
						OSRViewPort vp = (OSRViewPort) iterator.next().getElements().iterator().next();
						vp.getOSRBridge().getRootNode().getChild(0).removeControl(Rotator.class);
					}
				}
				final List<Table.TableRow> selectedRows = table.getSelectedRows();
				for (Table.TableRow r : selectedRows) {
					final Iterator<Element> iterator = r.getElements().iterator();
					iterator.next();
					OSRViewPort vp = (OSRViewPort) iterator.next().getElements().iterator().next();
					vp.getOSRBridge().getRootNode().getChild(0).addControl(new Rotator());
				}
				lastSelected = new ArrayList<Table.TableRow>(selectedRows);
			}
		};
		table.addColumn("Name");
		table.addColumn("Model");
		table.addColumn("Density");
		table.addColumn("Colour");
		panel.addChild(table);

		// table.addRow(createRow("Prop/Prop-Paintings1/Prop-Painting1-WantedShadow.csm.xml"));
		table.addRow(createRow(1, "Prop/Prop-Clutter/Prop-Clutter-Grass2.csm.xml"));
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

	}

	private Table.TableRow createRow(int row, String propName) {
		Table.TableRow row1 = new Table.TableRow(screen, table);

		Table.TableCell cell1 = new Table.TableCell(screen, Icelib.getBaseFilename(propName), "Name " + row);
		// cell1.setHeight(100);
		row1.addChild(cell1);

		Table.TableCell cell2 = new Table.TableCell(screen, null, "Model " + row);
		// cell2.setHeight(100);

		AbstractProp prop = pf.getProp(propName);
		prop.getSpatial().rotate(0, -FastMath.HALF_PI, 0);
		prop.getSpatial().scale(0.25f);

		Node n = new Node();
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(3));
		n.addLight(al);
		n.attachChild(prop.getSpatial());

		OSRViewPort vp = new OSRViewPort(screen, new Vector2f(100, 100), new Vector2f(100, 100), Vector4f.ZERO, null);
		vp.setOSRBridge(n, 100, 100);
		vp.setIgnoreMouse(true);
		vp.setDocking(null);
		vp.setScaleEW(false);
		vp.setScaleNS(false);
		cell2.addChild(vp);

		row1.addChild(cell2);
		Table.TableCell cell3 = new Table.TableCell(screen, null, "Density " + row);
		Spinner<Float> sp1 = new Spinner<Float>(screen, Orientation.HORIZONTAL, true);
		// cell3.setHeight(100);
		cell3.setVAlign(BitmapFont.VAlign.Center);
		sp1.setSpinnerModel(new FloatRangeSpinnerModel(0, 10, 1, row));
		sp1.setDocking(null);
		sp1.setScaleEW(false);
		sp1.setScaleNS(false);
		cell3.addChild(sp1);
		row1.addChild(cell3);

		Table.TableCell cell4 = new Table.TableCell(screen, null, "Colour " + row);
		// cell4.setHeight(100);
		cell4.setHAlign(BitmapFont.Align.Right);
		ColorFieldControl cfc = new ColorFieldControl(screen, ColorRGBA.White) {
			@Override
			protected void onChangeColor(ColorRGBA newColor) {
			}
		};
		cell4.addChild(cfc);
		row1.addChild(cell4);

		return row1;
	}
}
