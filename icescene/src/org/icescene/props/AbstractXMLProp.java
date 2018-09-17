package org.icescene.props;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.assets.ExtendedMaterialListKey;
import org.icescene.assets.ExtendedOgreMeshKey;
import org.icescene.audio.AudioAppState;
import org.icescene.audio.AudioQueue;
import org.icescene.audio.QueuedAudio;
import org.icescene.configuration.AudioConfiguration;
import org.icescene.configuration.AudioConfiguration.Sound;
import org.icescene.controls.SynchronizingPhysicsControl;
import org.icescene.entities.EntityContext;
import org.icescene.io.MouseManager;
import org.icescene.ogreparticle.OGREParticleConfiguration;
import org.icescene.ogreparticle.OGREParticleEmitter;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.scene.Buildable;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.MaterialList;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

import emitter.Emitter;

public abstract class AbstractXMLProp extends AbstractProp {

	private final static Logger LOG = Logger.getLogger(AbstractXMLProp.class.getName());
	private static final boolean COLLISION_ENABLED = !"true"
			.equals(System.getProperty("icescene.disableBlockingMeshes", "false"));
	protected Component component;
	protected Spatial propSpatial;
	private List<Spatial> physicsMeshesToAddOnAttachment = new ArrayList<Spatial>();
	private List<Spatial> physicsMeshesToRemove = new ArrayList<Spatial>();
	private Map<Buildable, Spatial> entities = new HashMap<Buildable, Spatial>();
	private List<QueuedAudio> sounds = new ArrayList<QueuedAudio>();

	public AbstractXMLProp(String name, EntityContext app) {
		super(name, app);
	}

	public Component getComponent() {
		return component;
	}

	@Override
	protected void onSpatialAttachedOrDetached() {
		if (spatial.getParent() == null) {
			BulletAppState bullet = context.getStateManager().getState(BulletAppState.class);
			//
			// Remove physics meshes
			for (Spatial s : physicsMeshesToRemove) {
				LOG.info(String.format("Removing physics mesh %s", s.getName()));
				RigidBodyControl rbc = s.getControl(RigidBodyControl.class);
				// if (rbc != null) {
				// rbc.destroy();
				// }
				if (bullet != null)
					bullet.getPhysicsSpace().remove(s);
				// s.removeFromParent();
			}
			physicsMeshesToRemove.clear();
			for (QueuedAudio qa : sounds) {
				qa.stop();
			}
		} else {

			// Can now add physics meshes
			if (physicsMeshesToAddOnAttachment.size() > 0) {
				for (Spatial s : physicsMeshesToAddOnAttachment) {
					configurePhysicsMesh(s);
					spatial.getParent().attachChild(s);
				}
				physicsMeshesToAddOnAttachment.clear();
			}

		}
	}

	public void reload() {
		Node currentParent = null;
		if (component != null) {
			currentParent = spatial.getParent();

			// Stop and remove sounds
			for (Spatial s : spatial.getChildren()) {
				if (s instanceof AudioNode) {
					((AudioNode) s).stop();
				}
			}

			// Remove all children
			spatial.detachAllChildren();

			// Remove physics meshes etc
			spatial.removeFromParent();
		}
		configureProp();
		if (currentParent != null) {
			currentParent.attachChild(spatial);
		}
	}

	@Override
	public void onConfigureProp() {
		component = context.getComponentManager().get(sceneryItem);

		// Load any audio config files for this model
		// AudioConfiguration.loadDir(component.getAssetFolder(),
		// context.getAssetManager());

		// TODO LOD generation and baking
		// TODO Optimise props (e.g. CL's) by using batches for all spatials
		// with same material

		addMesh();
		addPhysicsMeshes();
		addSound();
		addLights();
		addParticles();
	}

	@Override
	public AbstractProp clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScale(Vector3f scale) {
		Vector3f s = getScale().clone();
		super.setScale(scale);
		if (!s.equals(getScale())) {
			// Do physics meshes do
			for (Spatial sp : physicsMeshesToRemove)
				sp.setLocalScale(getScale());
			for (Spatial sp : physicsMeshesToAddOnAttachment)
				sp.setLocalScale(getScale());
		}
	}

	public Spatial getMesh(Buildable component) {
		return entities.get(component);
	}

