package org.icescene.configuration;

import com.google.gson.JsonObject;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;

import icemoon.iceloader.BaseConfiguration;

public class AbstractJSONConfiguration extends BaseConfiguration<JsonObject> {

	public AbstractJSONConfiguration(String scriptPath, AssetManager assetManager) {
		super(scriptPath, assetManager.loadAsset(new AssetKey<JsonObject>(scriptPath)));
	}

}
