package org.icescene.audio;

import icemoon.iceloader.locators.AbstractServerLocator;
import icemoon.iceloader.locators.AssetCacheLocator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;

public class ExternalAudioStreamLocator extends AbstractServerLocator {

    private static final Logger LOG = Logger.getLogger(ExternalAudioStreamLocator.class.getName());

    public ExternalAudioStreamLocator() {
        setUseCaching(false);
        setFireEvents(false);
    }

    public AssetInfo locate(AssetManager manager, @SuppressWarnings("rawtypes") AssetKey key) {
        final String name = key.getName();
        if (name.startsWith("http://") || name.startsWith("https://")) {
            try {
                // TODO - can't handle skipping encoding of port 
                int idx = name.indexOf('/', name.indexOf('/') + 1);
                if(idx != -1) {
                    int portIdx = name.indexOf(':', idx + 1);
                    if(portIdx != -1 && portIdx < name.length() && Character.isDigit(name.charAt(portIdx + 1))) {
                        int endIdx = name.indexOf('/', portIdx + 1);
                        if(endIdx != -1) {
                            idx = endIdx;
                        }
                    }
                }
                String[] parts = name.substring(idx + 1).split("/");
                StringBuilder encName = new StringBuilder(name.substring(0, idx));
                for (String part : parts) {
                    if (encName.length() > 0) {
                        encName.append("/");
                    }
                    encName.append(URLEncoder.encode(part, "UTF-8"));
                }
                URL url = new URL(encName.toString());
                LOG.info(String.format("Loading external URL %s", url));
                final AssetInfo ai = create(manager, key, url, -1, -1);     
                return ai;
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Error while locating " + name, ex);
                return null;
            }
        }
        return null;
    }

    public AssetInfo getCachedAssetInfo(AssetManager manager, @SuppressWarnings("rawtypes") AssetKey key) {
        // Content has not changed, return original cached content
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Content %s has not changed, using cached version", key.getName()));
        }
        return AssetCacheLocator.getCachedAssetInfo(key);
    }

}
