package org.icetests;

import org.icescene.IcesceneApp;
import org.icescene.assets.MeshLoader;
import org.icescene.controls.Rotator;
import org.icescene.props.AbstractProp;
import org.icescene.props.EntityFactory;

import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;

public class TestProp extends IcesceneApp {

	public static void main(String[] args) {
		TestProp app = new TestProp();
		MeshLoader.setTexturePathsRelativeToMesh(true);
		app.start();
	}

	@Override
	protected void onSimpleInitApp() {
		flyCam.setMoveSpeed(100);
		flyCam.setDragToRotate(true);
		EntityFactory pf = new EntityFactory(this, rootNode);
		AbstractProp prop = pf.getProp("Prop-Paintings1#Prop-Painting1-WantedShadow");
		prop.getSpatial().rotate(0, -FastMath.QUARTER_PI * 2, 0);
		prop.getSpatial().addControl(new Rotator());
		getRootNode().attachChild(prop.getSpatial());

		// Need light for props
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(3));
		rootNode.addLight(al);
	}
}
