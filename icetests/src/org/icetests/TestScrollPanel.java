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
import icetone.core.Orientation;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;

public class TestScrollPanel extends IcesceneApp {

	static {
		AppInfo.context = TestScrollPanel.class;
	}

	public static void main(String[] args) {
		TestScrollPanel app = new TestScrollPanel();
		app.start();
	}

	public TestScrollPanel() {
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

		Panel panel = new Panel();
		panel.setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[fill, grow][][]", "[fill, grow]"));

		ScrollPanel scr = new ScrollPanel(screen);
		((WrappingLayout) scr.getScrollContentLayout()).setOrientation(Orientation.HORIZONTAL);
		// scr.getScrollableArea().setIgnoreMouse(true);
		// scr.setScrollAreaLayout(new MigLayout(screen, "wrap 1, fill, ins 0,
		// gap 0", "[grow]", "[]"));
		// scr.setUseVerticalWrap(true);
		panel.addElement(scr);

		for (int i = 0; i < 10; i++) {
			scr.addScrollableContent(createPanel("Item number " + i, 64 * i));
		}
		screen.addElement(panel);

	}

	BaseElement createPanel(String n, float y) {
		BaseElement p = new BaseElement(new BorderLayout());
		p.setIgnoreMouse(true);

		Label l1 = new Label(n);
		ElementStyle.medium(l1);
		p.addElement(l1, Border.CENTER);

		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addElement(l2, Border.SOUTH);

		return p;
	}
}
