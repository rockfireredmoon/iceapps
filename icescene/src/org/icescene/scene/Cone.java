package org.icescene.scene;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

public class Cone extends Geometry {

    public Cone(String name, AssetManager assetManager) {
        super(name, new com.jme3.scene.shape.Dome(2, 16, 2));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Green);
        mat.setColor("Diffuse", ColorRGBA.Green);
        mat.setFloat("Shininess", 10f);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        mat.getAdditionalRenderState().setWireframe(true);
        setMaterial(mat);
    }
}
