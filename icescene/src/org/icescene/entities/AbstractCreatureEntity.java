package org.icescene.entities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.AbstractCreature;
import org.icelib.AttachableTemplate;
import org.icelib.AttachmentItem;
import org.icelib.AttachmentPoint;
import org.icelib.EntityKey;
import org.icelib.EquipType;
import org.icelib.Icelib;
import org.icelib.RGB;
import org.icelib.Slot;
import org.icescene.ServiceRef;
import org.icescene.assets.ExtendedMaterialListKey;
import org.icescene.assets.ExtendedOgreMeshKey;
import org.icescene.assets.ExtendedMaterialListKey.Lighting;
import org.icescene.configuration.attachments.AttachableDef;
import org.icescene.configuration.attachments.AttachmentPlace;
import org.icescene.configuration.attachments.AttachmentPoints;
import org.icescene.configuration.creatures.AbstractCreatureDefinition;
import org.icescene.configuration.creatures.CreatureKey;

import com.jme3.animation.SkeletonControl;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public abstract class AbstractCreatureEntity<C extends AbstractCreatureDefinition> extends AbstractSpawnEntity {

	protected final static List<String> DEFAULT_LOOP_ANIMS = Arrays.asList("idle", "combatidle", "walk", "run");

	private static final Logger LOG = Logger.getLogger(AbstractCreatureEntity.class.getName());
	// protected AttachmentPointSet attachmentConfig;
	protected C entityDefinition;
	protected Map<AttachmentItem, Slot> allAttachments;
	protected CreatureKey entityKey;

	@ServiceRef
	protected static AttachmentPoints attachmentPoints;
	// @ServiceRef
	// protected static AttachableTemplates attachablesConfig;
	@ServiceRef
	protected static AttachableDef attachableDef;

	public AbstractCreatureEntity(EntityContext context, AbstractCreature creature, String path, String entityInstanceId) {
		super(context, creature, path, entityInstanceId);
	}

	@Override
	protected final void onCleanUpScene() {
		entityDefinition = null;
		allAttachments.clear();
	}

	@Override
	protected final void onBeforeSpawnLoad() throws Exception {

		// attachmentConfig = attachmentPoints.getSet(appearance);

		onBeforeCreatureLoad();

		// Locate the mesh and crete an OgreMeshKey (to help Ogre loader locate
		// materials)
		String assetPath = getCreatureMeshPath();
		LOG.info(String.format("Body mesh path is %s", assetPath));
		final ExtendedOgreMeshKey meshKey = new ExtendedOgreMeshKey(assetPath) {
			@Override
			public void configureMaterialKey(ExtendedMaterialListKey mk) {
				mk.setCache(false);
				mk.setLitMaterialDef("MatDefs/MOB_Lit.j3md");
				mk.setUnlitMaterialDef("MatDefs/MOB_Unlit.j3md");
			}
		};

		// The script definition may use different textures (when creatures are
		// sharing meshes)
		// List<String> texture = entityDefinition.getTexture();
		// if(texture != null && !texture.isEmpty()) {
		// meshKey.getTextures().put("Diffuse", texture.get(0));
		// if(texture.size() > 1) {
		// LOG.warning(String.format("Multiple alternative textures for %s not
		// yet supported. %s",
		// assetPath , texture));
		// }
		// }

		// Load any audio config files for this model
		// AudioConfiguration.loadDir(Icelib.removeTrailingSlashes(FilenameUtils.getPath(assetPath)),
		// context.getAssetManager());

		// Actually load model (material will be set on spatial here)
		bodyMesh = context.getAssetManager().loadModel(meshKey);
		new MaterialConfigurator(bodyMesh) {
			@Override
			protected void configureGeometry(Geometry geom) {
				onMaterial(geom.getMaterial());
			}
		};

	}

	/**
	 * Get the creature definition used for this spatial
	 * 
	 * @return creature definition
	 */
	public C getDefinition() {
		return entityDefinition;
	}

	protected void onBeforeCreatureLoad() throws Exception {
	}

	protected Map<AttachmentItem, Slot> getAttachments() {
		Map<AttachmentItem, Slot> l = new HashMap<>();
		for (AttachmentItem item : appearance.getAttachments()) {
			// TODO how slots are determined for this need sorting
			l.put(item, null);
		}
		return l;
	}

	@Override
	protected final void onSpawnLoad() throws Exception {

		spatial.addControl(createAnimationHandler());

		// Get the skeleton (mainly for attachments)
		skeletonControl = bodyMesh.getControl(SkeletonControl.class);
		if (skeletonControl == null) {
			LOG.warning(String.format("No skeleton for %s", getCreatureMeshPath()));
		} else {
			try {
				skel = skeletonControl.getSkeleton();
				if (isShowingNameplate()) {
					addNameplate();
				}

			} catch (final Exception e) {
				LOG.log(Level.SEVERE, "Failed on body skeleton.", e);
			}
		}
		// Attachments
		allAttachments = getAttachments();
		for (Map.Entry<AttachmentItem, Slot> item : allAttachments.entrySet()) {
			try {
				addAttachment(item.getKey(), item.getValue());
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to add attachment.", e);
			}
		}

		onCreatureLoad();
		reloadSkin();
	}

	protected void onCreatureLoad() throws Exception {
	}

	protected void configureMeshMaterialKey(ExtendedMaterialListKey mk) {
		mk.setLitMaterialDef("MatDefs/MOB_Lit.j3md");
		mk.setUnlitMaterialDef("MatDefs/MOB_Unlit.j3md");
	}

	@Override
	protected Map<String, RGB> createColorMap() {
		Map<String, RGB> skinMap = appearance.getSkinMap();
		Map<String, RGB> colorMapValues = new LinkedHashMap<String, RGB>(skinMap);
		colorMapValues.putAll(creature.getAppearance().getSkinMap());

		// for (Skin el : entityDefinition.getSkin().values()) {
		// colorMapValues.put(el.getName(), skinMap.containsKey(el.getName()) ?
		// skinMap.get(el.getName()) : el.getDefaultColour());
		// }
		// for (Map.Entry<String, RGB> en : colorMapValues.entrySet()) {
		// Icelib.removeMe("createColorMap %s %s", en.getKey(), en.getValue());
		// }
		// return colorMapValues;
		// return appearance.getSkinMap();
		return colorMapValues;
	}

	public void reloadSkin() {
		LOG.info(String.format("Reloading skin for %s (%d materials)", entityDefinition, materials.size()));
		for (Material m : materials) {
			onSpawnMaterial(m);
		}
	}

	protected AttachmentPoint getDefaultNode(EntityKey assetName, Slot slot) {
		AttachmentPoint node = null;
		AttachableTemplate attachTemplate = attachableDef.get(assetName);
		if (attachTemplate == null) {
			LOG.log(Level.SEVERE, String.format("No attachable template for item definition %s", assetName));
		} else {
			// TODO how do we select which point to use. I think it should
			// be based on the
			// weapon type, and the current armed state (e.g. the X key in
			// game)
			node = attachTemplate.getAttachPoints().get(0);

			// TODO hopefully this will not be needed once i have more data
			if (node == null && slot != null) {
				node = slot.toDefaultAttachmentPoint(EquipType.Side.NA);
			}
		}
		return node;
	}

	protected void addAttachment(AttachmentItem att, Slot slot) throws Exception {
		AttachmentItemEntity attSpatial = new AttachmentItemEntity(context, att);
		LOG.info(String.format("Attaching %s", att));
		attSpatial.loadAndUpdateScene();
		AttachmentPoint node = att.getNode();
		if (node == null) {
			node = getDefaultNode(att.getKey(), slot);
		}
		if (node == null) {
			LOG.log(Level.SEVERE, String.format("Cannot determine attachment point for %s", att.getKey()));
		} else {
			attachToBone(node, attSpatial.getSpatial());
		}
	}

	public void attachToBone(AttachmentPoint node, Spatial attSpatial) {
		AttachmentPlace place = getAttachmentPoint(node);
		if (place == null) {
			LOG.log(Level.SEVERE, String.format("Cannot determine attachment place %s", node));
		} else {
			if (place.getPosition() != null) {
				attSpatial.setLocalTranslation(place.getPosition());
			}
			if (place.getOrientation() != null) {
				attSpatial.setLocalRotation(place.getOrientation());
			}
			if (place.getScale() != null) {
				attSpatial.setLocalScale(place.getScale());
			}
			String bone = place.getBone();
			LOG.info(String.format("Attaching %s to %s", node, bone));
			Node n = skeletonControl.getAttachmentsNode(bone);
			if (n == null) {
				LOG.warning(String.format("Cannot find bone %s", bone));
				for (int i = 0; i < skeletonControl.getSkeleton().getBoneCount(); i++) {
					LOG.warning(String.format("   BONE: %s", skeletonControl.getSkeleton().getBone(i)));
				}
			}
			n.attachChild(attSpatial);
		}
	}

	protected abstract AttachmentPlace getAttachmentPoint(AttachmentPoint node);

	@Override
	protected Material texture(String tintColorMapPath, String tintPath, String texturePath, Material material) {
		return texture(createColorMap(), context.getAssetManager(), material, tintColorMapPath, tintPath, texturePath);
	}

	protected abstract String getCreatureMeshPath();

	protected void attachNameplate(BitmapText playerText) {
		Node n = new Node();
		n.attachChild(playerText);
		spatial.attachChild(n);
	}

	protected String getNameplateText() {
		String displayName = creature.getDisplayName();
		if (creature.getSubName() != null) {
			displayName += "\n<" + creature.getSubName() + ">";
		}
		return displayName;
	}
}
