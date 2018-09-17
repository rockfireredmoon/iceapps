package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.ColorRGBA;

import icetone.controls.containers.Panel;
import icetone.controls.extras.Indicator;
import icetone.core.Orientation;
import icetone.core.layout.mig.MigLayout;

public class TestStdIndicator extends IcesceneApp {
	static {
		AppInfo.context = TestStdIndicator.class;
	}

	public static void main(String[] args) {
		TestStdIndicator app = new TestStdIndicator();
		app.start();
	}

	public TestStdIndicator() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setDragToRotate(true);
		inputManager.setCursorVisible(true);

		final Indicator ind = new Indicator(screen, Orientation.HORIZONTAL);
		ind.setIndicatorColor(ColorRGBA.randomColor());
		ind.setCurrentValue(50);
		ind.setMaxValue(100);

		Panel p = new Panel(new MigLayout("fill"));
		p.addElement(ind, "growx");

		screen.addElement(p);

	}
}
