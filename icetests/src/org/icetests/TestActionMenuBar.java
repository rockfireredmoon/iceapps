package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.actions.ActionMenu;
import org.iceui.actions.ActionMenuBar;
import org.iceui.actions.AppAction;
import org.iceui.actions.AppAction.Style;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class TestActionMenuBar extends IcesceneApp {

	static {
		AppInfo.context = TestActionMenuBar.class;
	}

	public static void main(String[] args) {
		TestActionMenuBar app = new TestActionMenuBar();
		app.start();
	}

	public TestActionMenuBar() {
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

		ActionMenuBar menuBar = new ActionMenuBar(screen);
		menuBar.addActionMenu(new ActionMenu("File"));
		menuBar.addAction(new AppAction("Open").setMenu("File").onAction(evt -> {
			System.out.println("Open");
		}));
		menuBar.addAction(new AppAction("Exit").setMenu("File").onAction(evt -> {
			System.out.println("Exit");
		}));
		menuBar.addAction(new AppAction("Toggle").setStyle(Style.TOGGLE).setMenu("File").onAction(evt -> {
			System.out.println("Toggle " + evt.getSourceAction().isActive());
		}));

		screen.showElement(menuBar);
	}

}
