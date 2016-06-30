package org.icescene.assets;

import java.util.Map;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetProcessor;
import com.jme3.asset.CloneableAssetProcessor;
import com.jme3.asset.cache.AssetCache;
import com.jme3.asset.cache.WeakRefCloneAssetCache;

public class ComponentKey extends AssetKey<ComponentDefinition> {
	
	private Map<String, String> variables;

	public ComponentKey() {
		super();
	}

	public ComponentKey(String name) {
		super(name);
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	@Override
	public ComponentKey clone() {
		return (ComponentKey) super.clone();
	}

    @Override
    public Class<? extends AssetCache> getCacheType() {
        return WeakRefCloneAssetCache.class;
    }

    @Override
    public Class<? extends AssetProcessor> getProcessorType() {
        return CloneableAssetProcessor.class;
    }
}
