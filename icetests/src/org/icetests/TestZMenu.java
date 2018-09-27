package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.controls.Rotator;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonGroup;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.buttons.RadioButton;
import icetone.controls.containers.Panel;
import icetone.controls.menuing.Menu;
import icetone.core.layout.FlowLayout;
import icetone.extras.chooser.ColorFieldControl;

public class TestZMenu extends IcesceneApp {

	static {
		AppInfo.context = TestZMenu.class;
	}

	public static void main(String[] args) {
		TestZMenu app = new TestZMenu();
		app.start();
	}

	public TestZMenu() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
		geom.addControl(new Rotator());
		


		Menu menu = new Menu(screen);

		// Ordinary menu items
		for (int i = 0; i < 15; i++) {
			menu.addMenuItem("This is menu item " + i, "MENU" + i);
		}
		menu.addMenuItem("This is <u>underlined</u>", "underline");
		menu.addMenuItem("This is <b>bold</b>", "bold");
		menu.addMenuItem("This is <i>italic</i>", "italic");

		// A CheckBox
		menu.addMenuItem("A Checkbox!", new CheckBox(), null);

		// Some radio buttons
		ButtonGroup rgb = new ButtonGroup();
		RadioButton r1 = new RadioButton();
		menu.addMenuItem("Radio 1!", r1, null);
		rgb.addButton(r1);
		RadioButton r2 = new RadioButton();
		rgb.addButton(r2);
		menu.addMenuItem("Radio 2!", r2, null);

		// A separator
		menu.addSeparator();
		
		menu.addMenuItem("Color", new ColorFieldControl(ColorRGBA.Red, false, false), null);
		
		// A separator
		menu.addSeparator();

		// Sub menus
		addSubMenu(menu, 0);

		// Another separator
		// menu.addMenuItem(null, new XSeparator(screen,
		// Orientation.HORIZONTAL), null).setSelectable(false);

		// A slider
		// ModelledSlider slider = new ModelledSlider(screen,
		// orgPosition, ModelledSlider.Orientation.HORIZONTAL, true) {
		// @Override
		// public void onChange(Object value) {
		// }
		// };
		// slider.setSliderModel(new
		// ModelledSlider.FloatRangeSliderModel(0, 10, 1));
		// menu.addMenuItem(null, slider, null).setSelectable(false);

		// Show menu
//		screen.addElement(menu);
//		 menu.showMenu(null, evt.getgetAbsoluteX(), getAbsoluteY() -
//		 menu.getHeight());

		PushButton menuButton = new PushButton("Menu") {
		};
		menuButton.onMouseReleased(evt -> {
			menu.showMenu(evt.getElement());
//			menu.showMenu(evt.getElement(), VAlign.Bottom, Align.Center, 10);
		});

		Panel window = new Panel(new FlowLayout());
		window.addElement(menuButton);
		screen.addElement(window);

	}

	private void addSubMenu(Menu menu, int depth) {
		// A submenu
		Menu submenu = new Menu(screen);
		for (int i = 0; i < 30; i++) {
			if (i == 5) {
				if (depth < 6) {
					addSubMenu(submenu, depth + 1);
				}
			} else {
				submenu.addMenuItem("This is menu item " + i, "MENU" + i);
			}
		}
		menu.addMenuItem("A submenu", submenu, menu.getStyleId());
	}
}
