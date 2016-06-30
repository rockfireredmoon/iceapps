package org.icescene.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.asset.cache.AssetCache;
import com.jme3.material.Material;
import com.jme3.scene.plugins.ogre.matext.OgreMaterialKey;

public class ExtendedMaterialListKey extends OgreMaterialKey {
	private static final Logger LOG = Logger.getLogger(ExtendedMaterialListKey.class.getName());
	private static final String DEFAULT_LIT_MATERIAL_DEF = "MatDefs/Standard_Lit.j3md";
	private static final String DEFAULT_UNLIT_MATERIAL_DEF = "MatDefs/Standard_Unlit.j3md";

	public enum Lighting {
		LIT, UNLIT, DEFAULT
	}

	private String litMaterialDef;
	private String unlitMaterialDef;
	private Lighting lighting = Lighting.DEFAULT;
	private boolean cache;

	public ExtendedMaterialListKey(String path) {
		// Use defaults decided by loader
		this(path, null, null);
	}

	public ExtendedMaterialListKey(String path, String litMaterialDef, String unlitMaterialDef) {
		super(path);
		this.litMaterialDef = litMaterialDef;
		this.unlitMaterialDef = unlitMaterialDef;
	}

	public ExtendedMaterialListKey setTextureAliases(Map<ExtendedMaterialKey, Map<String, String>> textureAliases) {
		this.textureAliases = textureAliases;
		return this;
	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public Map<ExtendedMaterialKey, Map<String, String>> getTextureAliases() {
		return textureAliases;
	}

	public void setLitMaterialDef(String litMaterialDef) {
		this.litMaterialDef = litMaterialDef;
	}

	public void setUnlitMaterialDef(String unlitMaterialDef) {
		this.unlitMaterialDef = unlitMaterialDef;
	}

	protected Material createMaterial(AssetManager assetManager, boolean lit) {
		if (lighting != Lighting.DEFAULT) {
			lit = lighting == Lighting.LIT;
		}
		String path = lit ? (litMaterialDef == null ? DEFAULT_LIT_MATERIAL_DEF : litMaterialDef)
				: (unlitMaterialDef == null ? DEFAULT_UNLIT_MATERIAL_DEF : unlitMaterialDef);
		if (LOG.isLoggable(Level.FINE))
			LOG.fine(String.format("Creating material using %s for %s", path, lit ? "LIT" : "UNLIT"));
		ExtendedMaterial mat = new ExtendedMaterial(assetManager, path);
		mat.setLit(lit);
		return mat;

		// private static final String DEFAULT_LIT_MATERIAL_DEF =
		// "MatDefs/Standard_Lit.j3md";
		// private static final String DEFAULT_UNLIT_MATERIAL_DEF =
		// "MatDefs/Standard_Unlit.j3md";

		//
		// // Determine material def to use
		// litMaterialDef = null;
		// lighting = MaterialKey.Lighting.DEFAULT;
		// if (key instanceof MaterialKey) {
		// litMaterialDef = ((MaterialKey) key).getLitMaterialDef();
		// unlitMaterialDef = ((MaterialKey) key).getUnlitMaterialDef();
		// lighting = ((MaterialKey) key).getLighting();
		// }
		// if (litMaterialDef == null) {
		// litMaterialDef = DEFAULT_LIT_MATERIAL_DEF;
		// }
		// if (unlitMaterialDef == null) {
		// unlitMaterialDef = DEFAULT_UNLIT_MATERIAL_DEF;
		// }
		//
		// Icelib.removeMe("Compiling %s (%s, %s)", key, unlitMaterialDef,
		// litMaterialDef);
		// if(key.toString().indexOf("TerrainWater") != -1) {
		// Icelib.removeMe("XXX");
		// }
		//
		// ExtendedMaterial mat;
		// if (noLight) {
		// mat = new ExtendedMaterial(assetManager, unlitMaterialDef);
		// } else {
		// mat = new ExtendedMaterial(assetManager, litMaterialDef);
		// }
		//
		//
		//
		// ExtendedMaterial mat;
		// if (noLight) {
		// mat = new ExtendedMaterial(assetManager, unlitMaterialDef);
		// } else {
		// mat = new ExtendedMaterial(assetManager, litMaterialDef);
		// }
		// mat.setDisabled(disabled);
	}

	public Lighting getLighting() {
		return lighting;
	}

	public void setLighting(Lighting lighting) {
		this.lighting = lighting;
	}

	public Class<? extends AssetCache> getCacheType() {
		return cache ? super.getCacheType() : null;
	}

	public String getLitMaterialDef() {
		return litMaterialDef;
	}

	public String getUnlitMaterialDef() {
		return unlitMaterialDef;
	}
	private Map<ExtendedMaterialKey, Map<String, String>> textureAliases;

	public void addAlias(ExtendedMaterialKey matKey, String alias, String matParamName) {
		if (textureAliases == null) {
			textureAliases = new HashMap<ExtendedMaterialKey, Map<String, String>>();
		}
		Map<String, String> map = textureAliases.get(matKey);
		if (map == null) {
			map = new HashMap<String, String>();
			textureAliases.put(matKey, map);
		}
		if (LOG.isLoggable(Level.FINE))
			LOG.fine(String.format("Aliasing %s to %s on %s", alias, matParamName, matKey));
		map.put(alias, matParamName);
	}

}
