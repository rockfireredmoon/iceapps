package org.icescene.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.AttachableTemplate;
import org.icelib.AttachmentItem;
import org.icelib.Icelib;
import org.icelib.RGB;
import org.icescene.Icescene;
import org.icescene.ServiceRef;
import org.icescene.assets.ExtendedMaterialListKey;
import org.icescene.assets.ExtendedOgreMeshKey;
import org.icescene.configuration.attachments.AttachableDef;
import org.icescene.ogreparticle.OGREParticleConfiguration;
import org.icescene.ogreparticle.OGREParticleEmitter;
import org.icescene.ogreparticle.OGREParticleScript;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.MaterialList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import emitter.Emitter;

public class AttachmentItemEntity extends AbstractLoadableEntity {

	private final static Logger LOG = Logger.getLogger(AttachmentItemEntity.class.getName());
	protected Skeleton skel;
	protected SkeletonControl skeletonControl;
	protected final AttachmentItem item;
	private Spatial itemSpatial;
	private AnimControl animControl;
	private List<Emitter> emitters = new ArrayList<Emitter>();

	@ServiceRef
	protected static AttachableDef attachableDef;

	public AttachmentItemEntity(EntityContext context, AttachmentItem item) {
		super(context, "AttachmentItemNode-" + item.getNode(), null);
		this.item = item;
	}

	@Override
	protected Map<String, RGB> createColorMap() {
		List<? extends RGB> c = item.getColors();
		Map<String, RGB> m = new HashMap<String, RGB>();
		if (c != null) {
			for (int i = 0; i < c.size(); i++) {
				m.put(Icelib.toNumberName(i + 1), c.get(i));
			}
		}
		return m;
	}

	@Override
	protected final void doCleanUpScene() {
		for (Emitter e : emitters) {
			e.setEnabled(false);
		}
		itemSpatial.removeFromParent();
		emitters.clear();
	}

	@Override
	protected final void doCleanUp() {
	}

	@Override
	protected void doLoad() throws Exception {
		final String meshPath = Icescene
				.checkAssetPath(String.format("%1$s/%2$s.mesh", item.getKey().getPath(), item.getKey().getName()));

		// Add particle effects from template
		List<OGREParticleScript> particleScripts = new ArrayList<OGREParticleScript>();
		AttachableTemplate temp = attachableDef.get(item.getKey());
		if (temp == null) {
			LOG.warning(String.format("No attachable definition %s", item.getKey()));
			itemSpatial = new Node();
		} else if (temp.isParticle()) {
			particleScripts.add(OGREParticleConfiguration.findScript(context.getAssetManager(), temp.getEntity()));
			// for (String s : temp.getParticles()) {
			// try {
			// particleScripts.add(OGREParticleConfiguration.findScript(assetManager,
			// s));
			// } catch (AssetNotFoundException anfe) {
			// LOG.log(Level.SEVERE, "Failed to load effect.", anfe);
			// }
			// }

			itemSpatial = new Node();
		} else {

			// Locate the mesh and crete an OgreMeshKey (to help Ogre loader
			// locate
			// materials)
			final ExtendedOgreMeshKey meshKey = new ExtendedOgreMeshKey(meshPath, new MaterialList()) {
				@Override
				public void configureMaterialKey(ExtendedMaterialListKey mk) {
					mk.setCache(false);
				}
			};

			// Actually load model (material will be set on spatial here)
			itemSpatial = context.getAssetManager().loadModel(meshKey);
			final String tintColorMapPath = Icescene
					.checkAssetPath(String.format("%1$s/%2$s-Tint.png.colormap", item.getKey().getPath(), item.getKey().getName()));
			final String tintPath = Icescene
					.checkAssetPath(String.format("%1$s/%2$s-Tint.png", item.getKey().getPath(), item.getKey().getName()));
			final String texturePath = Icescene
					.checkAssetPath(String.format("%1$s/%2$s.png", item.getKey().getPath(), item.getKey().getName()));
			new MaterialConfigurator(itemSpatial) {
				@Override
				protected void configureGeometry(Geometry geom) {
					texture(createColorMap(), context.getAssetManager(), geom.getMaterial(), tintColorMapPath, tintPath,
							texturePath);
				}
			};

		}

		// Add particle effects from user
		emitters.clear();
		String itemEffect = item.getEffect();
		if (Icelib.isNotNullOrEmpty(itemEffect)) {
			try {
				particleScripts.add(OGREParticleConfiguration.findScript(context.getAssetManager(), itemEffect));
			} catch (AssetNotFoundException anfe) {
				LOG.log(Level.SEVERE, "Failed to load effect.", anfe);
			}
		}

		for (OGREParticleScript s : particleScripts) {
			for (OGREParticleEmitter i : s.getEmitters()) {
				final Emitter emitter = i.createEmitter(context.getAssetManager());
				emitter.setEnabled(true);
				emitter.initialize(context.getAssetManager());
				emitters.add(emitter);
			}
		}
	}

	@Override
	protected void doLoadScene() {
		for (Emitter e : emitters) {
			spatial.addControl(e);
		}

		animControl = itemSpatial.getControl(AnimControl.class);
		if (animControl != null) {
			for (String s : animControl.getAnimationNames()) {
				AnimChannel ch = animControl.createChannel();
				ch.setAnim(s);
				ch.setLoopMode(LoopMode.Loop);
				ch.setSpeed(1f);
			}
		}
		spatial.attachChild(itemSpatial);
	}
}
