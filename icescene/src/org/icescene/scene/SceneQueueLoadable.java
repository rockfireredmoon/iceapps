package org.icescene.scene;

public interface SceneQueueLoadable<T> {
    T getTileLocation();
    boolean isUnloaded();
    void unload();
}
