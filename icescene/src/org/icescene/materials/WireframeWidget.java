package org.icescene.materials;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;

public class WireframeWidget extends Widget {

    public WireframeWidget(AssetManager assetManager, ColorRGBA color) {
        super(assetManager, color);
        getAdditionalRenderState().setWireframe(true);
    }
}
