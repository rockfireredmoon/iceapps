package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.asset.AssetKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.text.SLabel;
import icetone.controls.windows.Panel;
import icetone.core.layout.FlowLayout;

public class TestNewInit extends IcesceneApp {

	static {
		AppInfo.context = TestNewInit.class;
	}

	public static void main(String[] args) {
		TestNewInit app = new TestNewInit();
		app.start();
	}

	public TestNewInit() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.getStylesheets().add(assetManager.loadAsset(new AssetKey<Stylesheet>("Interface/Css/default.css")));

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
		Panel xcw = new Panel();
//		xcw.setMinDimensions(new Vector2f(100, 100));
		xcw.setLayoutManager(new FlowLayout());

		xcw.addChild(new SLabel("Test SLabel"));

		xcw.sizeToContent();
//		xcw.setDimensions(500, 500);

		screen.addElement(xcw);

	}

}
