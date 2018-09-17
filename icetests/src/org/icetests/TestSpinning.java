package org.icetests;

import org.icescene.IcesceneApp;

import com.jme3.math.Vector2f;

import icetone.controls.containers.Panel;
import icetone.extras.controls.BusySpinner;

public class TestSpinning extends IcesceneApp {

	public static void main(String[] args) {
		TestSpinning app = new TestSpinning();
		app.start();
	}

	public TestSpinning() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Panel xcw = new Panel(screen);
		xcw.addElement(new BusySpinner(screen).setSpeed(10f));
		screen.showElement(xcw);

	}
}
