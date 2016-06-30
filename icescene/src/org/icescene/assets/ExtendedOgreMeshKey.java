package org.icescene.assets;

import com.jme3.material.MaterialList;
import com.jme3.scene.plugins.ogre.OgreMeshKey;

public class ExtendedOgreMeshKey extends OgreMeshKey {

	public ExtendedOgreMeshKey() {
	}

	public ExtendedOgreMeshKey(String name) {
		super(name);
	}

	public ExtendedOgreMeshKey(String name, MaterialList materialList) {
		super(name, materialList);
	}

	public ExtendedOgreMeshKey(String name, String materialName) {
		super(name, materialName);
	}

	protected void configureMaterialKey(ExtendedMaterialListKey key) {
	}

}
