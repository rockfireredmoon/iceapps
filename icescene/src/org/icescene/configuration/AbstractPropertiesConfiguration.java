package org.icescene.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import icemoon.iceloader.AbstractConfiguration;
import icetone.extras.util.ExtrasUtil;

public abstract class AbstractPropertiesConfiguration<T extends AbstractPropertiesConfiguration> extends AbstractConfiguration<LinkedHashMap<String, String>> {

    private final static Logger LOG = Logger.getLogger(AbstractPropertiesConfiguration.class.getName());
    protected final T base;
    private LinkedHashMap<String, String> copy;
    /**
     * Load template default configuration from the current context classloader (falling
     * back to the loader that loaded this class).
     *
     * @param resourceName
     */
    public AbstractPropertiesConfiguration(AssetManager assetManager, String resourceName) {
        this(assetManager, resourceName, (T) null);
    }

    /**
     * Load template default from the current context classloader (falling back to the
     * loader that loaded this class).
     *
     * @param resourceName
     */
    public AbstractPropertiesConfiguration(AssetManager assetManager, String resourceName, T base) {
        super(resourceName, assetManager, new LinkedHashMap<String, String>());
        this.base = base;
        loadBase(base);
    }

    public void write(OutputStream out, boolean partial) throws IOException {
        fill(partial);
        PrintWriter pw = new PrintWriter(out, true);
        LinkedHashMap<String, String> baseMap = (LinkedHashMap<String, String>) (base == null ? null : base.getBackingObject());
        for (Map.Entry<String, String> en : getBackingObject().entrySet()) {
            if(baseMap == null || !en.getValue().equals(baseMap.get(en.getKey()))) {
                LOG.info(String.format("Writing %s=%s", en.getKey(), en.getValue()));
                pw.println(String.format("%s=%s", en.getKey(), en.getValue()));
            }
        }
    }
    
    protected abstract void fill(boolean partial);

    protected void load(InputStream in, LinkedHashMap<String, String> p) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                int idx = line.indexOf('=');
                String val = idx == -1 ? null : line.substring(idx + 1);
                String name = idx == -1 ? line : line.substring(0, idx);
                p.put(name, val);
            }
        }
        
        copy = new LinkedHashMap<String, String>();
        copy.putAll(p);
    }
    
    public void reset() {
        getBackingObject().clear();
        loadBase(base);
        getBackingObject().putAll(copy);
        load();
    }
    
    protected abstract void load();

    protected final String get(String key) {
        return get(key, null);
    }

    protected final String get(String key, String defaultValue) {
        String val = getBackingObject().get(key);
        return val == null ? defaultValue : val;
    }

    protected final int getInt(String key) {
    	return getInt(key, 0);
    }

    protected final int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected final long getLong(String key) {
        return getLong(key, 0l);
    }

    protected final long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(get(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected final boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    protected final boolean getBoolean(String key, boolean defaultValue) {
        return isTrue(get(key, String.valueOf(defaultValue)));
    }

    boolean isTrue(String val) {
        return val.equalsIgnoreCase("yes") || val.equalsIgnoreCase("true");
    }

    protected final float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    protected final float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(get(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return 0f;
        }
    }

    protected void put(String key, boolean val) {
        getBackingObject().put(key, String.valueOf(val));
    }

    protected void put(String key, String val) {
        if (val == null) {
            getBackingObject().remove(key);
        } else {
            getBackingObject().put(key, val);
        }
    }

    protected void put(String key, int interval) {
        getBackingObject().put(key, String.valueOf(interval));
    }

    protected void put(String key, float val) {
        getBackingObject().put(key, String.valueOf(val));
    }

    protected void put(String key, Vector3f val) {
        if (val == null) {
            getBackingObject().remove(key);
        } else {
            getBackingObject().put(key, val.x + "," + val.y + "," + val.z);
        }
    }

    protected void put(String key, ColorRGBA val) {
        if (val == null) {
            getBackingObject().remove(key);
        } else {
            getBackingObject().put(key, ExtrasUtil.toHexString(val));
        }
    }

    protected final ColorRGBA getColor(String key) {
        return getColor(key, null);
    }
    protected final Vector3f getVector3f(String key, Vector3f defaultValue) {
        try {
            String col = get(key);
            if (col != null) {
                String[] args = col.split(",");
                return new Vector3f(Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]));
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final ColorRGBA getColor(String key, ColorRGBA defaultValue) {
        try {
            String col = get(key);
            if (col != null) {
                return ExtrasUtil.fromColorString(col);
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadBase(T base) {
        if (base != null) {
            LinkedHashMap<String, String> thisProperties = getBackingObject();
            LinkedHashMap<String, String> tmpProperties = new LinkedHashMap<String, String>();
            LinkedHashMap<String, String> baseProperties = (LinkedHashMap<String, String>) base.getBackingObject();
            tmpProperties.putAll(baseProperties);
            tmpProperties.putAll(thisProperties);


            getBackingObject().clear();
            getBackingObject().putAll(tmpProperties);
        }
    }
}
