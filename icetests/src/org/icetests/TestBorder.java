package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.layout.BorderLayout;

public class TestBorder extends IcesceneApp {

	static {
		AppInfo.context = TestBorder.class;
	}

	public static void main(String[] args) {
		TestBorder app = new TestBorder();
		app.start();
	}

	public TestBorder() {
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

		Element center = new Element(screen);
		center.setColorMap("Interface/bgx.jpg");

		Element south = new Element(screen);
		south.setColorMap("Interface/bgy.jpg");
		south.setPreferredDimensions(new Vector2f(24, 24));

		Panel xcw = new Panel(screen, new Vector2f(100, 100), new Vector2f(500, 350));
		xcw.setLayoutManager(new BorderLayout(8, 8));
		xcw.addChild(south, BorderLayout.Border.SOUTH);
		xcw.addChild(center, BorderLayout.Border.CENTER);

		screen.addElement(xcw);
		xcw.show();

	}

}