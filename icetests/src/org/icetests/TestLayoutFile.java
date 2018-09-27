package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.ui.XHTMLAlertBox;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.core.BaseElement;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.loader.MouseButtonTarget;
import icetone.extras.windows.AlertBox.AlertType;

public class TestLayoutFile extends IcesceneApp {

	static {
		AppInfo.context = TestLayoutFile.class;
	}

	public static void main(String[] args) {
		TestLayoutFile app = new TestLayoutFile();
		app.start();
	}

	public TestLayoutFile() {
		setUseUI(true);
	}

	/**
	 * The default left click event
	 */
	@MouseButtonTarget()
	public void login() {
		XHTMLAlertBox.alert(screen, "Alert", "You logged in!", AlertType.INFORMATION);
	}

	/**
	 * If you bind more than one method to an event of the same type on the same
	 * element, you must provide the element's style ID.
	 */
	@MouseButtonTarget(id = "login", button = MouseUIButtonEvent.RIGHT)
	public void loginBad(MouseUIButtonEvent evt) {
		XHTMLAlertBox.alert(screen, "Alert", "Press the LEFT mouse button!", AlertType.ERROR);

		// If the event is part of the signature, it is up to you to consume the
		// event
		evt.setConsumed();
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

		// screen.addElement(screen.loadFromLayout("layouts/SimpleWindow.yaml"));

		// Load from the layout and bind events to this instance
		BaseElement loginWindow = screen.loadFromLayout("layouts/Everything.yaml");
//		Element loginWindow = screen.loadFromLayout("layouts/LoginWindow.yaml",
//				new ObjectTargetLayoutContext(screen, this));

		// Can also bind individually
//		loginWindow.getElementByStyleId("close").bindReleased(e1 -> System.exit(0));

		// Add to screen
		screen.addElement(loginWindow);
	}

}
