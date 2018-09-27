package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.scrolling.ScrollBar;
import icetone.controls.text.Label;
import icetone.core.Orientation;
import icetone.core.layout.mig.MigLayout;

public class TestScrollBars extends IcesceneApp {

	static {
		AppInfo.context = TestScrollBars.class;
	}

	public static void main(String[] args) {
		TestScrollBars app = new TestScrollBars();
		app.start();
	}

	public TestScrollBars() {
		setUseUI(true);
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

		Panel panel = new Panel();
		panel.setLayoutManager(new MigLayout(screen, "wrap 2", "[fill, grow][]", "[fill, grow][]"));

		Label child = new Label("Content");
		panel.addElement(child);
		panel.addElement(new ScrollBar(screen, Orientation.VERTICAL));
		panel.addElement(new ScrollBar(screen, Orientation.HORIZONTAL));
		panel.addElement(new Label("*"));

		screen.addElement(panel);

	}

}
