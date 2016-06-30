package org.icetests;

import org.icelib.AppInfo;
import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.UIButton;

import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

public class TestUIButton extends IcesceneApp {

	static {
		AppInfo.context = TestUIButton.class;
	}

	public static void main(String[] args) {
		TestUIButton app = new TestUIButton();
		app.start();
	}

	public TestUIButton() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseCustomCursors(true);
		screen.setUseUIAudio(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		SoundButton sb = new SoundButton("Sounds/Weather/Weather-Rain.ogg");

		Panel window = new Panel();
		window.setLayoutManager(new MigLayout("", "[]", "[]"));
		window.addChild(sb);
		window.pack(false);

		screen.addElement(window);
		window.show();

	}

	private float previewSize = 64f;

	private class SoundButton extends Container {

		private final UIButton selectButton;
		private final ButtonAdapter playButton;

		SoundButton(final String path) {
			super();
			setLayoutManager(new MigLayout(screen, "wrap 1", "[]", "[][][]"));

			// Top row
			final Label xLabel = new Label(Icelib.getBaseFilename(path));
			xLabel.setTextWrap(LineWrapMode.Character);

			// Middle row
			selectButton = new UIButton();
			selectButton.setButtonIcon(previewSize, previewSize, screen.getStyle("Common").getString("audioIcon"));

			Vector2f arrowSize = screen.getStyle("Common").getVector2f("arrowSize");
			playButton = new ButtonAdapter(screen, arrowSize.add(new Vector2f(3, 3))) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				}
			};
			playButton.setButtonIcon(arrowSize.x, arrowSize.y, screen.getStyle("Common").getString("arrowRight"));

			// Bottom row
			Container lc = new Container();
			lc.setLayoutManager(new BorderLayout());
			lc.addChild(ElementStyle.small(new Label("Play")), BorderLayout.Border.CENTER);
			lc.addChild(playButton, BorderLayout.Border.EAST);

			//
			addChild(xLabel, "ax 50%");
			addChild(selectButton, "ax 50%");
			addChild(lc, "al right");
		}
	}
}
