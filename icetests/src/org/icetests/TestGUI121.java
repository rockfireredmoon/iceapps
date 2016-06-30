package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;

public class TestGUI121 extends IcesceneApp {
	
	static {
		AppInfo.context = TestGUI121.class;
	}

	public static void main(String[] args) {
		TestGUI121 app = new TestGUI121();
		app.start();
	}

	public TestGUI121() {
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

		// Panel for actions and character selection
		Panel panel = new Panel();
		panel.setIsResizable(false);
		panel.setLayoutManager(new MigLayout("", "[grow, fill]", "[]"));

		// Buttons
		Container buttons = new Container();
		buttons.setLayoutManager(
				new MigLayout("fill", "[align center, shrink]4!:push[align center, shrink]4!:push[align center, shrink]"));
		ButtonAdapter create = new ButtonAdapter("Create Long Button");
		buttons.addChild(create, "");
		panel.addChild(buttons);

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

	Element createPanel(ElementManager screen, String n) {
		Element p = new Element();
		MigLayout l = new MigLayout("ins 0, gap 0, fill", "", "");
		p.setLayoutManager(l);

		Label l1 = new Label("Label 1");
		p.addChild(l1, "wrap, height 20, width 100%, shrink");

		Label l2 = new Label("Label 2");
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addChild(l2, "grow, height 100%, width 100%, aligny top");
		return p;
	}


}
