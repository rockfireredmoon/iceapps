package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.Slider;
import icetone.controls.text.Label;
import icetone.core.Orientation;
import icetone.core.layout.mig.MigLayout;

public class TestSlider extends IcesceneApp {

	static {
		AppInfo.context = TestSlider.class;
	}

	public static void main(String[] args) {
		TestSlider app = new TestSlider();
		app.start();
	}

	public TestSlider() {
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

		Panel win = new Panel(new MigLayout("fill", "[][][][][]", "[][][][]"));
		
		Label l1 = new Label("X1");
		Label l2 = new Label("X2");
		Label l3 = new Label("X3");
		Label l4 = new Label("X4");
		Label l5 = new Label("X5");
		Label l6 = new Label("X6");

		// Vert slider 1
		Slider<Float> sl1 = new Slider<Float>(screen, Orientation.VERTICAL);
		sl1.setLockToStep(true);
		sl1.setSliderModel(new FloatRangeSliderModel(0f, 100f, 50f, 20f));
		sl1.onChanged(evt->l1.setText(String.format("%3.2f", evt.getNewValue())));

		// Vert slider 2
		Slider<Float> sl2 = new Slider<Float>(screen, Orientation.VERTICAL);
		sl2.setSliderModel(new FloatRangeSliderModel(0f, 100f, 50f, 20f));
		sl2.setReversed(true);
		sl2.setLockToStep(true);
		sl2.onChanged(evt->l2.setText(String.format("%3.2f", evt.getNewValue())));

		// Vert slider 3
		Slider<Float> sl3 = new Slider<Float>(screen, Orientation.VERTICAL);
		sl3.setSliderModel(new FloatRangeSliderModel(0f, 1f, 0.50f, 0.05f));
		sl3.setLockToStep(true);
		sl3.onChanged(evt->l3.setText(String.format("%3.2f", evt.getNewValue())));

		// Hor Slider 1 (reversed)
		Slider<Float> sl4 = new Slider<Float>();
		sl4.setReversed(true);
		sl4.setSliderModel(new FloatRangeSliderModel(0f, 100f, 100f, 20f));
		sl4.onChanged(evt->l4.setText(String.format("%3.2f", evt.getNewValue())));

		// Hor Slider 2
		Slider<Float> sl5 = new Slider<Float>();
		sl5.setSliderModel(new FloatRangeSliderModel(0f, 100f, 100f, 20f));
		sl5.setLockToStep(true);
		sl5.onChanged(evt->l5.setText(String.format("%3.2f", evt.getNewValue())));

		// Hor Slider 3
		Slider<Float> sl6 = new Slider<Float>(screen, Orientation.HORIZONTAL);
		sl6.setSliderModel(new FloatRangeSliderModel(0f, 1f, 0.50f, 0.05f));
		sl6.setLockToStep(false);
		sl6.onChanged(evt->l6.setText(String.format("%3.2f", evt.getNewValue())));

		win.addElement(new Label(), "span 2");
		win.addElement(l1);
		win.addElement(l2);
		win.addElement(l3, "wrap");
		win.addElement(l4);
		win.addElement(sl4, "growx");
		win.addElement(sl1, "spany 3, growy");
		win.addElement(sl2, "spany 3, growy");
		win.addElement(sl3, "spany 3, growy, wrap");
		win.addElement(l5);
		win.addElement(sl5, "growx, wrap");
		win.addElement(l6);
		win.addElement(sl6, "growx");

		screen.addElement(win);

	}

}
