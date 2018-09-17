package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.Vector4f;

import icetone.controls.containers.Panel;
import icetone.core.BaseElement;
import icetone.core.Size;
import icetone.core.layout.mig.MigLayout;

public class TestImage extends IcesceneApp {

	static {
		AppInfo.context = TestImage.class;
	}

	public static void main(String[] args) {
		TestImage app = new TestImage();
		app.start();
	}

	public TestImage() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

		BaseElement lineEl = new BaseElement(screen);
		lineEl.setBackgroundDimensions(new Size(256, 256));
		lineEl.setTexture("Effects/dust.png");

		BaseElement e2 = new BaseElement(new MigLayout("wrap 1"));
		e2.addElement(lineEl);
		e2.setTexture("bgx.jpg");
		e2.setMargin(new Vector4f(10, 10, 10, 10));

		Panel panel = new Panel(new MigLayout("wrap 1"));
		panel.addElement(e2);
		screen.showElement(panel);

	}
}
