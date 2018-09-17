package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.layout.mig.MigLayout;

public class TestScrollArea extends IcesceneApp {

	static {
		AppInfo.context = TestScrollArea.class;
	}

	public static void main(String[] args) {
		TestScrollArea app = new TestScrollArea();
		app.start();
	}

	public TestScrollArea() {
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
		panel.setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[fill, grow]", "[fill, grow]"));

		ScrollPanel scr = new ScrollPanel();
		// scr.getScrollableArea().setIgnoreMouse(true);
		scr.setScrollContentLayout(new MigLayout("wrap 1, fill, ins 0, gap 0", "[grow]", "[]"));
		scr.getScrollableArea().setText(
				"A list\nOf items\nThat should\nBe scrollable\nBut probably\nIsnt, lets see\nIf it works\nBet it doesnt\n");
		panel.addElement(scr);

		// for (int i = 0; i < 10; i++)
		// scr.addScrollableChild(createPanel(screen, "Item number " + i));

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

	BaseElement createPanel(BaseScreen screen, String n) {
		BaseElement p = new BaseElement(new MigLayout(screen, "ins 0, wrap 1, fill", "[grow, fill]", ""));
		p.setTexture("Interface/bgw.jpg");

		Label l1 = new Label(n);
		ElementStyle.medium(l1);
		p.addElement(l1);

		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addElement(l2, "growx");
		return p;
	}
}
