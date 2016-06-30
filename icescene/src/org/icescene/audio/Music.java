package org.icescene.audio;

import icemoon.iceloader.ServerAssetManager;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;

public class Music {

    private static final Logger LOG = Logger.getLogger(Music.class.getName());
    private static Collection<String> musicResources;
    private static Music instance;

    public static Music get(AssetManager assetManager) {
        if (instance == null) {
            instance = new Music(assetManager);
        }
        return instance;
    }

    private Music(AssetManager assetManager) {
        if (assetManager instanceof ServerAssetManager) {
            musicResources = ((ServerAssetManager) assetManager).getAssetNamesMatching("Music/.*/.*\\.ogg");
        } else {
            LOG.warning(String.format("Not using a %s, cannot get indexes of available music.", ServerAssetManager.class));
            musicResources = Collections.emptyList();
        }
    }

    public Collection<String> getMusicResources() {
        return musicResources;
    }
}
