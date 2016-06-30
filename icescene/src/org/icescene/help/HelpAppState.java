package org.icescene.help;

import java.util.prefs.Preferences;

import org.icescene.IcemoonAppState;
import org.iceui.HPosition;
import org.iceui.VPosition;
import org.iceui.controls.FancyPersistentWindow;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.SaveType;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.layout.FillLayout;

public class HelpAppState extends IcemoonAppState<IcemoonAppState<?>> {

	private FancyPersistentWindow helpWindow;
	private HelpPanel helpPanel;

	public HelpAppState(Preferences prefs) {
		super(prefs);
	}

	@Override
	protected void postInitialize() {
		helpWindow = new FancyPersistentWindow(screen, "Help", 10, VPosition.TOP, HPosition.CENTER, new Vector2f(560, 400),
				FancyWindow.Size.SMALL, true, SaveType.POSITION_AND_SIZE, prefs) {
			@Override
			protected void onCloseWindow() {
				super.onCloseWindow();
				app.getStateManager().detach(HelpAppState.this);
			}
		};
		helpWindow.setDestroyOnHide(true);
		helpWindow.getContentArea().setLayoutManager(new FillLayout());
		helpWindow.setMinimizable(true);
		helpWindow.setIsResizable(true);
		helpWindow.setMaximizable(true);
		helpWindow.setWindowTitle("Help");

		helpPanel = new HelpPanel(screen);

		helpWindow.getContentArea().addChild(helpPanel);

		screen.addElement(helpWindow, null, true);
		helpWindow.showWindow();
	}

	@Override
	protected void onCleanup() {
		helpWindow.hide();
	}
}
