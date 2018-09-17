package org.icetests;

import java.util.concurrent.Callable;

import org.icelib.AppInfo;
import org.icemoon.game.maps.WorldMapAppState;
import org.icescene.IcesceneApp;
import org.icescene.MiniMapWindow;
import org.icescene.ui.XHTMLAlertBox;
import org.iceui.controls.ElementStyle;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.core.layout.FillLayout;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.appstates.FrameManagerAppState;
import icetone.extras.windows.AlertBox;
import icetone.extras.windows.AlertBox.AlertType;
import icetone.extras.windows.DialogBox;
import icetone.extras.windows.InputBox;

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
		stateManager.attach(new FrameManagerAppState(screen));

		screen.showElement(createWin1(screen));
		screen.showElement(createWin2(screen));
		screen.showElement(createWin3(screen));
		screen.showElement(createWin4(screen));
		screen.showElement(createWin5(screen));
		screen.showElement(createWin6(screen));

	}

	private Frame createWin1(BaseScreen screen) {
		MiniMapWindow win1 = new MiniMapWindow(screen, getPreferences()) {

			@Override
			protected void onZoomIn() {
			}

			@Override
			protected void onZoomOut() {
			}

			@Override
			protected void onOpenWorldMap() {
				ToolKit.get().getApplication().getStateManager().attach(new WorldMapAppState());
			}

			@Override
			protected void onHome() {
			}
		};
		return win1;
	}

	private Frame createWin2(BaseScreen screen) {
		Frame win2 = new Frame(screen, "Window2", new Vector2f(100, 100), true);
		win2.getDragBar().setText("2 Fancy Window 2!");
		win2.getContentArea().setLayoutManager(new MigLayout(screen, "", "[][]"));
		PushButton b1 = new PushButton(screen, "Confirm");
		b1.onMouseReleased(evt -> {
			final DialogBox dialog = new DialogBox(screen, true) {
				{
					setStyleClass("large");
				}

				@Override
				public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
					hide();
				}

				@Override
				public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
					hide();
				}
			};
			dialog.setDestroyOnHide(true);
			ElementStyle.warningColor(dialog.getDragBar());
			dialog.getDragBar().setText("Confirm Deletion");
			dialog.setButtonOkText("Delete");
			dialog.setButtonOkTooltipText("Delete something!");
			dialog.setMsg(String.format(
					"Nuuuu! Are you sure about deleting %s? I mean " + "like REALLY sure. Deletion is pretty final..",
					"SOME CHARACTER"));
			dialog.setResizable(false);
			dialog.setMovable(false);
			dialog.setModal(true);
			screen.showElement(dialog, ScreenLayoutConstraints.center);

			new Thread() {
				public void run() {
					try {
						Thread.sleep(5000);
						screen.getApplication().enqueue(new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								AlertBox.alert(screen, "Test", "Test Again", AlertType.ERROR);
								return null;
							}

						});
					} catch (Exception e) {
					}
				}
			}.start();
		});
		b1.setToolTipText("A tooltip!");
		PushButton b2 = new PushButton(screen, "Input");
		b2.onMouseReleased(evt -> {
			final InputBox dialog = new InputBox(screen, new Vector2f(15, 15), true) {

				{
					setStyleClass("large");
				}

				@Override
				public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
					hide();
				}

				@Override
				public void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled) {
				}
			};
			dialog.setDestroyOnHide(true);
			ElementStyle.warningColor(dialog.getDragBar());
			dialog.setWindowTitle("New Clone");
			dialog.setButtonOkText("Clone");
			dialog.setMsg("Copying something or something");
			dialog.setResizable(false);
			dialog.setMovable(false);
			dialog.setModal(true);
			screen.showElement(dialog, ScreenLayoutConstraints.center);
		});
		b2.setToolTipText("A tooltip!");
		win2.setMinimizable(true);
		win2.getContentArea().addElement(b1);
		win2.getContentArea().addElement(b2);
		win2.getContentArea().addElement(new PushButton("B2").onMouseReleased(evt -> {
			Frame w = new Frame();
			w.setCloseable(true);
			w.setPosition((float) (Math.random() * 500f), (float) (Math.random() * 500f));
			w.setTitle("Stuff");
			w.setResizable(true);
			w.getContentArea().addElement(new Label("More stuff"));
			screen.showElement(w);
		}));
		win2.sizeToContent();
		return win2;
	}

	private Frame createWin3(BaseScreen screen) {
		Frame win3 = new Frame(screen, new Vector2f(200, 200), true);
		win3.getDragBar().setText("3 Fancy Window 3!");
		win3.getContentArea().setLayoutManager(new MigLayout(screen, "", "[]", "[]"));
		ComboBox<Integer> lc = new ComboBox<Integer>(screen);
		lc.addListItem("Item 1", 1);
		lc.addListItem("Item 2", 2);
		lc.addListItem("Item 3", 3);
		lc.addListItem("Item 4", 4);
		win3.getContentArea().addElement(lc);
		win3.sizeToContent();
		win3.setMaximizable(true);
		return win3;
	}

	private Frame createWin4(BaseScreen screen) {
		Frame win4 = new Frame(screen, true);
		win4.setResizable(true);
		win4.getContentArea().setLayoutManager(new FillLayout());
		win4.setMinimizable(true);
		win4.setMaximizable(true);
		return win4;
	}

	private Frame createWin5(BaseScreen screen) {
		Frame win5 = new Frame(screen, new Vector2f(400, 400), true);
		win5.getDragBar().setText("5 Fancy Window 5!");
		win5.getContentArea().setLayoutManager(new MigLayout(screen, "", "[][]"));
		PushButton b1 = new PushButton(screen, "A Button");
		b1.onMouseReleased(evt -> XHTMLAlertBox.alert(screen, "ALERT!",
				"Some <b>bold</b> and <i>italic</i> text that spans a few lines or something yes it does.",
				AlertType.ERROR));
		win5.setResizable(true);
		win5.setMinimizable(true);
		win5.setMaximizable(true);
		win5.getContentArea().addElement(b1);
		return win5;
	}

	private Frame createWin6(BaseScreen screen) {
		Frame win6 = new Frame(screen, new Vector2f(500, 500), false);
		win6.getDragBar().setText("6 Fancy Window 6!");
		win6.setResizable(true);
		return win6;
	}
}
