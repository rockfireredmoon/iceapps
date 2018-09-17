package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.containers.TabControl;
import icetone.controls.containers.TabControl.TabButton;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.core.Container;
import icetone.core.BaseElement;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

public class TestTabs extends IcesceneApp {
	static {
		AppInfo.context = TestTabs.class;
	}

	public static void main(String[] args) {
		TestTabs app = new TestTabs();
		app.start();
	}

	public TestTabs() {
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

		screen.showElement(layoutVersion(Border.NORTH));
		screen.showElement(layoutVersion(Border.SOUTH));
		screen.showElement(layoutVertical(Border.EAST));
		screen.showElement(layoutVertical(Border.WEST));
		screen.showElement(iconTabs());

	}

	private BaseElement createLayoutTabContent(int tab) {
		Container c = new Container(screen);
		c.setLayoutManager(new MigLayout(screen, "wrap 1"));
		c.addElement(new ComboBox<String>(screen, "Value 1", "Value 2", "Value 33"));
		return c;

		// ScrollPanel content1 = new ScrollPanel(getScreen());
		// content1.setScrollContentLayout(new MigLayout(screen, "wrap 1"));
		// for (int i = 0; i < 20; i++)
		// content1.addScrollableContent(new Label("[" + tab + "] Label A-" +
		// i));
		// return content1;
	}

	private Panel iconTabs() {
		TabControl tabs = new TabControl(screen);
		tabs.setStyleClass("character-tabs");
		tabs.setUseSlideEffect(true);
		String[] icons = new String[] { "equip-tab", "charms-tab", "mods-tab" };
		for (int i = 0; i < icons.length; i++) {
			TabButton tb = new TabButton(screen);
			tb.setStyleClass(icons[i]);
			tabs.addTab(tb, createLayoutTabContent(i));
			// tabButton.setButtonIcon(icons[i]);
		}
		Panel xcw = new Panel(new BorderLayout());
		xcw.addElement(tabs, Border.CENTER);
		xcw.addElement(new Label("Icons"), Border.NORTH);
		xcw.show();
		return xcw;
	}

	private Panel layoutVersion(Border tabPlacement) {
		TabControl tabs = new TabControl(tabPlacement);
		tabs.setUseSlideEffect(true);
		for (int i = 0; i < 5; i++) {
			tabs.addTab("Tab " + i, createLayoutTabContent(i));
		}
		Panel xcw = new Panel(new BorderLayout());
		xcw.addElement(tabs, Border.CENTER);
		xcw.addElement(new Label("Horizontal"), Border.NORTH);
		return xcw;
	}

	private Panel layoutVertical(Border tabPlacement) {
		TabControl tabs = new TabControl(tabPlacement);
		tabs.setUseSlideEffect(true);
		for (int i = 0; i < 5; i++) {
			tabs.addTab("Tab " + i);
			tabs.addTabChild(i, createLayoutTabContent(i));
		}
		Panel xcw = new Panel(new BorderLayout());
		xcw.addElement(tabs, Border.CENTER);
		xcw.addElement(new Label("Vertical"), Border.NORTH);
		return xcw;
	}
}
