package org.icescene;

import com.jme3.asset.AssetManager;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;

public class DisableableFilterPostProcessor extends FilterPostProcessor {

    public DisableableFilterPostProcessor(AssetManager assetManager) {
        super(assetManager);
    }
    
    @Override
    public void setFilterState(Filter filter, boolean enabled) {
        super.setFilterState(filter, enabled);
    }
}
