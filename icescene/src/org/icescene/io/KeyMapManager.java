package org.icescene.io;

import icemoon.iceloader.ServerAssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.icescripting.Scripts;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.Trigger;

/**
 * Manages the mappings between actual key presses and mapping names. Default
 * key maps are read from the asset path and any local preferences applied. If a
 * mapping preference changes, then the input managers key map is reconfigured
 * immediately.
 *
 */
public class KeyMapManager implements PreferenceChangeListener {

	/*
	 * Resume loading
	 */final static Logger LOG = Logger.getLogger(KeyMapManager.class.getName());

	// private Map<String, Mapping> maps = new HashMap<String, Mapping>();
	private Map<String, KeyMapping> maps = null;
	private InputManager inputManager;
	private Map<String, List<ActionListener>> listeners = new HashMap<>();
	private Preferences node;

	public KeyMapManager(String appName, Preferences node, ServerAssetManager assetManager, InputManager inputManager)
			throws IOException {
		this.inputManager = inputManager;
		this.node = node;

		// Load all of the default mappings from the asset index
		for (String n : Scripts.get().locateScript("Keymaps/.*\\.default")) {
			Scripts.get().eval(n);
		}

		try {
			Scripts.get().eval(String.format("Keymaps/%s.js", appName));
		}
		catch(AssetNotFoundException anfe) {
			LOG.info("No application specific key maps");
		}

		// Load all of the user mappings from the asset index
		for (String n : Scripts.get().locateScript("Keymaps/.*\\.user")) {
			Scripts.get().eval(n);
		}

		maps = (Map<String, KeyMapping>) Scripts.get().getBindings().get("__KeyMaps");
		if (maps.isEmpty()) {
			try {
				Scripts.get().eval("Keymaps/scene.default.js");
			}
			catch(AssetNotFoundException anfe) {
				throw new RuntimeException("Found no keymaps at all.");
			}
		}

		// Watch for changes to local configured mappings
		node.addPreferenceChangeListener(this);
	}

	public void addMapping(String mapping, Trigger... triggers) {
		UserKeyMapping mappingObj = getMapping(mapping);
		LOG.info(String.format("Adding mapping %s to %s", mapping, mappingObj.getSource().toString()));
		List<Trigger> t = new ArrayList<>();
		t.add(mappingObj.getSource().getTrigger());
		t.addAll(Arrays.asList(triggers));
		inputManager.addMapping(mapping, t.toArray(new Trigger[0]));
	}

	public void deleteMapping(String mapping) {
		inputManager.deleteMapping(mapping);
	}

	public List<UserKeyMapping> getMappings(String category) {
		List<UserKeyMapping> mappings = new ArrayList<>();
		for (KeyMapping n : maps.values()) {
			UserKeyMapping m = createMapping(n);
			if (category.equals(m.getSource().getCategory())) {
				mappings.add(m);
			}
		}
		return mappings;
	}

	public Set<String> getCategories() {
		Set<String> cats = new HashSet<>();
		for (KeyMapping n : maps.values()) {
			cats.add(n.getCategory());
		}
		return cats;
	}

	public UserKeyMapping getMapping(String name) {
		KeyMapping source = maps.get(name);
		if (source == null) {
			throw new IllegalArgumentException(String.format("No key mapping '%s'.", name));
		}
		return createMapping(source);
	}

	private UserKeyMapping createMapping(KeyMapping source) {
		return new UserKeyMapping(source, node);
	}

	public void resetMapping(String mapping) {
		UserKeyMapping mappingObj = getMapping(mapping);
		mappingObj.reset();
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		try {
			UserKeyMapping mappingObj = getMapping(evt.getKey());
			if (inputManager.hasMapping(mappingObj.getSource().getMapping())) {
				deleteMapping(mappingObj.getSource().getMapping());
				mappingObj.parseTrigger(evt.getNewValue());
				addMapping(mappingObj.getSource().getMapping());

				// Need to add listeners back
				List<ActionListener> listenerList = listeners.get(mappingObj.getSource().getMapping());
				if (listenerList != null) {
					for (ActionListener l : listenerList) {
						inputManager.addListener(l, mappingObj.getSource().getMapping());
					}
				}

				LOG.info(String.format("Re-mapped key %s", mappingObj));
			}
		} catch (IllegalArgumentException iae) {
			LOG.warning("Preference changed for unknown key mapping '" + evt.getKey() + "'.");
		}

	}

	public void removeListener(ActionListener actionListener) {
		for (String mapping : new ArrayList<String>(listeners.keySet())) {
			List<ActionListener> l = listeners.get(mapping);
			if (l != null) {
				l.remove(actionListener);
				if (l.isEmpty()) {
					listeners.remove(l);
				}
			}
		}
		inputManager.removeListener(actionListener);
	}

	public void addListener(ActionListener actionListener, String... mappings) {
		for (String mapping : mappings) {
			List<ActionListener> l = listeners.get(mapping);
			if (l == null) {
				l = new ArrayList<>();
				listeners.put(mapping, l);
			}
			if (l.contains(actionListener)) {
				throw new IllegalArgumentException("Already contains same action listener for " + mapping);
			}
			l.add(actionListener);
		}
		inputManager.addListener(actionListener, mappings);
	}

	public boolean isMapped(String name, String expectedMapping) {
		return name.equals(expectedMapping) && getMapping(name).getModifiers() == ModifierKeysAppState.get().getMask();
	}

	public boolean hasMapping(String mappingName) {
		return inputManager.hasMapping(mappingName);
	}

}
