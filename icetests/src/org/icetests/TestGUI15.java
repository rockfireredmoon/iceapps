package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.SelectList;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.controls.windows.TabControl;
import icetone.controls.windows.Window;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;

public class TestGUI15 extends IcesceneApp {

	static {
		AppInfo.context = TestGUI15.class;
	}

	public static void main(String[] args) {
		TestGUI15 app = new TestGUI15();
		app.start();
	}

	public TestGUI15() {
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

		Window win = new Window(screen, "Window", new Vector2f(125, 145), new Vector2f(400, 400));
		win.setLayoutManager(new MigLayout("fill, ins 24 0 0 0 0, gap 0"));

		TabControl tc = new TabControl();
		tc.addTab("Tab 1");
		win.addChild(tc, "growx, wrap");

		tc.addTabChild(0, createPanel("P1"));

		screen.addElement(win);

	}

	Element createPanel(String n) {
		Panel p = new Panel();
		p.setIsResizable(false);
		p.setIsMovable(false);
		p.setScaleNS(true);
		p.setScaleEW(true);

		MigLayout l = new MigLayout("gap 0, ins 0, wrap 1", "[grow, fill]", "[][grow, fill][][]");
		p.setLayoutManager(l);

		Label l1 = new Label("Label0" + n);
		l1.setText("Label 0");
		p.addChild(l1);

		SelectList<Integer> list = new SelectList<Integer>();
		list.addListItem("List 1 that is fairly long", 1);
		list.addListItem("List 2 that is fairly long", 1);
		list.addListItem("List 3 that is fairly long", 1);
		list.addListItem("List 4 that is fairly long", 1);
		list.setTextVAlign(BitmapFont.VAlign.Top);
		p.addChild(list);

		l1 = new Label(screen, "Label1" + n);
		l1.setText("Label 1");
		p.addChild(l1, "wrap");

		Label l2 = new Label(screen, "Label1" + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		l2.setText("Label 2");
		p.addChild(l2);
		return p;
	}
}
