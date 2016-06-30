package org.icescene.audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

public class M3ULoader implements AssetLoader {

    public class M3UPlaylist extends ArrayList<M3UInfo> {
    }
    
    public class M3UInfo {

        private URL location;
        private String title;
        private int length = -1;

        public URL getLocation() {
            return location;
        }

        public String getTitle() {
            return title;
        }

        public int getLength() {
            return length;
        }
    }

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(assetInfo.openStream()));
        M3UPlaylist l = new M3UPlaylist();
        try {
            String line;
            M3UInfo info = new M3UInfo();
            while ((line = r.readLine()) != null) {
                final String trim = line.trim();
                System.out.println("trim: " + trim);
                if (trim.startsWith("#")) {
                    if (trim.startsWith("#EXTM3U")) {
                        // Is extended
                    } else if (trim.startsWith("#EXTINF:")) {
                        String s = trim.substring(8);
                        int idx = s.indexOf(':');
                        if (idx != -1) {
                            try {
                                info.length = Integer.parseInt(s.substring(0, idx));
                            } catch (NumberFormatException nfe) {
                            }
                            info.title = s.substring(idx + 1);
                        }
                    }
                } else {
                    info.location = new URL(trim);
                    l.add(info);
                    info = new M3UInfo();
                }
            }
            return l;
        } finally {
            r.close();
        }
    }
}