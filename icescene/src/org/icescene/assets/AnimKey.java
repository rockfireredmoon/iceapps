package org.icescene.assets;

import java.util.List;

import com.jme3.animation.Animation;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetProcessor;
import com.jme3.asset.CloneableAssetProcessor;
import com.jme3.asset.cache.AssetCache;
import com.jme3.asset.cache.WeakRefCloneAssetCache;

public class AnimKey extends AssetKey<List<Animation>> {

	public AnimKey(String name) {
		super(name);
	}

	public AnimKey() {
		super();
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
