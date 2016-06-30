package org.icescene.props;

import com.jme3.scene.Spatial;

public interface SpatialProvider<T extends Spatial> {
    
    T getProp(String name);
}
