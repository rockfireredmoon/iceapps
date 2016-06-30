package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.Slider;
import icetone.controls.windows.Panel;
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

		Panel win = new Panel(new MigLayout("fill", "[][][][]", "[][][]"));
		
 	// Slider 1
		Slider<Float> sl4 = new Slider<Float>() {

			@Override
			public void onChange(Float value) {
				System.out.println("onChange(" + value + ")");
			}

		};
		sl4.setSliderModel(new FloatRangeSliderModel(0f, 100f, 100f, 20f));


		// Vert slider 1
		Slider<Float> sl1 = new Slider<Float>(screen, Slider.Orientation.VERTICAL, false) {

			@Override
			public void onChange(Float value) {
				System.out.println("onChange(" + value + ")");
			}

		};
		sl1.setLockToStep(true);
		sl1.setSliderModel(new FloatRangeSliderModel(0f, 100f, 50f, 20f));

		// Vert slider 2
		Slider<Float> sl2 = new Slider<Float>(screen, Slider.Orientation.VERTICAL, true) {

			@Override
			public void onChange(Float value) {
				System.out.println("onChange(" + value + ") sp = " + stepPerIndex());
			}

		};
		sl2.setSliderModel(new FloatRangeSliderModel(0f, 100f, 50f, 20f));
		sl2.setLockToStep(true);

		// Vert slider 3
		Slider<Float> sl3 = new Slider<Float>(screen, Slider.Orientation.VERTICAL, true) {
			@Override
			public void onChange(Float value) {
				System.out.println("onChange(" + value + ") sp = " + stepPerIndex());
			}
		};
		sl3.setSliderModel(new FloatRangeSliderModel(0f, 1f, 0.50f, 0.05f));
		sl3.setLockToStep(true);
		
		// Hor Slider 2
		Slider<Float> sl5 = new Slider<Float>() {

			@Override
			public void onChange(Float value) {
				System.out.println("onChange(" + value + ")");
			}

		};
		sl5.setSliderModel(new FloatRangeSliderModel(0f, 100f, 100f, 20f));
		sl5.setLockToStep(true);

		// Hor  Slider 3
		Slider<Float> sl6 = new Slider<Float>(screen, Slider.Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float value) {
				System.out.println("onChange(" + value + ")");
			}
		};
		sl6.setSliderModel(new FloatRangeSliderModel(0f, 1f, 0.50f, 0.05f));
		sl3.setLockToStep(false);
		
		win.addChild(sl4, "growx");
		win.addChild(sl1, "spany 3, growy");
		win.addChild(sl2, "spany 3, growy");
		win.addChild(sl3, "spany 3, growy, wrap");
		win.addChild(sl5, "growx, wrap");
		win.addChild(sl6, "growx");

		screen.addElement(win);

	}

}
