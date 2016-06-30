package org.icescene.configuration;

import icemoon.iceloader.AbstractConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.icescene.SceneConstants;

import com.jme3.asset.AssetManager;

public class TerrainClutterConfiguration extends AbstractConfiguration<INIFile> {

	private static TerrainClutterConfiguration instance;

	public static class ClutterDefinition {

		private String textureImageName;
		private float density;
		private Map<String, Float> meshScales = new LinkedHashMap<String, Float>();

		public ClutterDefinition(String textureImageName) {
			this.textureImageName = textureImageName;
		}

		public String getTextureImageName() {
			return textureImageName;
		}

		public void setDensity(float density) {
			this.density = density;
		}

		public float getDensity() {
			return density;
		}

		public Map<String, Float> getMeshScales() {
			return meshScales;
		}

		@Override
		public ClutterDefinition clone() {
			ClutterDefinition def = new ClutterDefinition(textureImageName);
			def.density = density;
			def.meshScales = new HashMap<String, Float>(meshScales);
			return def;
		}

		@Override
		public String toString() {
			return "ClutterDefinition{" + "textureImageName=" + textureImageName + ", density=" + density + ", meshScales="
					+ meshScales + '}';
		}
	}

	private Map<String, ClutterDefinition> clutterDefinitions = new LinkedHashMap<String, ClutterDefinition>();

	public static TerrainClutterConfiguration get(AssetManager assetManager) {
		if (instance == null) {
			instance = new TerrainClutterConfiguration(SceneConstants.TERRAIN_PATH + "/Terrain-Common/TerrainClutter.cfg",
					assetManager);
		}
		return instance;
	}

	public static void remove() {
		instance = null;
	}

	public TerrainClutterConfiguration(String resourceName, AssetManager classLoader) {
		super(resourceName, classLoader, new INIFile());
		INIFile file = getBackingObject();
		for (String section : file.getAllSectionNames()) {
			float density = 0.1f;
			ClutterDefinition def = new ClutterDefinition(section);
			for (String s : file.getPropertyNames(section)) {
				if (s.equals("Density")) {
					def.density = file.getDoubleProperty(section, "Density").floatValue();
				} else {
					def.meshScales.put(s, file.getDoubleProperty(section, s).floatValue());
				}
			}
			clutterDefinitions.put(section, def);
		}
	}

	public void putClutterDefinition(String textureImageName, ClutterDefinition def) {
		clutterDefinitions.put(textureImageName, def);
	}

	public ClutterDefinition getClutterDefinition(String textureImageName) {
		return clutterDefinitions.get(textureImageName);
	}

	public List<ClutterDefinition> getClutterDefintions() {
		return Collections.unmodifiableList(new ArrayList<ClutterDefinition>(clutterDefinitions.values()));
	}

	@Override
	protected void load(InputStream in, INIFile backingObject) throws IOException {
		backingObject.load(in);
	}

	public void write(OutputStream out) throws IOException {
		fill();
		getBackingObject().save(out);
	}

	protected void fill() {
		backingObject.clear();
		for (Map.Entry<String, ClutterDefinition> en : clutterDefinitions.entrySet()) {
			backingObject.addSection(en.getKey(), null);
			backingObject.setFloatProperty(en.getKey(), "Density", en.getValue().getDensity(), null);
			for (Map.Entry<String, Float> cen : en.getValue().getMeshScales().entrySet()) {
				backingObject.setFloatProperty(en.getKey(), cen.getKey(), cen.getValue(), null);
			}
		}
	}
}
