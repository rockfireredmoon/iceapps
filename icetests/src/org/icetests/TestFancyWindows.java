package org.icetests;

import java.util.concurrent.Callable;
import java.util.prefs.Preferences;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.ui.WindowManagerAppState;
import org.icescene.ui.XHTMLAlertBox;
import org.iceui.controls.FancyAlertBox;
import org.iceui.controls.FancyAlertBox.AlertType;
import org.iceui.controls.FancyDialogBox;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.FancyWindow.Size;
import org.iceui.controls.UIUtil;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.lists.ComboBox;
import icetone.core.Element;
import icetone.core.Screen;
import icetone.core.layout.FillLayout;
import icetone.core.layout.mig.MigLayout;

public class TestFancyWindows extends IcesceneApp {

	static {
		AppInfo.context = TestFancyWindows.class;
	}

	public static void main(String[] args) {
		TestFancyWindows app = new TestFancyWindows();
		app.start();
	}

	public TestFancyWindows() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		rootNode.attachChild(geom);
		final WindowManagerAppState windowManagerAppState = new WindowManagerAppState(Preferences.systemRoot().node("test"));

		// FancyWindow win1 = createWin1(screen);
		FancyWindow win2 = createWin2(screen);
		win2.setMinimizable(true);
		FancyWindow win3 = createWin3(screen);
		win3.setMaximizable(true);
		FancyWindow win4 = createWin4(screen);
		win4.setMinimizable(true);
		win4.setMaximizable(true);
		FancyWindow win5 = createWin5(screen);
		win5.setMinimizable(true);
		win5.setMaximizable(true);
		FancyWindow win6 = createWin6(screen);

		stateManager.attach(windowManagerAppState);

		// win2.sizeToContent();

		// screen.addElement(win1);
		screen.addElement(win2, null, true);
		screen.addElement(win3);
		screen.addElement(win4);
		screen.addElement(win5);
		screen.addElement(win6);

		win2.showWindow();

	}

	private FancyWindow createWin1(Screen screen) {
		FancyWindow win1 = new FancyWindow(screen, "Window1", new Vector2f(0, 0), Size.MINIMAP, false);
		return win1;
	}

	private FancyWindow createWin2(Screen screen) {
		FancyWindow win2 = new FancyWindow(screen, "Window2", new Vector2f(100, 100), Size.SMALL, true);
		win2.getDragBar().setText("2 Fancy Window 2!");
		win2.getContentArea().setLayoutManager(new MigLayout(screen, "", "[][]"));
		ButtonAdapter b1 = new ButtonAdapter(screen, "A Button") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				final FancyDialogBox dialog = new FancyDialogBox(screen, new Vector2f(15, 15), FancyWindow.Size.LARGE, true) {
					@Override
					public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
						hideWindow();
					}

					@Override
					public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
						hideWindow();
					}
				};
				dialog.setDestroyOnHide(true);
				dialog.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
				dialog.getDragBar().setText("Confirm Deletion");
				dialog.setButtonOkText("Delete");
				dialog.setButtonOkTooltipText("Delete something!");
				dialog.setMsg(String.format(
						"Nuuuu! Are you sure about deleting %s? I mean " + "like REALLY sure. Deletion is pretty final..",
						"SOME CHARACTER"));

				// TODO packing is what gives that weird 1 pixel gap

				dialog.sizeToContent();
				dialog.setIsResizable(false);
				dialog.setIsMovable(false);
				UIUtil.center(screen, dialog);
				screen.addElement(dialog, null, true);
				dialog.showAsModal(true);

				new Thread() {
					public void run() {
						try {
							Thread.sleep(5000);
							app.enqueue(new Callable<Void>() {

								@Override
								public Void call() throws Exception {
									FancyAlertBox.alert(screen, "Test", "Test Again", AlertType.ERROR);
									return null;
								}

							});
						} catch (Exception e) {
						}
					}
				}.start();
			}
		};
		b1.setToolTipText("A tooltip!");
		win2.getContentArea().addChild(b1);
		win2.sizeToContent();
		return win2;
	}

	private FancyWindow createWin3(Screen screen) {
		FancyWindow win3 = new FancyWindow(screen, new Vector2f(200, 200), Size.SMALL, true);
		win3.getDragBar().setText("3 Fancy Window 3!");
		win3.getContentArea().setLayoutManager(new MigLayout(screen, "", "[]", "[]"));
		ComboBox<Integer> lc = new ComboBox<Integer>(screen) {

			@Override
			public void onChange(int selectedIndex, Integer value) {
			}
		};
		lc.addListItem("Item 1", 1);
		lc.addListItem("Item 2", 2);
		lc.addListItem("Item 3", 3);
		lc.addListItem("Item 4", 4);
		win3.getContentArea().addChild(lc);
		win3.sizeToContent();
		return win3;
	}

	private FancyWindow createWin4(Screen screen) {
		FancyWindow win4 = new FancyWindow(screen, new Vector2f(300, 300), Size.SMALL, true);
		win4.getDragBar().setText("4 Fancy Window 4!");
		Element en = new Element(screen, Vector4f.ZERO, "Interface/bgw.jpg");
		win4.getContentArea().setLayoutManager(new FillLayout());
		win4.getContentArea().addChild(en);
		win4.sizeToContent();
		return win4;
	}

	private FancyWindow createWin5(Screen screen) {
		FancyWindow win5 = new FancyWindow(screen, new Vector2f(400, 400), Size.LARGE, true);
		win5.getDragBar().setText("5 Fancy Window 5!");
		win5.getContentArea().setLayoutManager(new MigLayout(screen, "", "[][]"));
		ButtonAdapter b1 = new ButtonAdapter(screen, "A Button") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				XHTMLAlertBox.alert(screen, "ALERT!",
						"Some <b>bold</b> and <i>italic</i> text that spans a few lines or something yes it does.",
						AlertType.ERROR);
			}
		};
		win5.setIsResizable(true);
		win5.getContentArea().addChild(b1);
		win5.sizeToContent();
		return win5;
	}

	private FancyWindow createWin6(Screen screen) {
		FancyWindow win6 = new FancyWindow(screen, new Vector2f(500, 500), Size.LARGE, false);
		win6.getDragBar().setText("6 Fancy Window 6!");
		// win5.pack(false);
		win6.setIsResizable(true);
		return win6;
	}
}
