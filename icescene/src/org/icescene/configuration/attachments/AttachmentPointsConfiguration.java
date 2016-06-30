package org.icescene.configuration.attachments;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Appearance;
import org.icelib.AttachmentPoint;
import org.icelib.Icelib;
import org.icescene.configuration.AbstractSquirrelConfiguration;
import org.icesquirrel.interpreter.SquirrelInterpretedScript;
import org.icesquirrel.runtime.SquirrelArray;
import org.icesquirrel.runtime.SquirrelTable;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

/**
 * Parses AttachmentPoints.txt, producing objects that may be queried for
 * attachment parameters.
 */
public class AttachmentPointsConfiguration extends AbstractSquirrelConfiguration {

	private final static Logger LOG = Logger.getLogger(AttachmentPointsConfiguration.class.getName());
	private static AttachmentPointsConfiguration instance;

	public static AttachmentPointsConfiguration get(AssetManager assetManager) {
		if (instance == null) {
			instance = new AttachmentPointsConfiguration(assetManager);
		}
		return instance;
	}

	public AttachmentPointSet getSet(Appearance appearance) {
		// Get the attachments config for this creature
		String key;
		switch (appearance.getName()) {
		case C2:
			key = "Biped." + Icelib.toEnglish(appearance.getGender());
			break;
		case N4:
			key = appearance.getBodyTemplate();
			break;
		case P1:
			return null;
		default:
			throw new UnsupportedOperationException();
		}
		return getSet(key);
	}

	private Map<String, AttachmentPointSet> sets = new HashMap<String, AttachmentPointSet>();

	public AttachmentPointsConfiguration(AssetManager assetManager) {
		super("Data/AttachmentPoints.txt", assetManager, new SquirrelInterpretedScript());
		SquirrelTable obj = (SquirrelTable) backingObject.getRootTable().get(getHeader());
		for (Map.Entry<Object, Object> en : obj.entrySet()) {
			SquirrelTable creatureObj = (SquirrelTable) en.getValue();
			final String name = (String) en.getKey();
			try {
				AttachmentPointSet set;
				// if (creatureObj.containsKey("*")) {
				// set = sets.get((String) creatureObj.get("*")).clone();
				// set.key = name;
				// } else {
				set = new AttachmentPointSet(name);
				// }

				for (Map.Entry<Object, Object> placeEn : creatureObj.entrySet()) {

					if (placeEn.getKey().equals("*")) {
						final String delegate = (String) creatureObj.get("*");
						set = sets.get(delegate).clone();
						set.key = name;
					} else {
						SquirrelTable placeObj = (SquirrelTable) placeEn.getValue();

						AttachmentPoint point = AttachmentPoint.valueOf(placeEn.getKey().toString().toUpperCase());
						AttachmentPlace place = new AttachmentPlace();

						place.setBone((String) placeObj.get("bone"));

						// Position
						if (placeObj.containsKey("position")) {
							place.setPosition(stringToVector3f((String) placeObj.get("position")));
						}

						// Orientation
						if (placeObj.containsKey("orientation")) {
							SquirrelTable orientationObj = (SquirrelTable) placeObj.get("orientation");
							Quaternion q = null;
							for (Map.Entry<Object, Object> orientationEn : orientationObj.entrySet()) {
								SquirrelArray oEls = (SquirrelArray) orientationEn.getValue();
								Quaternion a = new Quaternion();
								a.fromAngleAxis(Float.parseFloat(oEls.get(0).toString()) * FastMath.TWO_PI, stringToVector3f(oEls
										.get(1).toString()));
								if (q == null) {
									q = a;
								} else {
									q.multLocal(a);
								}
							}
							place.setOrientation(q);
						}

						// Hidden
						if (placeObj.containsKey("hidden")) {
							place.getHidden().clear();
							for (Object e : (SquirrelArray) placeObj.get("hidden")) {
								place.getHidden().add((String) e);
							}
						}

						// Scale
						if (placeObj.containsKey("scale")) {
							place.setScale(stringToVector3f((String) placeObj.get("scale")));
						}

						set.places.put(point, place);
					}

				}

				sets.put(name, set);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load attachable template.", e);
			}
		}
	}

	public AttachmentPointSet getSet(String key) {
		return sets.get(key);
	}

	@Override
	protected String getHeader() {
		return "AttachmentPoints";
	}

	@Override
	public void fill() {
		throw new UnsupportedOperationException();
	}
}
