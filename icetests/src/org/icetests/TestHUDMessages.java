package org.icetests;

import org.icelib.AppInfo;
import org.icescene.HUDMessageAppState;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.BaseElement;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

public class TestHUDMessages extends IcesceneApp {

	static {
		AppInfo.context = TestHUDMessages.class;
	}

	public static void main(String[] args) {
		TestHUDMessages app = new TestHUDMessages();
		app.start();
	}

	public TestHUDMessages() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		final HUDMessageAppState ham = new HUDMessageAppState();
		getStateManager().attach(ham);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		rootNode.attachChild(geom);

		BaseElement buttons = new BaseElement(screen);
		buttons.setLayoutManager(new MigLayout(screen));
		buttons.addElement(new PushButton("Test Info").onMouseReleased(
				evt -> ham.message(HUDMessageAppState.Channel.INFORMATION, "This is an information test!")));
		buttons.addElement(new PushButton("Test Error")
				.onMouseReleased(evt -> ham.message(HUDMessageAppState.Channel.ERROR, "This is an error test!")));
		buttons.addElement(new PushButton("Test Warn")
				.onMouseReleased(evt -> ham.message(HUDMessageAppState.Channel.WARNING, "This is a warning test!")));
		buttons.addElement(new PushButton("Test Broadcast").onMouseReleased(
				evt -> ham.message(HUDMessageAppState.Channel.BROADCAST, "This is a broadcast test!")));

		// Panel
		Panel p = new Panel(screen);
		p.setLayoutManager(new BorderLayout());
		p.addElement(buttons, Border.CENTER);
		screen.showElement(p);

	}

}
