package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import icetone.controls.containers.Panel;
import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.Slider;
import icetone.core.BaseElement;
import icetone.core.layout.BasicLayout;
import icetone.core.layout.FlowLayout;

public class TestSpiniCON extends IcesceneApp {

	static {
		AppInfo.context = TestSpiniCON.class;
	}

	public static void main(String[] args) {
		TestSpiniCON app = new TestSpiniCON();
		app.start();
	}

	public TestSpiniCON() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		final Slider<Float> speedSlider = new Slider<Float>(screen);
		speedSlider.setSliderModel(new FloatRangeSliderModel(0, 50, 1));

		final BaseElement icon = new BaseElement(screen, Vector4f.ZERO, "Interface/Styles/Gold/Progress_0.png");
		icon.addControl(new AbstractControl() {
			private float rot;

			@Override
			protected void controlUpdate(float tpf) {
				rot -= tpf * speedSlider.getSelectedValue();
				if (rot < 0) {
					rot = FastMath.TWO_PI - tpf;
				}
				Quaternion fromAngles = new Quaternion().fromAngles(0, 0,
						rot -= tpf * (Float) speedSlider.getSelectedValue());
				// fromAngles.normalizeLocal();
				spatial.center().move(icon.getX() + (icon.getWidth() / 2f), icon.getY() + (icon.getHeight() / 2f), 0)
						.setLocalRotation(fromAngles);
			}

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}

			@Override
			public Control cloneForSpatial(Spatial paramSpatial) {
				return null;
			}
		});

		BaseElement background = new BaseElement(screen, "Interface/bgx.jpg");
		background.addElement(icon);

		Panel window = new Panel(new FlowLayout());
		window.addElement(background);
		window.addElement(speedSlider);

		screen.addElement(window);
		window.show();

	}
}
