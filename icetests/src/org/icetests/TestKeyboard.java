package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import icetone.extras.controls.Keyboard;

public class TestKeyboard extends IcesceneApp {

	static {
		AppInfo.context = TestKeyboard.class;
	}

	public static void main(String[] args) {
		TestKeyboard app = new TestKeyboard();
		app.start();
	}

	public TestKeyboard() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		Keyboard panel = new Keyboard(screen);
		screen.showElement(panel);

	}

}
