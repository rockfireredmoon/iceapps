package org.icescene.props;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;
import org.icelib.SceneryItem;
import org.icescene.IcesceneApp;
import org.icescene.entities.EntityContext;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.scene.Node;

import icemoon.iceloader.ServerAssetManager;
import icetone.core.utils.Alarm;

/**
 * While most props will be {@link XMLProp} instances, sometimes we want special
 * behavior for particular props. For example, <strong>Light<//strong> props
 * have properties to allow colour radius etc to be set. <br/>
 * Instead of directly instantiating props, they should all be created through
 * this factory which will ensure special implementations are loaded as java
 * classes instead of raw XML. The java classes themselves may extend
 * {@link XMLProp} and so sceneryItem models may be used easily. <br/>
 * A reference to this factory may be obtained from {@link GameAppState}.
 */
public class EntityFactory implements EntityContext {

	private final static Logger LOG = Logger.getLogger(EntityFactory.class.getName());
	protected final SimpleApplication app;
	protected List<String> propPackages = new ArrayList<String>();
	private LinkedHashMap<String, String> propTypePatterns;
	private Node lightingParent;

	public EntityFactory(SimpleApplication app, Node lightingParent) {
		this.app = app;
		this.lightingParent = lightingParent;
		propPackages.add(AbstractProp.class.getPackage().getName());

		AssetInfo locateAsset = app.getAssetManager().locateAsset(new AssetKey<String>("Data/PropTypes.txt"));
		if(locateAsset == null)
			throw new RuntimeException("Could not locate Data/PropTypes.txt");
		InputStream in = locateAsset.openStream();
		propTypePatterns = new LinkedHashMap<String, String>();
		try {
			try {
				for (String s : IOUtils.readLines(in)) {
					s = s.trim();
					if (!s.startsWith("#")) {
						int idx = s.indexOf("=");
						if(idx == -1) {
							throw new IOException(String.format("Unexpected line in prop type data file '%s'.", s));
						}
						propTypePatterns.put(s.substring(0, idx), s.substring(idx + 1));
					}
				}
			} finally {
				in.close();
			}
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to load prop type map.", ioe);
		}
	}

	public <T extends AbstractProp> T getProp(String name) {
		SceneryItem sceneryItem = new SceneryItem();
		sceneryItem.setAsset(name);
		return getProp(sceneryItem);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractProp> T getProp(SceneryItem sceneryItem) {

		String name = sceneryItem.getAssetName();
		if (name.endsWith(".csm.xml")) {
			throw new IllegalArgumentException("Prop names should not end with .csm.xml");
		}

		String propPath = name.replace("#", "/");
		String propName = propPath;
		String propDir = null;
		String propPackage = null;
		int idx = propPath.indexOf('/');
		if (idx != -1) {
			propDir = propPath.substring(0, idx);
			propPackage = propDir.replace("/", ".").replace("-", "").toLowerCase();
			propName = propPath.substring(idx + 1);
		}

		// Determine the class to use to actually handle the prop
		String className = null;
		for (Object k : propTypePatterns.keySet()) {
			if (name.matches((String) k)) {
				try {
					String matchClassName = propTypePatterns.get((String) k);
					if (matchClassName.equals("_")) {
						className = (propPackage == null ? "" : propPackage + ".") + propName.replace("-", "").replace("_", "");
					} else {
						className = matchClassName;
					}
					break;
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Failed to load prop class.", e);
				}
			}
		}

		// Must have a mapping
		if (className == null) {
			throw new AssetNotFoundException("Failed to find pattern that matched " + propName + " in prop type mappings.");
		}

		// Search all the registered packages for the prop class
		Class<? extends AbstractProp> propClazz = null;
		for (String packageParent : propPackages) {
			String fullClassName = packageParent + "." + className;
			try {
				propClazz = (Class<? extends AbstractProp>) Class.forName(fullClassName, true, getClass().getClassLoader());
				break;
			} catch (Exception e) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.log(Level.FINE, "Failed to load prop class.", e);
				}
			}
		}

		// Must have found the prop class to use
		if (propClazz == null) {
			throw new AssetNotFoundException("Failed to find any prop class " + className + " in any prop package.");
		}

		// Finally load the prop
		try {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Loading prop %s using class %s", name, propClazz.getName()));
			}
			T prop = (T) propClazz.getConstructor(String.class, EntityContext.class).newInstance(name, this);
			prop.setLightingParent(lightingParent);
			prop.setSceneryItem(sceneryItem);
			prop.configureProp();
			return (T) prop;
		} catch (Exception ex1) {
			throw new RuntimeException(ex1);
		}
	}

	// public AbstractProp getProp(String name) {
	// HashMap<String, String> variables = new HashMap<String, String>();
	// int idx = name.indexOf('?');
	// if (idx != -1) {
	// String[] vars = name.substring(idx + 1).split("\\&+");
	// name = name.substring(0, idx);
	// for (String v : vars) {
	// idx = v.indexOf('=');
	// String key = v;
	// String val = null;
	// if (idx != -1) {
	// key = v.substring(0, idx);
	// val = v.substring(idx + 1);
	// }
	// variables.put(key, val);
	// }
	// }
	// return getProp(name, variables);
	// }

	public Set<String> getPropResourcesForType(final String type) {
		return getPropResources(String.format("^Prop/Prop-%s/.*\\.csm\\.xml$", type));
	}

	public Set<String> getPropResources(final String pattern) {
		return ((ServerAssetManager) app.getAssetManager()).getAssetNamesMatching(pattern);
	}

	public Set<String> getAllPropResources() {
		return ((ServerAssetManager) app.getAssetManager()).getAssetNamesMatching(".*\\.csm\\.xml$");
	}

	public AbstractProp getPropForResourcePath(String propName) throws IOException {
		if (!propName.endsWith(".csm.xml")) {
			throw new IOException("Path must end with .csm.xml");
		}
		String[] els = propName.substring(0, propName.length() - 8).split("/");
		if (els.length < 2) {
			throw new IOException("Expect at least folder and path.");
		}
		return getProp(els[els.length - 2] + "#" + els[els.length - 1]);
	}

	@Override
	public AssetManager getAssetManager() {
		return app.getAssetManager();
	}

	@Override
	public Alarm getAlarm() {
		return ((IcesceneApp) app).getAlarm();
	}

	@Override
	public AppStateManager getStateManager() {
		return app.getStateManager();
	}

	@Override
	public Preferences getPreferences() {
		return ((IcesceneApp) app).getPreferences();
	}

	@Override
	public IcesceneApp getApp() {
		return (IcesceneApp) app;
	}

	@Override
	public ComponentManager getComponentManager() {
		return ((IcesceneApp) app).getComponentManager();
	}
}
