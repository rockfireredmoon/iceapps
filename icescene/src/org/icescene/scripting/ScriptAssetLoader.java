package org.icescene.scripting;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Logger;

import org.icescripting.ScriptLoader;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;

import icemoon.iceloader.ServerAssetManager;

public class ScriptAssetLoader implements ScriptLoader {
	final static Logger LOG = Logger.getLogger(ScriptAssetLoader.class.getName());

	private ServerAssetManager assetManager;

	public ScriptAssetLoader(ServerAssetManager assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	public boolean exists(String path) {
		return assetManager.hasAsset(path);
	}

	@Override
	public InputStream load(String path) throws FileNotFoundException {
		AssetInfo locateAsset = assetManager.locateAsset(new AssetKey<String>(path));
		if (locateAsset == null) {
			throw new FileNotFoundException(String.format("Could not find script asset %s", path));
		}
		return locateAsset.openStream();
	}

	@Override
	public Set<String> locate(String pattern) {
		LOG.info(String.format("Locating scripts with pattern %s", pattern));
		return assetManager.getAssetNamesMatching(pattern);
	}

}
