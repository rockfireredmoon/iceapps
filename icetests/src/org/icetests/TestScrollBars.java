package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.scrolling.ScrollBar;
import icetone.controls.scrolling.Scrollable;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Element.Orientation;
import icetone.core.layout.mig.MigLayout;

public class TestScrollBars extends IcesceneApp {

	static {
		AppInfo.context = TestScrollBars.class;
	}

	public static void main(String[] args) {
		TestScrollBars app = new TestScrollBars();
		app.start();
	}

	public TestScrollBars() {
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
		panel.setLayoutManager(new MigLayout(screen, "wrap 2", "[fill, grow][]", "[fill, grow][]"));

		Label child = new Label("Content");
		panel.addChild(child);
		Scrollable scrollable = new Scrollable() {

			@Override
			public void setScrollAreaPositionTo(float relativeScrollAmount, Orientation or) {
			}

			@Override
			public int getTrackInc() {
				return 5;
			}

			@Override
			public float getScrollableArea(Orientation or) {
				return or == Orientation.VERTICAL ? panel.getHeight() + 200 : panel.getWidth() + 200;
			}

			@Override
			public float getScrollBounds(Orientation or) {
				return or == Orientation.VERTICAL ? panel.getHeight()  : panel.getWidth();
			}

			@Override
			public int getButtonInc() {
				return 10;
			}
		};
		panel.addChild(new ScrollBar(screen, scrollable, Orientation.VERTICAL));
		panel.addChild(new ScrollBar(screen, scrollable, Orientation.HORIZONTAL));
		panel.addChild(new Label("*"));

		screen.addElement(panel);

	}

}
