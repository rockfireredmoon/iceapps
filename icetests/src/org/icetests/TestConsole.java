package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.console.ConsoleAppState;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class TestConsole extends IcesceneApp {

	static {
		AppInfo.context = TestConsole.class;
	}

	public static void main(String[] args) {
		TestConsole app = new TestConsole();
		app.start();
	}

	public TestConsole() {
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

		// Listen for / key

		Trigger TRIGGER_FORWARD_1 = new KeyTrigger(KeyInput.KEY_SLASH);
		getInputManager().addMapping("OpenConsole", TRIGGER_FORWARD_1);
		getInputManager().addListener(new ActionListener() {
			public void onAction(String name, boolean isPressed, float tpf) {
				if (!isPressed) {
					ConsoleAppState.toggle(prefs, getStateManager());
				}
			}
		}, "OpenConsole");

	}
}
