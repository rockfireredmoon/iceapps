package org.icetests;

import org.icescene.IcesceneApp;
import org.icescene.build.Manipulator;
import org.icescene.entities.EntityContext;

public class TestManipulator extends IcesceneApp {

	public static void main(String[] args) {
		TestManipulator app = new TestManipulator();
		app.start();
	}

	public TestManipulator() {
		setUseUI(true);
		// setStylePath("icetone/style/def/style_map.gui.xml");
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);
		Manipulator m = new Manipulator(EntityContext.create(this));
		m.configureProp();
		rootNode.attachChild(m.getSpatial());

	}

}
