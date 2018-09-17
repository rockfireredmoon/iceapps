package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.text.Label;
import icetone.core.layout.GridLayout;

public class TestClipping extends IcesceneApp {

	static {
		AppInfo.context = TestClipping.class;
	}

	public static void main(String[] args) {
		TestClipping app = new TestClipping();
		app.start();
	}

	public TestClipping() {
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

		Panel xcw = new Panel(new GridLayout(2, 1));

		Label child = new Label("A really reallly long label that should be clipped");
		xcw.addElement(child);
		xcw.addElement(new Label("Short label"));

		screen.addElement(xcw);

	}

}
