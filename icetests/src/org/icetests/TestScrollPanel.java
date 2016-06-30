package org.icetests;

import org.icelib.AppInfo;
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
import icetone.core.layout.BorderLayout;
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

	protected void configureScreen() {
		super.configureScreen();
		screen.setUseToolTips(false);
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

		for (int i = 0; i < 10; i++) {
			final Element createPanel = createPanel("Item number " + i, 64 * i);
//			createPanel.addClippingLayer(scr.getScrollBounds());
			scr.addScrollableContent(createPanel);
//			panel.addChild(createPanel, "wrap");
		}

		// Add window to screen (causes a layout)
		screen.addElement(panel);

		// System.err.println("Track is desc of Thumb: " +
		// scr.getHorizontalScrollBar().getScrollTrack().isDescendantOf(scr.getHorizontalScrollBar().getScrollThumb()));
		// System.err.println("Thumb is desc of Trace: " +
		// scr.getHorizontalScrollBar().getScrollThumb().isDescendantOf(scr.getHorizontalScrollBar().getScrollTrack()));

	}

	Element createPanel(String n, float y) {
		Element p = new Element(new BorderLayout());
		p.setIgnoreMouse(true);
		p.setMinDimensions(new Vector2f(200, 64));

		Label l1 = new Label(n);
		l1.setFont(screen.getStyle("Font").getString("mediumFont"));
		l1.setFontSize(screen.getStyle("Common").getFloat("mediumFontSize"));
		p.addChild(l1, BorderLayout.Border.CENTER);

		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addChild(l2, BorderLayout.Border.SOUTH);

		return p;
	}
}
