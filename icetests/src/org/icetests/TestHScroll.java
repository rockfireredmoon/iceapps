package org.icetests;

import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;

public class TestHScroll extends IcesceneApp {

	public static void main(String[] args) {
		TestHScroll app = new TestHScroll();
		app.start();
	}

	public TestHScroll() {
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
		Panel panel = new Panel(screen, "Panel", new Vector2f(8, 8), new Vector2f(300f, screen.getHeight() - 16));
		panel.setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[fill, grow]", "[fill, grow]"));

		ScrollPanel scr = new ScrollPanel(screen);
		// scr.getScrollableArea().setIgnoreMouse(true);
		// scr.setScrollAreaLayout(new MigLayout(screen, "wrap 1, fill, ins 0,
		// gap 0", "[grow]", "[]"));
		// scr.setUseVerticalWrap(true);
		panel.addChild(scr);

		// Element el = new Element(screen, new Vector2f(600, 1600));
		// el.setColorMap("Interface/bgx.jpg");
		// scr.addScrollableContent(el);

		for (int i = 0; i < 20; i++) {
			final Element createPanel = createPanel(screen, "Item number " + i, 64 * i);
			createPanel.addClippingLayer(scr.getScrollBounds());
			scr.addScrollableContent(createPanel);
		}

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

	Element createPanel(ElementManager screen, String n, float y) {
		Element p = new Element(screen, UIDUtil.getUID(), new Vector2f(400, 64));
		p.setLayoutManager(new BorderLayout());
		p.setIgnoreMouse(true);

		Label l1 = new Label(screen, "Label1" + n);
		l1.setText(n);
		LUtil.noScaleNoDock(l1);
		l1.setIgnoreMouse(true);
		l1.setFont(screen.getStyle("Font").getString("mediumFont"));
		l1.setFontSize(screen.getStyle("Common").getFloat("mediumFontSize"));
		p.addChild(l1, BorderLayout.Border.CENTER);

		Label l2 = new Label(screen, "Label1" + n);
		LUtil.noScaleNoDock(l2);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		l2.setText("Label 2 for " + n);
		l2.setIgnoreMouse(true);
		p.addChild(l2, BorderLayout.Border.SOUTH);

		return p;
	}
}
