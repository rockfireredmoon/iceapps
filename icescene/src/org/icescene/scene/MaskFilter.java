package org.icescene.scene;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.Texture;

public class MaskFilter extends Filter {

    private Texture mask;

    public MaskFilter() {
        super("MaskFilter");
    }

    @Override
    protected void initFilter(final AssetManager manager, final RenderManager renderManager, final ViewPort vp,
            final int w, final int h) {
        material = new Material(manager, "MatDefs/Filters/Mask.j3md");
        material.setTexture("Mask", mask);
    }

    public void setMaskTexture(Texture mask) {
        this.mask = mask;
    }

    @Override
    protected Material getMaterial() {
        return material;
    }
}

