package org.icescene.assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.jme3.asset.AssetKey;
import com.jme3.asset.CloneableSmartAsset;

public class ComponentDefinition implements CloneableSmartAsset {


	private List<Properties> xrefs = new ArrayList<>();
	private List<Properties> entities = new ArrayList<>();
	private List<Properties> lights = new ArrayList<>();
	private List<Properties> sounds = new ArrayList<>();
	private List<Properties> particleSystems = new ArrayList<>();
	private String componentId;
	private AssetKey<?> key;

	@Override
	public ComponentDefinition clone() {
		ComponentDefinition d;
		try {
			d = (ComponentDefinition) (super.clone());
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return d;
	}

	public String getComponentId() {
		return componentId;
	}

	public List<Properties> getXrefs() {
		return xrefs;
	}

	public List<Properties> getParticleSystems() {
		return particleSystems;
	}

	public List<Properties> getEntities() {
		return entities;
	}

	public List<Properties> getLights() {
		return lights;
	}

	public List<Properties> getSounds() {
		return sounds;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	@Override
	public void setKey(AssetKey key) {
		this.key = key;
	}

	@Override
	public AssetKey<?> getKey() {
		return key;
	}

}
