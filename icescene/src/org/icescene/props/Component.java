package org.icescene.props;

import icemoon.iceloader.BaseConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.icelib.Color;
import org.icescene.assets.ComponentDefinition;
import org.iceui.IceUI;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class Component extends BaseConfiguration<ComponentDefinition> {

	private final static Logger LOG = Logger.getLogger(Component.class.getName());

	private final List<Entity> entities = new ArrayList<Entity>();
	private final List<XRef> xrefs = new ArrayList<>();
	private final List<Light> lights = new ArrayList<>();
	private final List<SoundItem> sounds = new ArrayList<>();
	private final List<ParticleComponent> particles = new ArrayList<>();
	private final Map<String, String> variables;

	public Component(ComponentDefinition def, Map<String, String> variables) {
		super(def.getKey().getName(), def);

		this.variables = variables;

		for (Properties atts : def.getEntities()) {

			String qf = atts.getProperty("qf");
			String vf = atts.getProperty("vf");
			String p = atts.getProperty("p"); // position
			String q = atts.getProperty("q"); // rotation (Quaternion)
			boolean lightExclude = "1".equals(atts.getProperty("lightExclude"));

			String mesh = replaceVariables(atts.getProperty("mesh"), null);

			final String meshClassName = atts.getProperty("meshClass");
			Class<Spatial> meshClass = null;
			try {
				meshClass = (Class<Spatial>) (meshClassName == null ? null : Class.forName(meshClassName, true, getClass()
						.getClassLoader()));
			} catch (ClassNotFoundException ex) {
				LOG.warning(String.format("Failed to load mesh class %s", meshClassName));
			}

			final Entity entity = new Entity(mesh, qf == null ? 0 : Integer.parseInt(qf), vf == null ? 0 : Integer.parseInt(vf));
			entity.setMeshClass(meshClass);
			entity.setUnlit(lightExclude);

			// Location
			entity.setLocation(parseVector3f(p, entity.getLocation()));
			entity.setRotation(parseRotation(q, entity.getRotation()));
			entities.add(entity);
		}

		for (Properties atts : def.getSounds()) {
			String name = atts.getProperty("name");
			String sound = replaceVariables(atts.getProperty("sound"), "Test.ogg");
			float gain = Float.parseFloat(replaceVariables(atts.getProperty("gain"), "1"));
			sounds.add(new SoundItem(name == null ? sound : sound, sound, gain));
		}

		for (Properties atts : def.getParticleSystems()) {
			String template = atts.getProperty("template");
			ParticleComponent p = new ParticleComponent(template);
			p.setPosition(parseVector3f(atts.getProperty("p"), p.getPosition()));
			p.setScale(parseVector3f(atts.getProperty("s"), p.getScale()));
			particles.add(p);
		}

		for (Properties atts : def.getLights()) {

			String type = atts.getProperty("type");
			if (type.equalsIgnoreCase("point")) {
				lights.add(new PointLightConfiguration(parseColor(atts.getProperty("defaultColor"), ColorRGBA.White), parseFloat(
						atts.getProperty("radius"), 1f)));
			} else if (type.equalsIgnoreCase("spot")) {
				lights.add(new SpotLightConfiguration(parseColor(atts.getProperty("defaultColor"), ColorRGBA.White), parseFloat(
						atts.getProperty("range"), 1f), parseFloat(atts.getProperty("outer"), 1f), parseFloat(
						atts.getProperty("inner"), 1f)));
			} else {
				LOG.warning(String.format("Unknown light type %s.", type));
			}
		}

		for (Properties atts : def.getXrefs()) {
			XRef xr = new XRef(atts.getProperty("cref"));
			xr.setFloorAnchor(Integer.parseInt(replaceVariables(atts.getProperty("floorAnchor"),
					String.valueOf(xr.getFloorAnchor()))));
			String p = atts.getProperty("p"); // position
			String q = atts.getProperty("q"); // rotation (Quaternion)
			xr.setLocation(parseVector3f(p, xr.getLocation()));
			xr.setRotation(parseRotation(q, xr.getRotation()));
			xrefs.add(xr);
		}
	}

	public List<ParticleComponent> getParticles() {
		return particles;
	}

	public List<Light> getLights() {
		return lights;
	}

	public List<SoundItem> getSounds() {
		return sounds;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public List<XRef> getXRefs() {
		return xrefs;
	}

	private String replaceVariables(String mesh, String defaultValue) {
		if (mesh == null) {
			mesh = defaultValue;
		}
		if (mesh != null) {
			for (Map.Entry<String, String> en : variables.entrySet()) {
				if (en.getValue() != null)
					mesh = mesh.replace("$(" + en.getKey() + ")", en.getValue());
			}
		}
		return mesh;
	}

	protected Quaternion parseRotation(String q, Quaternion defaultValue) {
		if (q != null) {
			try {
				String[] args = q.split("\\s+");
				// OGRE quaternion is w,x,y,z, JME3 is x,y,z,w
				return new Quaternion(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]),
						Float.parseFloat(args[0]));
			} catch (Exception e) {
				LOG.warning(String.format("Could not parse rotation " + q));
			}
		}
		return defaultValue;
	}

	protected Vector3f parseVector3f(String p, Vector3f defaultValue) {
		if (p != null) {
			try {
				String[] args = p.split("\\s+");
				return new Vector3f(Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]));
			} catch (Exception e) {
				e.printStackTrace();
				LOG.warning(String.format("Could not parse position " + p));
			}
		}
		return defaultValue;
	}

	private ColorRGBA parseColor(String attVal, ColorRGBA defVal) {
		try {
			if (attVal != null) {
				return IceUI.toRGBA(new Color(attVal));
			}
		} catch (NumberFormatException nfe) {
		}
		return defVal;
	}

	private float parseFloat(String attVal, float defVal) {
		try {
			if (attVal != null) {
				return Float.parseFloat(attVal);
			}
		} catch (NumberFormatException nfe) {
		}
		return defVal;
	}
}
