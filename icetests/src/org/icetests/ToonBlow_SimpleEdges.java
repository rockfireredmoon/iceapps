package org.icetests;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;

public class ToonBlow_SimpleEdges extends SimpleApplication {

	public static void main(String[] args) {
		ToonBlow_SimpleEdges app = new ToonBlow_SimpleEdges();
		app.start();
	}

	@Override
	public void simpleInitApp() {

		Spatial toon = assetManager.loadModel("Models/ToonBlow/toon.obj");
		Material mat = assetManager.loadMaterial("Materials/ToonBlow/ToonBlow_SimpleEdges.j3m");
		toon.setMaterial(mat);
		TangentBinormalGenerator.generate(toon);
		rootNode.attachChild(toon);

//		Spatial toon2 = assetManager.loadModel("Models/ToonBlow/toon.obj");
//		Material mat2 = assetManager.loadMaterial("Materials/ToonBlow/ToonBlow_Base_Specular.j3m");
//		toon2.setMaterial(mat2);
//		TangentBinormalGenerator.generate(toon2);
//		toon2.setLocalTranslation(-2f, 0, 0);
//		rootNode.attachChild(toon2);
//
//		Spatial toon3 = assetManager.loadModel("Models/ToonBlow/toon.obj");
//		Material mat3 = assetManager.loadMaterial("Materials/ToonBlow/ToonBlow_Base.j3m");
//		toon3.setMaterial(mat3);
//		TangentBinormalGenerator.generate(toon3);
//		toon3.setLocalTranslation(-4f, 0, 0);
//		rootNode.attachChild(toon3);

		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
		dl.setColor(new ColorRGBA(1, 1, 1, 1));
		rootNode.addLight(dl);

		flyCam.setMoveSpeed(5);

	}

	@Override
	public void simpleUpdate(float tpf) {
		viewPort.setBackgroundColor(ColorRGBA.Gray);

	}

}
