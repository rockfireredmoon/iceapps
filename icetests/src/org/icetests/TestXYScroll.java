package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.Vector2f;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.controls.scrolling.ScrollPanel;
import icetone.core.Size;
import icetone.core.layout.FixedLayout;

public class TestXYScroll extends IcesceneApp {

	static {
		AppInfo.context = TestXYScroll.class;
	}

	public static void main(String[] args) {
		TestXYScroll app = new TestXYScroll();
		app.start();
	}

	public TestXYScroll() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		ScrollPanel scr = new ScrollPanel();
		scr.setScrollContentLayout(new FixedLayout());

		final Vector2f bs = new Vector2f(64, 24);
		for (int i = 0; i < 10; i++) {
			scr.addScrollableContent(addRandomButton(bs));
		}

		Panel panel = new Panel();
		panel.setResizable(true);
		panel.setMovable(true);
		panel.addElement(scr);
		panel.sizeToContent();
		screen.showElement(panel);

	}

	private PushButton addRandomButton(final Vector2f bs) {
		Vector2f p = new Vector2f((int) ((float) Math.random() * 255), (int) ((float) Math.random() * 255));
		PushButton ba = new PushButton(screen);
		ba.setPreferredDimensions(new Size(bs));
		ba.onMouseReleased(evt -> addRandomButton(bs));
		ba.setText((int) p.x + "," + (int) p.y);
		return ba;
	}
}
