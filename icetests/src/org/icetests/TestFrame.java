package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;

public class TestFrame extends IcesceneApp {

	static {
		AppInfo.context = TestFrame.class;
	}

	public static void main(String[] args) {
		TestFrame app = new TestFrame();
		app.start();
	}

	public TestFrame() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		Frame frame = new Frame();
		frame.setResizable(true);
		frame.setMovable(true);
		frame.setTitle("A Frame!");
		frame.getContentArea().addElement(new PushButton("Test").setEnabled(false));
		screen.showElement(frame);

	}

}
