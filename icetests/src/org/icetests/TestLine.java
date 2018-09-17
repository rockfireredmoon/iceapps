package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.controls.containers.Panel;
import icetone.core.layout.mig.MigLayout;
import icetone.xhtml.scene.LineElement;

public class TestLine extends IcesceneApp {

	static {
		AppInfo.context = TestLine.class;
	}

	public static void main(String[] args) {
		TestLine app = new TestLine();
		app.start();
	}

	public TestLine() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		Panel panel = new Panel(new MigLayout("wrap 1"));

		LineElement lineEl = new LineElement(screen, new Vector2f(5, 5), new Vector2f(50, 25), ColorRGBA.Blue, 1);
		lineEl.setIgnoreGlobalAlpha(true);
		panel.addElement(lineEl);
		screen.showElement(panel);

	}
}
