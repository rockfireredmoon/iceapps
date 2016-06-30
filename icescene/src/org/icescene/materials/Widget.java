package org.icescene.materials;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

public class Widget extends Material {
    
    public Widget(AssetManager assetManager, ColorRGBA color) {
        super(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        setBoolean("UseMaterialColors", true);
        setColor("Ambient", color);
        setColor("Diffuse", color);
        setFloat("Shininess", 10f);
    }
}
