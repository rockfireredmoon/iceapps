package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.FancyWindow;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.Dial;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestDials extends IcesceneApp {

	static {
		AppInfo.context = TestDials.class;
	}

	public static void main(String[] args) {
		TestDials app = new TestDials();
		app.start();
	}

	public TestDials() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseCustomCursors(true);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Panel for actions and character selection
		FancyWindow xcw = new FancyWindow();
		xcw.getContentArea().setLayoutManager(new MigLayout("fill"));

		//
		Dial<Void> d1 = new Dial<Void>() {

			@Override
			public void onChange(int selectedIndex, Void value) {
				super.onChange(selectedIndex, value);
				setToolTipText("Vol: " + selectedIndex);
			}
			
		};
		xcw.getContentArea().addChild(d1, "growx, growy");
		d1.setToolTipText("Button with just text");

		xcw.sizeToContent();

		screen.addElement(xcw);

	}

}
