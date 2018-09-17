package org.icetests;

import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.OSRViewPort;
import icetone.controls.containers.Panel;
import icetone.core.Size;

public class TestOSRViewPort extends IcesceneApp {

	public static void main(String[] args) {
		TestOSRViewPort app = new TestOSRViewPort();
		app.start();
	}

	public TestOSRViewPort() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		OSRViewPort vp = new OSRViewPort(screen, new Size(100, 100));

		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);
		geom.addControl(new Rotator());

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		mat.getAdditionalRenderState().setWireframe(true);
		geom.setMaterial(mat);

		Node n = new Node();
		n.attachChild(geom);

		vp.setOSRBridge(n, 100, 100);

		Panel p = new Panel();
		p.addElement(vp);
		screen.addElement(p);

	}

	class Rotator extends AbstractControl {

		@Override
		protected void controlUpdate(float tpf) {
			spatial.rotate(0, tpf, 0);
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}
	}
}
