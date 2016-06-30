package org.icescene.entities;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.configuration.MeshConfiguration;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public abstract class AbstractEntity {

	private static final Logger LOG = Logger.getLogger(AbstractEntity.class.getName());

	public AbstractEntity() {
	}

	public abstract Spatial getSpatial();

	protected boolean loadToKey(AssetManager assetManager, MeshConfiguration meshConfig, MeshConfiguration.TextureDefinition def,
			Material material, String key) {
		if (material.getMaterialDef().getMaterialParam(key) != null) {
			final String texturePath = meshConfig.absolutize(def.getName());
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Loading texture %s", texturePath));
			}
			final Texture tex = assetManager.loadTexture(new TextureKey(texturePath, false));
			configureTexture(material, key, tex);
			return true;
		}
		return false;
	}

	protected void configureTexture(Material material, String key, final Texture tex) {
		tex.setWrap(Texture.WrapMode.Repeat);
		material.setTexture(key, tex);
	}

//	protected Material createMaterial(MeshConfiguration meshConfig, AssetManager assetManager) {
//		Material mat = MaterialFactory.getManager().getMaterial(meshConfig, assetManager);
//
//		// Diffuse
//		MeshConfiguration.TextureDefinition def = meshConfig.getTexture(MeshConfiguration.DIFFUSE);
//		if (def != null) {
//			// Add texture images
//			if (!loadToKey(assetManager, meshConfig, def, mat, "DiffuseMap")
//					&& !loadToKey(assetManager, meshConfig, def, mat, "ColorMap")) {
//				LOG.warning("No known texture keys in material.");
//			}
//		}
//
//		// Illumination
//		def = meshConfig.getTexture(MeshConfiguration.ILLUMINATED);
//		if (def != null) {
//			// Add texture images
//			if (!loadToKey(assetManager, meshConfig, def, mat, "ColorRamp")) {
//				LOG.warning("No known texture keys in material.");
//			}
//		}
//
//		return mat;
//	}
}
