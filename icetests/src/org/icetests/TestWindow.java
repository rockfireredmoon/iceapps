package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Window;
import icetone.core.Size;
import icetone.extras.chooser.ColorFieldControl;

public class TestWindow extends IcesceneApp {

	static {
		AppInfo.context = TestWindow.class;
	}

	public static void main(String[] args) {
		TestWindow app = new TestWindow();
		app.start();
	}

	private ColorFieldControl color;

	public TestWindow() {
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

		Window win = new Window("A Window");
		win.getContentArea().setPreferredDimensions(new Size(200, 200));

		// SelectList<Integer> list = new SelectList<Integer>();
		// list.addListItem("List item 1", 1);
		// list.addListItem("List item 2", 1);
		// list.addListItem("List item 3", 1);
		// list.addListItem("List item 4", 1);
		// list.addListItem("List item 5", 1);
		// list.setTextVAlign(BitmapFont.VAlign.Top);
		// win.getContentArea().setLayoutManager(new FillLayout());
		// win.getContentArea().addElement(list);

		// Add window to screen (causes a layout)
		// screen.addElement(win);

		screen.addElement(new Window("Second Window").setUseCloseButton(true).setUseCollapseButton(true)
				.setPosition(200, 200).setPreferredDimensions(new Size(200, 200)));

	}

}
