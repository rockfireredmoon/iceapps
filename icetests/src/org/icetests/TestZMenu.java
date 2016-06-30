package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.controls.Rotator;
import org.iceui.controls.XSeparator;
import org.iceui.controls.ZMenu;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.RadioButton;
import icetone.controls.buttons.RadioButtonGroup;
import icetone.controls.windows.Panel;
import icetone.core.layout.FlowLayout;

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
		screen.setUseCustomCursors(true);
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

		ButtonAdapter menuButton = new ButtonAdapter("Menu") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				ZMenu menu = new ZMenu(screen);

				// Ordinary menu items
				for (int i = 0; i < 15; i++) {
					menu.addMenuItem("This is menu item " + i, "MENU" + i);
				}
				menu.addMenuItem("This is <u>underlined</u>", "underline");
				menu.addMenuItem("This is <b>bold</b>", "bold");
				menu.addMenuItem("This is <i>italic</i>", "italic");

				// A CheckBox
				menu.addMenuItem("A Checkbox!", new CheckBox(), name);

				// Some radio buttons
				RadioButtonGroup rgb = new RadioButtonGroup();
				RadioButton r1 = new RadioButton();
				menu.addMenuItem("Radio 1!", r1, name);
				rgb.addButton(r1);
				RadioButton r2 = new RadioButton();
				rgb.addButton(r2);
				menu.addMenuItem("Radio 2!", r2, name);

				// A separator
				 menu.addMenuItem(null, new XSeparator(screen,
				 Orientation.HORIZONTAL), null).setSelectable(false);

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
				screen.addElement(menu);
				// menu.showMenu(null, getAbsoluteX(), getAbsoluteY() -
				// menu.getHeight());
				menu.showMenu(null, evt.getX(), evt.getY());
			}

			private void addSubMenu(ZMenu menu, int depth) {
				// A submenu
				ZMenu submenu = new ZMenu(screen);
				for (int i = 0; i < 30; i++) {
					if (i == 5) {
						if (depth < 6) {
							addSubMenu(submenu, depth + 1);
						}
					} else {
						submenu.addMenuItem("This is menu item " + i, "MENU" + i);
					}
				}
				menu.addMenuItem("A submenu", submenu, name);
			}
		};

		Panel window = new Panel(new FlowLayout());
		window.getTextPaddingVec().set(10, 10, 10, 10);
		window.addChild(menuButton);
		window.setIsResizable(false);
		window.sizeToContent();
		screen.addElement(window);

	}
}
