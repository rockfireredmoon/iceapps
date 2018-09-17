package org.icescene.configuration;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import icemoon.iceloader.BaseConfiguration;

/**
 * Parses Squirrel script data files.
 */
public abstract class AbstractScriptedConfiguration extends BaseConfiguration<Map<Object, Object>> {

	protected AssetManager assetManager;

	protected Vector3f stringToVector3f(final String str) throws NumberFormatException {
		StringTokenizer t = new StringTokenizer(str, ", \t");
		final Vector3f vector3f = new Vector3f(Float.parseFloat(t.nextToken()), Float.parseFloat(t.nextToken()), Float.parseFloat(t
				.nextToken()));
		return vector3f;
	}

	public AbstractScriptedConfiguration(String scriptPath, Map<Object, Object> backingObject, AssetManager assetManager) {
		super(scriptPath, backingObject);
		this.assetManager = assetManager;
	}

	public List<String> getStrings(String key, List<String> defaultValue) {
		return backingObject.containsKey(key) ? (List<String>) backingObject.get(key) : defaultValue;
	}

	public String get(String key, String defaultValue) {
		return backingObject.containsKey(key) ? (String) backingObject.get(key) : defaultValue;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return backingObject.containsKey(key) ? (Boolean) backingObject.get(key) : defaultValue;
	}

	public ColorRGBA getColour(String key, ColorRGBA defaultValue) {
		return backingObject.containsKey(key) ? (ColorRGBA) backingObject.get(key) : defaultValue;
	}

	public Vector3f getVector3f(String key, Vector3f defaultValue) {
		return backingObject.containsKey(key) ? (Vector3f) backingObject.get(key) : defaultValue;
	}

	public float getFloat(String key, float defaultValue) {
		return backingObject.containsKey(key) ? ((Number) backingObject.get(key)).floatValue() : defaultValue;
	}

	public int getInt(String key, int defaultValue) {
		return backingObject.containsKey(key) ? ((Number) backingObject.get(key)).intValue() : defaultValue;
	}
}
