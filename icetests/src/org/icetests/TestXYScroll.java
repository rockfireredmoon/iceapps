package org.icetests;

import java.awt.ScrollPane;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.windows.Panel;
import icetone.core.layout.FixedLayoutManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.XYLayoutManager;

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
		scr.setScrollContentLayout(new FixedLayoutManager());
		// panel.setLayoutManager(new XYLayoutManager());

		final Vector2f bs = new Vector2f(64, 24);
		for (int i = 0; i < 10; i++) {
			scr.addScrollableContent(addRandomButton(bs));
		}

		Panel panel = new Panel();
		panel.addChild(scr);
		panel.sizeToContent();
		
		screen.addElement(panel);

	}

	private ButtonAdapter addRandomButton(final Vector2f bs) {
		Vector2f p = new Vector2f((int) ((float) Math.random() * 255), (int) ((float) Math.random() * 255));
		ButtonAdapter ba = new ButtonAdapter(screen, p, bs) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				addRandomButton(bs);
			}
		};
		ba.setText((int) p.x + "," + (int) p.y);
		return ba;
	}
}
