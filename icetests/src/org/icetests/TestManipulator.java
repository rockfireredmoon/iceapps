package org.icetests;

import org.icescene.IcesceneApp;
import org.icescene.build.ObjectManipulatorControl;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.io.MouseManager;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class TestManipulator extends IcesceneApp {

	public static void main(String[] args) {
		TestManipulator app = new TestManipulator();
		app.start();
	}

	public TestManipulator() {
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

		ModifierKeysAppState mkas = new ModifierKeysAppState();
		stateManager.attach(mkas);

		MouseManager mm = new MouseManager(rootNode);
		stateManager.attach(mm);

		ObjectManipulatorControl omc = new ObjectManipulatorControl(rootNode, mm, this);
		geom.addControl(omc);

	}

}
