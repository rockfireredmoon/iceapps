package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.scene.AnimSprite;
import org.icescene.scene.Sprite;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class TestAnimSprite extends IcesceneApp {
	
	static {
		AppInfo.context = TestAnimSprite.class;
	}

	public static void main(String[] args) {
		TestAnimSprite app = new TestAnimSprite();
		app.start();
	}

	public TestAnimSprite() {
		setUseUI(true);
		// setStylePath("icetone/style/def/style_map.gui.xml");
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);
		flyCam.setEnabled(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		AnimSprite as = new AnimSprite(assetManager, "Interface/Styles/Gold/Scene/ability-h.png", 32, 32);
		as.setLocalTranslation(200, 200, 0);
		as.start(0.1f);
		guiNode.attachChild(as);

		Sprite as2 = new Sprite(assetManager, "Interface/Styles/Gold/Scene/ability-h.png", 0, 0, 32, 32);
		as2.setLocalTranslation(400, 400, 0);
		guiNode.attachChild(as2);

	}

}
