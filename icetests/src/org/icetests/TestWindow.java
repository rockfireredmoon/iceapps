package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.color.ColorFieldControl;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.SelectList;
import icetone.controls.windows.Window;

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

		Window win = new Window();

		SelectList<Integer> list = new SelectList<Integer>();
		list.addListItem("List item 1", 1);
		list.addListItem("List item 2", 1);
		list.addListItem("List item 3", 1);
		list.addListItem("List item 4", 1);
		list.addListItem("List item 5", 1);
		list.setTextVAlign(BitmapFont.VAlign.Top);
		win.getContentArea().addChild(list);

		// Add window to screen (causes a layout)
		screen.addElement(win);

	}

}
