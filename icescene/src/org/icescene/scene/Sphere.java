package org.icescene.scene;

import org.icescene.materials.WireframeWidget;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

public class Sphere extends Geometry {

    public Sphere(String name, AssetManager assetManager) {
        super(name, new com.jme3.scene.shape.Sphere(16, 16, 1));
        setMaterial(new WireframeWidget(assetManager, ColorRGBA.Green));
    }
}
