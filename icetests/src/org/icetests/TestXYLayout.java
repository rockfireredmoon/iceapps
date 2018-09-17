package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.Vector2f;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.Size;
import icetone.core.layout.XYLayout;

public class TestXYLayout extends IcesceneApp {

	static {
		AppInfo.context = TestXYLayout.class;
	}

	public static void main(String[] args) {
		TestXYLayout app = new TestXYLayout();
		app.start();
	}

	public TestXYLayout() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Panel panel = new Panel(screen, "Panel", new Vector2f(8, 8), new Size(300f, 300));
		panel.setLayoutManager(new XYLayout());

		final Vector2f bs = new Vector2f(64, 24);
		for (int i = 0; i < 10; i++) {
			addRandomButton(panel, bs);
		}

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

	private void addRandomButton(final Panel panel, final Vector2f bs) {
		Vector2f p = new Vector2f((int) ((float) Math.random() * (panel.getWidth() - bs.x)),
				(int) ((float) Math.random() * (panel.getHeight() - bs.y)));
		PushButton ba = new PushButton(screen);
		ba.setPreferredDimensions(new Size(bs));
		ba.onMouseReleased(evt -> addRandomButton(panel, bs));
		ba.setText((int) p.x + "," + (int) p.y);
		panel.addElement(ba, p);
	}
}
