package org.icescene.assets;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;

public class ExtendedMaterial extends Material {

	private boolean lit;

	public ExtendedMaterial() {
		super();
	}

	public ExtendedMaterial(AssetManager contentMan, String defName) {
		super(contentMan, defName);
	}

	public ExtendedMaterial(MaterialDef def) {
		super(def);
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	public boolean isLit() {
		return lit;
	}

}
