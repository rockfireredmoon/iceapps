package org.icescene.props;

import icemoon.iceloader.ServerAssetManager;

import java.util.Set;

import org.icelib.SceneryItem;
import org.icelib.SceneryItem.Type;
import org.icescene.assets.ComponentDefinition;
import org.icescene.assets.ComponentKey;

import com.jme3.app.Application;
import com.jme3.asset.AssetNotFoundException;

public class ComponentManager {

	private Application app;

	public ComponentManager(Application app) {
		this.app = app;
	}

	public Component get(SceneryItem sceneryItem) {
		ComponentDefinition def = app.getAssetManager().loadAsset(new ComponentKey(resolve(sceneryItem)));
		return new Component(def, sceneryItem.getVariables());
	}

	protected String resolve(SceneryItem resource) {
		/* The path that is provide in the scenery asset doesn't (always) contain the 
		 * folder. This needs investigating, as it means we need to use an index which
		 * is not really desirable with something loaded as often as props */

		// String resourcePath = String.format("%s/%s.csm.xml",
		// resource.getType().assetPath(),
		// resource.getAssetName().replace("#", "/"));
		// return resourcePath;

		String resourcePath = resource.getAssetName();
		int idx = resourcePath.indexOf('#');
		if (idx != -1) {
			resourcePath = resourcePath.substring(idx + 1);
		}
		Type type = resource.getType();
		if (!type.hasSubDir()) {
			return String.format("%s/%s.csm.xml", type.assetPath(), resourcePath);
		}
		String pattern = type.hasSubDir() ? String.format("%s/.*/%s.csm.xml", type.assetPath(), resourcePath) : String.format(
				"%s/%s.csm.xml", type.assetPath(), resourcePath);
		Set<String> items = ((ServerAssetManager) app.getAssetManager()).getAssetNamesMatching(pattern);
		if (items.isEmpty()) {
			throw new AssetNotFoundException("Could not find " + resourcePath);
		}
		return items.iterator().next();
	}
}
