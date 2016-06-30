package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.IconTabControl;

import com.jme3.font.BitmapFont.Align;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.controls.windows.Panel;
import icetone.controls.windows.TabControl;
import icetone.core.Container;
import icetone.core.Element;
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

		screen.addElement(layoutVersion());
		// screen.addElement(createOptionsWindow());
		// screen.addElement(layoutVertical());
//		 screen.addElement(iconTabs());

	}

	private Element createOptionsWindow() {
		FancyWindow w = new FancyWindow();
		w.getContentArea().addChild(new Label("Align"));
		w.getContentArea().addChild(new ComboBox<Align>(Align.values()), "wrap");
		w.getContentArea().addChild(new Label("Text"));
		w.getContentArea().addChild(new TextField("some text"));
		w.pack(false);
		return w;
	}

	private Element createLayoutTabContent(int tab) {
		Container content1 = new Container(new MigLayout());
		for (int i = 0; i < 10; i++)
			content1.addChild(new Label("[" + tab + "] Label A-" + i), "wrap");
		return content1;
	}

	private Panel iconTabs() {
		IconTabControl tabs = new IconTabControl(screen);
		tabs.setUseSlideEffect(true);
		String[] icons = new String[] { screen.getStyle("Common").getString("arrowLeft"),
				screen.getStyle("Common").getString("arrowRight"), screen.getStyle("Common").getString("arrowUp"),
				screen.getStyle("Common").getString("arrowDown"), };
		for (int i = 0; i < icons.length; i++) {
			tabs.addTabWithIcon("Tab " + i, icons[i]);
			tabs.addTabChild(i, createLayoutTabContent(i));
		}
		Panel xcw = new Panel();
		xcw.setLayoutManager(new BorderLayout());
		xcw.addChild(tabs, BorderLayout.Border.CENTER);
		xcw.addChild(new Label("Icons"), BorderLayout.Border.NORTH);
		xcw.show();
		return xcw;
	}

	private Panel layoutVersion() {
		TabControl tabs = new TabControl();
		tabs.setUseSlideEffect(true);
		for (int i = 0; i < 5; i++) {
			tabs.addTab("Tab " + i, createLayoutTabContent(i));
		}
		Panel xcw = new Panel(new MigLayout("fill"));
		xcw.addChild(new Label("LayoutManager"), "wrap");
		xcw.addChild(tabs, "grow");
		return xcw;
	}

	private Panel layoutVertical() {
		TabControl tabs = new TabControl(Element.Orientation.VERTICAL);
		tabs.setUseSlideEffect(true);
		for (int i = 0; i < 5; i++) {
			tabs.addTab("Tab " + i);
			tabs.addTabChild(i, createLayoutTabContent(i));
		}
		// tabs.setColorMap("Interface/bgz.jpg");
		Panel xcw = new Panel();
		xcw.addChild(new Label("LayoutManager"), "wrap");
		xcw.addChild(tabs);
		return xcw;
	}
}
