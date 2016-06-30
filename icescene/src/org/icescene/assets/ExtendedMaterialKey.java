package org.icescene.assets;

import java.util.HashMap;
import java.util.Map;

import com.jme3.scene.plugins.ogre.matext.OgreMaterialKey;

public class ExtendedMaterialKey extends OgreMaterialKey {


	public class LODData {
		private int level;
		private boolean disabled;

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
		
		public int getLevel() {
			return level;
		}
	}
	
	private Map<Integer, LODData> lod = new HashMap<>();
	private boolean lit;
	private ExtendedMaterialListKey listKey;


	public ExtendedMaterialKey(String name) {
		super(name);
	}

	public boolean isLit() {
		return lit;
	}

	public ExtendedMaterialListKey getListKey() {
		return listKey;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	public LODData getLOD(int level) {
		LODData d = lod.get(level);
		if(d == null) {
			d= new LODData();
			d.level = level;
			lod.put(level, d);
		}
		return d;
	}

	public void setListKey(ExtendedMaterialListKey listKey) {
		this.listKey = listKey;		
	}
}