	protected void addPhysicsMeshes() {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Adding physics meshs for %s", getName()));
		}
		for (Entity entity : component.getEntities()) {
			if (!entity.isNormal() && COLLISION_ENABLED) {
				Spatial otherMesh = null;
				try {
					// Load from mode file
					String mesh = entity.getMesh();
					if (mesh == null) {
						if (entity.getPhysicsMeshClass() != null) {
							LOG.warning("TODO load physics meshes");
						} else {
							LOG.severe(String.format("Prop %s is neither a file mesh or a mesh class.", entity));
						}
					} else {
						if (!mesh.endsWith(".xml")) {
							mesh += ".xml";
						}
						final String meshPath = resolveSubmesh(mesh);
						if (LOG.isLoggable(Level.INFO)) {
							LOG.info(String.format("Loading physics model %s", meshPath));
						}
						otherMesh = context.getAssetManager().loadModel(meshPath);

						/*
						 * When importing meshes this way, we actually get a
						 * Node spatial, but because we want to be able to scale
						 * physics meshes too, we actually need a Geometry. So,
						 * we look for the geometry in the returned model and
						 * use that instead.
						 * 
						 * http://hub.jmonkeyengine.org/forum/topic/using-
						 * setscale- vector3f-on-collisionshapes/
						 */

						if (otherMesh instanceof Node) {
							Node node = (Node) otherMesh;
							for (Spatial s : node.getChildren()) {
								if (s instanceof Geometry) {
									otherMesh = s;
									break;
								}
							}
						}

						// otherMesh.setMaterial(MaterialFactory.getManager().getMaterial("Standard/Invisible",
						// app.getAssetManager()));

						// TODO make this configurable somehow (like /sb)
						// otherMesh.setMaterial(new
						// WireframeWidget(context.getAssetManager(),
						// entity.isBlocking() ? ColorRGBA.Green :
						// ColorRGBA.Blue));
						// otherMesh.setCullHint(CullHint.Never);
						otherMesh.setUserData(MouseManager.IGNORE, Boolean.TRUE);
						otherMesh.setCullHint(CullHint.Always);
						otherMesh.setShadowMode(RenderQueue.ShadowMode.Off);
						otherMesh.setLocalScale(getScale());
						entities.put(entity, otherMesh);

						if (spatial.getParent() != null) {
							;
							configurePhysicsMesh(otherMesh);
							spatial.getParent().attachChild(otherMesh);
						} else {
							// Defer until attached to parent
							physicsMeshesToAddOnAttachment.add(otherMesh);
						}
					}
				} catch (Exception e) {
					if (LOG.isLoggable(Level.FINE))
						LOG.log(Level.SEVERE, String.format("Failed to load mesh for component. %s", entity), e);
					else
						LOG.log(Level.SEVERE, String.format("Failed to load mesh for component. %s", entity), e);
				}

			}
		}
	}

	protected void addLights() {
		// Light
		for (Light l : component.getLights()) {
			/*
			 * Lights have to be attached to the scene in control and kept in
			 * sync with any spatials they are attached to using a control.
			 */
			if (lightingParent == null) {
				throw new RuntimeException(toString() + " has lighting, but no parent for the lighting has been set.");
			}
			addLight(l, lightingParent);
		}
	}

	protected void addParticles() {
		if (component.getParticles().size() > 0) {
			LOG.info(String.format("Component has %d particles", component.getParticles().size()));
			for (ParticleComponent pc : component.getParticles()) {
				OGREParticleScript scr = OGREParticleConfiguration.findScript(context.getAssetManager(),
						pc.getTemplate());
				for (OGREParticleEmitter i : scr.getEmitters()) {
					LOG.info(String.format("Adding particle template %s", i.getScript().getName()));
					Node n = new Node("Particle-" + i.getScript().getName());
					propSpatials.add(n);
					final Emitter emitter = i.createEmitter(context.getAssetManager());
					emitter.setEnabled(true);
					emitter.initialize(context.getAssetManager());
					n.addControl(emitter);
					n.setLocalScale(pc.getScale());
					n.setLocalTranslation(pc.getPosition());
					spatial.attachChild(n);
				}
			}
		}
	}

	protected void addSound() {
		if (component.getSounds().size() > 0) {
			LOG.info(String.format("Component has %d sounds", component.getSounds().size()));
			// String rn = component.getAssetPath();
			// rn = rn.substring(rn.lastIndexOf('/') + 1);

			// TODO preload dir that has config files in it
			// AudioConfiguration cfg = null;
			// try {
			// cfg = new AudioConfiguration(component.absolutize(rn.substring(0,
			// rn.indexOf('.')) + ".sound.cfg"),
			// app.getAssetManager());
			// } catch (AssetNotFoundException anfe) {
			// // No sound config, everything comes from sound item
			// }

			for (SoundItem sound : component.getSounds()) {
				// TODO all these as variables (i.e. extend SoundItem)
				String name = sound.getName();
				float gain = sound.getGain();
				boolean loop = true;
				boolean positional = true;
				float ref = 10;
				float max = 220;
				AudioQueue queue = AudioQueue.AMBIENT;
				Sound s = AudioConfiguration.getSound(name, context.getAssetManager());
				if (s != null) {
					gain = gain * s.getGain();
					loop = s.isLoop();
					queue = s.getQueue();
					ref = s.getRefDistance();
					max = s.getMaxDistance();
				}
				if (!name.startsWith("http:") && !name.startsWith("https:")) {
					name = s.getConfiguration().absolutize(name);
				}
				addSound(name, loop, queue, gain, ref, max, positional);
			}
		}
	}

	protected void addSound(String name, boolean loop, AudioQueue channel, float gain, float ref, float max,
			boolean positional) {
		boolean stream = true;
		boolean streamCache = true;
		// For backward compatibility, if the name has no '/', it is in
		// Sounds/General
		if (name.startsWith("http:") || name.startsWith("https:")) {
			streamCache = false;
		}
		// else if (name.indexOf('/') == -1) {
		// name = "Sounds/General/" + name;
		// }

		QueuedAudio qa = new QueuedAudio(spatial, name, 0, loop, channel, gain);
		qa.setRefDistance(ref);
		qa.setMaxDistance(max);
		qa.setPositional(positional);
		qa.setStream(stream);
		qa.setStreamCache(streamCache);
		queueSound(qa);
	}

	protected void queueSound(QueuedAudio qa) {
		sounds.add(qa);
		AudioAppState aas = context.getStateManager().getState(AudioAppState.class);
		LOG.info(String.format("Queueing sound %s for %s", qa.getPath(), getName()));
		aas.queue(qa);
	}

	protected void addMesh() {
		boolean doTrans = false;
		propSpatial = null;
		for (Entity entity : component.getEntities()) {
			if (entity.isNormal()) {
				try {
					if (entity.getMeshClass() == null) {
						// Load from mode file
						String mesh = entity.getMesh();
						if (!mesh.endsWith(".xml")) {
							mesh += ".xml";
						}
						final String meshPath = resolveSubmesh(mesh);
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Loading prop model %s", meshPath));
						}

						ExtendedOgreMeshKey meshKey = new ExtendedOgreMeshKey(meshPath, new MaterialList()) {
							@Override
							public void configureMaterialKey(ExtendedMaterialListKey mk) {
								configureMeshMaterialKey(mk);
							}
						};
						propSpatial = context.getAssetManager().loadModel(meshKey);

						// MeshConfiguration meshConfig =
						// MeshConfiguration.get(app.getAssetManager(),
						// meshPath);
						// Material propMaterial = createMaterial(meshConfig,
						// app.getAssetManager());
						// if (propMaterial.isTransparent()) {
						// doTrans = true;
						// }
						// propSpatial.setMaterial(propMaterial);

					} else {
						// Load a special java spatial
						propSpatial = entity.getMeshClass().getConstructor(String.class, AssetManager.class)
								.newInstance("Prop", context.getAssetManager());
					}
				} catch (Exception e) {
					if (LOG.isLoggable(Level.FINE))
						LOG.log(Level.SEVERE, String.format("Failed to load mesh for component. %s", entity), e);
					else
						LOG.log(Level.SEVERE, String.format("Failed to load mesh for component. %s", entity), e);
				}

				if (propSpatial != null) {
					entities.put(entity, propSpatial);
					propSpatial.setLocalTranslation(entity.getTranslation());
					if (entity.getRotation() != null) {
						propSpatial.setLocalRotation(entity.getRotation());
					}
					spatial.attachChild(propSpatial);
					propSpatials.add(propSpatial);
				}

			}
		}

		for (XRef xref : component.getXRefs()) {
			try {
				String prop = xref.getCRef();

				// TODO why is this? Par-Candle_Glow1 is referred to without
				// '-Emitter' on the end,
				// and the same for 'Par-BigExplosion'. Investigate why, and
				// make uniform?
				// NOTE it is appears its because the component ID differs from
				// the filename in this
				// case and those are used
				if (prop.startsWith("Par-") && !prop.endsWith("-Emitter")) {
					prop = prop + "-Emitter";
				}

				EntityFactory factory = (EntityFactory) context;
				propSpatial = factory.getProp(prop).getSpatial();
			} catch (Exception e) {
				if (LOG.isLoggable(Level.FINE))
					LOG.log(Level.SEVERE, String.format("Failed to load prop for component XRef. %s", xref), e);
				else
					LOG.log(Level.SEVERE, String.format("Failed to load prop for component XRef. %s", xref), e);
			}

			if (propSpatial != null) {
				entities.put(xref, propSpatial);
				propSpatial.setLocalTranslation(xref.getTranslation());
				if (xref.getRotation() != null) {
					propSpatial.setLocalRotation(xref.getRotation());
				}
				spatial.attachChild(propSpatial);
				propSpatials.add(propSpatial);
			}

		}

		if (doTrans) {
			spatial.setQueueBucket(RenderQueue.Bucket.Transparent);
		}
	}

	protected String resolveSubmesh(String mesh) {
		final String meshPath = component.absolutize(mesh);
		return meshPath;
	}

	protected void configureMeshMaterialKey(ExtendedMaterialListKey mk) {
	}

	private void configurePhysicsMesh(Spatial s) {
		LOG.info("Adding physics meshes for " + s.getName());
		final BulletAppState state = context.getStateManager().getState(BulletAppState.class);
		if (state != null) {
			CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(s);

			RigidBodyControl rbc = new RigidBodyControl(sceneShape, 0);
			s.addControl(rbc);
			s.addControl(new SynchronizingPhysicsControl(spatial));
			state.getPhysicsSpace().add(s);
		} else {
			LOG.info("No physics appstate available, not creating physics meshes");
		}
		physicsMeshesToRemove.add(s);
	}
}
