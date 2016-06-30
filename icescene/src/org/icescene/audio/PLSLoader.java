package org.icescene.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.icescene.configuration.INIFile;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

public class PLSLoader implements AssetLoader {

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        INIFile ini = new INIFile();
        final InputStream in = assetInfo.openStream();
        try {
            ini.load(in);
            if (!Arrays.asList(ini.getAllSectionNames()).contains("playlist")) {
                throw new IOException("Not a playlist.");
            }
            return ini;
        } finally {
            in.close();
        }
    }
}