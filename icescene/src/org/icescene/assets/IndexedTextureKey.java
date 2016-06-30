package org.icescene.assets;

import com.jme3.asset.TextureKey;

public class IndexedTextureKey extends TextureKey {

    public IndexedTextureKey(String name, boolean flipY) {
        super(name, flipY);
    }

    public IndexedTextureKey(String name) {
        super(name);
    }

    public IndexedTextureKey() {
    }
    
}
