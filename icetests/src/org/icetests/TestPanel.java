package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;

public class TestPanel extends IcesceneApp {

	static {
		AppInfo.context = TestPanel.class;
	}

	public static void main(String[] args) {
		TestPanel app = new TestPanel();
		app.start();
	}

	public TestPanel() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		Panel panel = new Panel();
		panel.addElement(new PushButton("Test").setEnabled(false));
		screen.showElement(panel);

	}

}
