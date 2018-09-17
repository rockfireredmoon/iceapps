package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Window;
import icetone.core.Size;
import icetone.extras.chooser.ColorFieldControl;

public class TestScaled extends IcesceneApp {

	static {
		AppInfo.context = TestScaled.class;
	}

	public static void main(String[] args) {
		TestScaled app = new TestScaled();
		app.start();
	}

	private ColorFieldControl color;

	public TestScaled() {
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

		Window win = new Window("A Window");
		win.setPosition(300, 300);
		win.getContentArea().setPreferredDimensions(new Size(200, 200));
		//win.setScale(2f, 2f);
		win.setScaled(true);

		screen.showElement(win);

	}

}
