package org.icescene.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Appearance.Race;
import org.icelib.Icelib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jme3.asset.AssetManager;

public class NameSuggestions extends AbstractJSONConfiguration {

	private final static Logger LOG = Logger.getLogger(NameSuggestions.class.getName());
	private static NameSuggestions instance;

	public static NameSuggestions get(AssetManager assetManager) {
		if (instance == null) {
			instance = new NameSuggestions(assetManager);
		}
		return instance;
	}

	public NameSuggestions(AssetManager assetManager) {
		super("EarthEternal/NameSuggestions.json", assetManager);
	}

	public List<String> getRandomNames(Race race, int count) {
		List<String> l = getNames(race);
		List<String> n = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			n.add(l.get((int) (Math.random() * l.size())));
		}
		return n;
	}

	public List<String> getNames(Race race) {
		List<String> l = getList("General");
		if (race != null) {
			String rk = Icelib.toEnglish(race);
			if (backingObject.has(rk)) {
				l.addAll(getList(rk));
			} else {
				LOG.log(Level.WARNING, String.format("No race specific name suggestions for %s", rk));
			}
		}
		return l;
	}

	protected List<String> getList(String k) {
		List<String> l = new ArrayList<String>();
		JsonArray asJsonArray = backingObject.getAsJsonArray(k);
		for (JsonElement e : asJsonArray) {
			if(!e.isJsonNull())
				l.add(e.getAsString());
		}
		return l;
	}

}
