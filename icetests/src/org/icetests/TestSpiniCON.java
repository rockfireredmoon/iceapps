package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.Slider;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.layout.BasicLayoutManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

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
		screen.setUseCustomCursors(true);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		final Slider<Float> speedSlider = new Slider<Float>(screen, new Vector2f(150, 20), new Vector2f(200, 24),
				Element.Orientation.HORIZONTAL, true);
		speedSlider.setSliderModel(new FloatRangeSliderModel(0, 50, 1));

		final Element icon = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, new Vector2f(31, 31), Vector4f.ZERO,
				"Interface/Styles/Gold/Progress_0.png");
		icon.setScaleEW(false);
		icon.setScaleNS(false);
		icon.addControl(new AbstractControl() {
			private float rot;

			@Override
			protected void controlUpdate(float tpf) {
				rot -= tpf * speedSlider.getSelectedValue();
				if (rot < 0) {
					rot = FastMath.TWO_PI - tpf;
				}
				Quaternion fromAngles = new Quaternion().fromAngles(0, 0, rot -= tpf * (Float) speedSlider.getSelectedValue());
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

		Element background = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO,
				"Interface/bgx.jpg");
		background.addChild(icon);

		Panel window = new Panel(new BasicLayoutManager());
		window.addChild(background);
		window.addChild(speedSlider);

		screen.addElement(window);
		window.show();

	}
}
