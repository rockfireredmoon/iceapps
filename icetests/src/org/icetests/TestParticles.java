package org.icetests;

import java.util.Set;

import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.ogreparticle.OGREParticleConfiguration;
import org.icescene.ogreparticle.OGREParticleEmitter;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.ogreparticle.OGREParticleScript.BillboardRotation;
import org.icescene.ogreparticle.OGREParticleScript.BillboardType;

import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import emitter.Emitter;
import icemoon.iceloader.ServerAssetManager;
import icetone.controls.containers.Panel;
import icetone.controls.table.Table;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.core.Size;
import icetone.core.layout.FillLayout;

public class TestParticles extends IcesceneApp {

	public static void main(String[] args) {
		TestParticles app = new TestParticles();
		app.start();
	}

	private Table table;
	private Node node;
	private Panel panel;

	public TestParticles() {
		setUseUI(true);
		// setStylePath("icetone/style/def/style_map.gui.xml");

	}

	@Override
	public void onSimpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		 panel = new Panel(new FillLayout());
//		panel.setLayoutManager(new MigLayout(screen, "fill", "[grow]", "[grow]"));

		table = new Table(screen);
		table.onChanged(evt -> {
			TableRow selRow = evt.getSource().getSelectedRow();
			if (selRow != null && selRow.getParentRow() != null) {
				OGREParticleScript cfg = (OGREParticleScript) selRow.getValue();
				setConfig(cfg);
			}
		});
		table.addColumn("Effect");
		table.setPreferredDimensions(new Size(300, 300));
//		table.setHeadersVisible(false);

		final Set<String> particleFiles = ((ServerAssetManager) getAssetManager())
				.getAssetNamesMatching(".*/.*\\.particle");
		for (String s : particleFiles) {
			TableRow row = new TableRow(screen, table, s);
			row.setLeaf(false);
			TableCell cell = new TableCell(screen, Icelib.getBasename(Icelib.getFilename(s)), s);
			row.addElement(cell);
			table.addRow(row);

			OGREParticleConfiguration cgh = OGREParticleConfiguration.get(assetManager, s);
			for (OGREParticleScript g : cgh.getBackingObject().values()) {
				TableRow crow = new TableRow(screen, table, g);
				TableCell ccell = new TableCell(screen, g.getName(), g);
				crow.addElement(ccell);
				row.addRow(crow);
			}
		}

		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_FIRST);
		panel.addElement(table, "growx, growy");

		screen.addElement(panel);

	}

	private void setConfig(OGREParticleScript cfg) {
		if (node != null) {
			node.removeFromParent();
		}
		node = new Node();
		cfg.setBillboardType(BillboardType.POINT);
		
		for (OGREParticleEmitter i : cfg.getEmitters()) {
			final Emitter emitter = i.createEmitter(assetManager);
			emitter.setEnabled(true);
			emitter.initialize(assetManager);
			node.addControl(emitter);
		}
		node.scale(0.01f, 0.01f, 1f);
//		node.move(200, 200, 0);
		panel.attachChild(node);
		rootNode.attachChild(node);
	}
}
