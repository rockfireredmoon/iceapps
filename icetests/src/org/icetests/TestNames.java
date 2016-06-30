package org.icetests;

import java.util.List;

import org.icelib.AppInfo;
import org.icelib.Appearance.Race;
import org.icescene.IcesceneApp;
import org.icescene.configuration.NameSuggestions;
import org.iceui.controls.FancyWindow;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.Table;
import icetone.controls.lists.Table.TableRow;
import icetone.core.layout.FillLayout;

public class TestNames extends IcesceneApp {

	static {
		AppInfo.context = TestNames.class;
	}

	public static void main(String[] args) {
		TestNames app = new TestNames();
		app.start();
	}

	public TestNames() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseCustomCursors(true);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Panel for actions and character selection
		FancyWindow fw = new FancyWindow();
		fw.getContentArea().setLayoutManager(new FillLayout());
		fw.setIsResizable(true);

		Table t = new Table();
		t.setUseContentPaging(true);
		t.addColumn("Race");
		t.addColumn("Name");
		NameSuggestions nameSuggestions = NameSuggestions.get(assetManager);
		for (Race r : Race.values()) {
			System.out.println(r);
			List<String> nl = nameSuggestions.getNames(r);
			for (String n : nl) {
				TableRow tr = new TableRow(t);
				tr.addCell(r.name(), r);
				tr.addCell(n, n);
				t.addRow(tr, false);
			}
		}
		t.pack();

		fw.getContentArea().addChild(t);
		fw.sizeToContent();

		screen.addElement(fw);

	}

}
