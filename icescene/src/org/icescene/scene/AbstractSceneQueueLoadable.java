package org.icescene.scene;

public abstract class AbstractSceneQueueLoadable<T> implements SceneQueueLoadable<T> {
    
    protected T tileLocation;
    protected boolean unloaded;
    
    public AbstractSceneQueueLoadable() {
        
    }
    public AbstractSceneQueueLoadable(T tileLocation) {
        this.tileLocation = tileLocation;
    }

    public T getTileLocation() {
        return tileLocation;
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

    public void setTileLocation(T tileLocation) {
        this.tileLocation = tileLocation;
    }
    
}
