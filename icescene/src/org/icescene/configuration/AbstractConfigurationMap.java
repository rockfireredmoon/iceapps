package org.icescene.configuration;

import icemoon.iceloader.ServerAssetManager;

import java.util.Map;

import org.icelib.beans.MappedMap;
import org.icescripting.Scripts;

import com.jme3.asset.AssetManager;

public abstract class AbstractConfigurationMap<K, V> extends MappedMap<K, V> {
	private static final long serialVersionUID = 1L;
	
	private boolean allLoaded;

	public AbstractConfigurationMap(Class<K> keyClass, Class<? extends V> valueClass) {
		super(keyClass, valueClass);
	}

	public AbstractConfigurationMap(Map<K, V> backingMap, Class<K> keyClass, Class<? extends V> valueClass) {
		super(backingMap, keyClass, valueClass);
	}

	
	public void loadAll(AssetManager assets) {
		if(!allLoaded) {
			for(String s : ((ServerAssetManager)assets).getAssetNamesMatching(allAssetsPattern())) {
				if (!Scripts.get().isLoaded(s)) {
					Scripts.get().eval(s);
				}
			}
			allLoaded = true;
		}
	}

	protected abstract String allAssetsPattern();
}
