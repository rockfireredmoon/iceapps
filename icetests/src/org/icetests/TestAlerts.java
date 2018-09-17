package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.ui.XHTMLAlertBox;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Window;
import icetone.extras.windows.AlertBox.AlertType;

public class TestAlerts extends IcesceneApp {

	static {
		AppInfo.context = TestAlerts.class;
	}

	public static void main(String[] args) {
		TestAlerts app = new TestAlerts();
		app.start();
	}

	public TestAlerts() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Window w = new Window("Alerts");
		w.getContentArea().addElement(new PushButton("Error").onMouseReleased(evt -> {
			XHTMLAlertBox.alert(screen, "Error",
					"A single line.",
					AlertType.ERROR);
			;
		}));

		w.getContentArea().addElement(new PushButton("Information").onMouseReleased(evt -> {
			XHTMLAlertBox.alert(screen, "Information",
					"Some <b>bold</b> and <i>italic</i> text that spans 2 lines or something yes it does.",
					AlertType.INFORMATION);
			;
		}));

		w.getContentArea().addElement(new PushButton("Progress").onMouseReleased(evt -> {
			XHTMLAlertBox.alert(screen, "Progress",
					"Some <b>bold</b> and <i>italic</i> text that spans 3 lines<br/>nor at least hopefully. The first wraps naturally, the 2nd with a newline.",
					AlertType.PROGRESS);
			;
		}));

		screen.showElement(w);
	}

}
