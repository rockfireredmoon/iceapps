package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;

public class TestMigScreenLayout extends IcesceneApp {

	static {
		AppInfo.context = TestMigScreenLayout.class;

	}

	public static void main(String[] args) {
		TestMigScreenLayout app = new TestMigScreenLayout();
		app.start();
	}

	public TestMigScreenLayout() {
		setUseUI(true);
		setResizable(true);
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

		screen.setLayoutManager(new MigLayout(screen, "fill", "[:250:][grow][]", "[grow]"));

		Panel left = new Panel();
		left.setUID("LeftPanel");
		left.setLayoutManager(new FlowLayout(0, BitmapFont.Align.Center));
		left.addChild(new Label("Some label text"));

		// Add window to screen (causes a layout)
		screen.addElement(left, "growy, width 250", false);
		screen.addElement(new Label("Middle"));
		screen.addElement(new Label("Right"));

	}
}
