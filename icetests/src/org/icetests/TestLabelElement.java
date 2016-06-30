package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import icetone.controls.text.LabelElement;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestLabelElement extends IcesceneApp {

	static {
		AppInfo.context = TestLabelElement.class;
	}

	public static void main(String[] args) {
		TestLabelElement app = new TestLabelElement();
		app.start();
	}

	public TestLabelElement() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		Panel panel = new Panel(new MigLayout());
		panel.addChild(new LabelElement("Some <b>bold</b> and <i>italic text</i>!!"));
		screen.addElement(panel);

	}
}
