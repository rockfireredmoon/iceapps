package org.icescene.help;

import java.util.prefs.Preferences;

import org.icescene.IcemoonAppState;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;

import icetone.core.Size;
import icetone.core.layout.FillLayout;
import icetone.extras.windows.PersistentWindow;
import icetone.extras.windows.SaveType;

public class HelpAppState extends IcemoonAppState<IcemoonAppState<?>> {

	private PersistentWindow helpWindow;
	private HelpPanel helpPanel;

	public HelpAppState(Preferences prefs) {
		super(prefs);
	}

	@Override
	protected void postInitialize() {
		helpWindow = new PersistentWindow(screen, "Help", 10, VAlign.Top, Align.Center, new Size(560, 400), true,
				SaveType.POSITION_AND_SIZE, prefs) {
			@Override
			protected void onCloseWindow() {
				super.onCloseWindow();
				app.getStateManager().detach(HelpAppState.this);
			}
		};
		helpWindow.setDestroyOnHide(true);
		helpWindow.getContentArea().setLayoutManager(new FillLayout());
		helpWindow.setMinimizable(true);
		helpWindow.setResizable(true);
		helpWindow.setMaximizable(true);
		helpWindow.setWindowTitle("Help");

		helpPanel = new HelpPanel(screen);

		helpWindow.getContentArea().addElement(helpPanel);
		screen.showElement(helpWindow);
	}

	@Override
	protected void onCleanup() {
		helpWindow.hide();
	}
}
