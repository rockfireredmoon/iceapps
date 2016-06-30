package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestSpinner extends IcesceneApp {

	static {
		AppInfo.context = TestSpinner.class;
	}

	public static void main(String[] args) {
		TestSpinner app = new TestSpinner();
		app.start();
	}

	public TestSpinner() {
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

		// Panel for actions and character selection
		Panel panel = new Panel();
		panel.setLayoutManager(new MigLayout("fill"));

		Label l1 = new Label("Angle 1");
		panel.addChild(l1);
		Spinner<Float> a1 = new Spinner<Float>();
		a1.setLabel(l1);
		a1.setSpinnerModel(new FloatRangeSpinnerModel(0, 180, 1f, 0));
		a1.setFormatterString("%3.0f");
		panel.addChild(a1, "growx");

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}
}
