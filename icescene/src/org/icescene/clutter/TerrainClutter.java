package org.icescene.clutter;

import org.icescene.scene.SceneQueueLoadable;

import com.jme3.scene.Node;

public class TerrainClutter extends Node implements SceneQueueLoadable<TerrainClutterTile> {
    private final TerrainClutterTile tile;
    private boolean unloaded;

    public TerrainClutter(TerrainClutterTile tile) {
        this.tile= tile;
    }

    public TerrainClutterTile getTileLocation() {
        return tile;
    }
    
    public boolean isUnloaded() {
        return unloaded;
    }
    
    public void unload() {
        if(isUnloaded()) {
            throw new IllegalStateException("Already unloaded");
        }
        unloaded = true;
    }
    
}
