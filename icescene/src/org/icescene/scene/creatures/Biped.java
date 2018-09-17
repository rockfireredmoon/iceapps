package org.icescene.scene.creatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.icelib.AbstractCreature;
import org.icelib.Appearance;
import org.icelib.AttachmentItem;
import org.icelib.AttachmentPoint;
import org.icelib.BodyType;
import org.icelib.ClothingItem;
import org.icelib.ClothingTemplate;
import org.icelib.ClothingTemplateKey;
import org.icelib.Icelib;
import org.icelib.ItemAppearance;
import org.icelib.RGB;
import org.icelib.Region;
import org.icelib.Slot;
import org.icescene.Icescene;
import org.icescene.ServiceRef;
import org.icescene.animation.AnimationDefs;
import org.icescene.assets.ExtendedMaterialListKey;
import org.icescene.assets.ExtendedOgreMeshKey;
import org.icescene.configuration.ClothingDef;
import org.icescene.configuration.attachments.AttachmentPlace;
import org.icescene.configuration.attachments.AttachmentPointSet;
import org.icescene.configuration.creatures.ContentDef;
import org.icescene.configuration.creatures.CreatureDefinition;
import org.icescene.configuration.creatures.CreatureKey;
import org.icescene.configuration.creatures.Detail;
import org.icescene.configuration.creatures.DetailElements;
import org.icescene.configuration.creatures.DetailType;
import org.icescene.configuration.creatures.DetailVariants;
import org.icescene.configuration.creatures.Details;
import org.icescene.configuration.creatures.Head;
import org.icescene.controls.AnimationHandler;
import org.icescene.controls.BipedAnimationHandler;
import org.icescene.entities.AbstractCreatureEntity;
import org.icescene.entities.EntityContext;
import org.icescene.entities.MaterialConfigurator;
import org.icescene.scene.ClothingTexture;
import org.icescene.scene.ClothingTextureKey;

import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Biped extends AbstractCreatureEntity<CreatureDefinition> {

	protected final static String PATH = "Biped";
	protected final static String TYPE = "Biped";
	private final static Logger LOG = Logger.getLogger(Biped.class.getName());
	private Head head;
	private Map<ClothingTextureKey, ClothingTexture> clothingTextures = new LinkedHashMap<ClothingTextureKey, ClothingTexture>();
	private Map<Slot, ItemAppearance> itemAppearance = new EnumMap<Slot, ItemAppearance>(Slot.class);
	private BodyType bodyType;
	private BodyType armBodyType;
	protected String genderName;
	protected String raceName;
	protected String bodyMeshName;
	protected String bodySizeSuffix;
	protected Map<DetailType, List<Spatial>> detailSpatials = new EnumMap<DetailType, List<Spatial>>(DetailType.class);
	private Spatial headModel;
	private Spatial leftModel;
	private Spatial rightModel;

	@ServiceRef
	protected static ClothingDef clothingDef;
	@ServiceRef
	protected static ContentDef contentDef;

	@ServiceRef
	protected static AnimationDefs animationDefs;

	public Biped(EntityContext context, AbstractCreature creature, String entityInstanceId) {
		super(context, creature, PATH, entityInstanceId);
	}

	// @Override
	// public Collection<AnimationSequence> getAnimationPresets() {
	//
	// if (entityDefinition != null) {
	// List<AnimationSequence> s = new ArrayList<AnimationSequence>();
	// for (Map.Entry<String, AnimationOption> o :
	// animationDefs.get("Biped").getAnimations().entrySet()) {
	// // String key = entityKey.getName() + "_" + o.getKey();
	// String key = o.getKey();
	// AnimationSequence seq = new AnimationSequence(key);
	// seq.setSounds(o.getValue().getSounds().values());
	//
	// Anim anim = new Anim(SubGroup.NONE, Part.ALL, key);
	//
	// // TODO a better way?
	// if (DEFAULT_LOOP_ANIMS.contains(key.toLowerCase())) {
	// anim.setLoopMode(LoopMode.Loop);
	// }
	//
	// seq.addAnim(anim);
	// s.add(seq);
	// }
	// return s;
	// }
	// return Collections.emptyList();
	//
	// // return AnimationSequence.list();
	// }

	public void clearAppearance() {
		itemAppearance.clear();
	}

	public void setAppearance(Slot equipType, ItemAppearance appearance) {
		itemAppearance.put(equipType, appearance);
	}

	protected AnimationHandler<CreatureDefinition, Biped> createAnimationHandler() {
		return new BipedAnimationHandler(this, context);
	}

	@Override
	protected final void onBeforeCreatureLoad() throws Exception {
		// // Seems a bit weird these are not defined in CreatureDef.

		genderName = Icelib.toEnglish(creature.getAppearance().getGender());
		raceName = Icelib.toEnglish(creature.getAppearance().getRace());
		bodyMeshName = TYPE + "-" + raceName + "_" + genderName + "-Body";
		bodySizeSuffix = creature.getAppearance().getBody() == null ? "" : creature.getAppearance().getBody().getMeshSuffix();

		clothingTextures.clear();

		armBodyType = BodyType.NORMAL;
		bodyType = BodyType.NORMAL;

		// Get the static creature definition
		entityKey = new CreatureKey("Biped", Icelib.toEnglish(creature.getAppearance().getRace()) + "_"
				+ Icelib.toEnglish(creature.getAppearance().getGender()));
		entityDefinition = contentDef.get(entityKey);
		if (entityDefinition == null) {
			throw new IllegalArgumentException(String.format("No creature definition for %s", entityKey));
		}

		/*
		 * Build up a list of all the textures that will be needed, and what
		 * items they apply to. This is the first pass, after this the actual
		 * material will be configured using the information gathered
		 * 
		 * Biped appearance can come from two places. Firstly the {@link
		 * Appearance} data that describes the biped as a while.
		 * 
		 * Secondly, appearance data from any equipped items.
		 */

		// Appearance data
		for (ClothingItem item : creature.getAppearance().getClothing()) {
			LOG.info(String.format("Adding appearance clothing %s to %s", item.getKey(), item.getType()));
			addClothingTexture(item.getKey(), item.getType(), item.getColors());
		}

		// Item appearance data
		for (Map.Entry<Slot, ItemAppearance> m : itemAppearance.entrySet()) {
			Slot slot = m.getKey();
			if (slot.isBasic()) {
				ItemAppearance app = m.getValue();
				if (app == null) {
					LOG.severe(String.format("No item appearance for %s", slot));
				} else {
					// We have an appearance for this item
					String clothingType = app.getClothingType();
					if (clothingType == null) {
						LOG.severe(String.format("No item appearance asset for slot %s", slot));
					} else {
						LOG.info(String.format("Adding item clothing %s to %s", clothingType, slot));
						addClothingTexture(new ClothingTemplateKey(clothingType), slot.toEquipType().toClothingItemType(),
								app.getClothingColor());
					}
				}
			}
		}
	}

	@Override
	protected final void onSpawnMaterial(Material material) {
		super.onSpawnMaterial(material);

		textureBodyPart(material, bodyMeshName, raceName, genderName);

		// Now configure the material. Sort by the order in which the textures
		// must be laid down.
		// This order may change depending on the mesh types (e.g. robed)
		List<ClothingTexture> texes = new ArrayList<ClothingTexture>(clothingTextures.values());
		Collections.sort(texes, new Comparator<ClothingTexture>() {
			public int compare(ClothingTexture o1, ClothingTexture o2) {
				Integer ob1 = new Integer(o1.getKey().getRegion().toSortKey(bodyType));
				Integer ob2 = new Integer(o2.getKey().getRegion().toSortKey(bodyType));
				return ob1.compareTo(ob2);
			}
		});
		int idx = 0;
		for (ClothingTexture tex : texes) {
			tex.configureMaterial(material, idx++);
		}
	}

	protected Map<AttachmentItem, Slot> getAttachments() {
		Map<AttachmentItem, Slot> l = new HashMap<>();
		l.putAll(super.getAttachments());
		for (Map.Entry<Slot, ItemAppearance> m : itemAppearance.entrySet()) {
			Slot slot = m.getKey();
			if (slot.isBasic()) {
				LOG.info(String.format("Adding attachments to %s", slot));
				ItemAppearance app = m.getValue();
				if (app == null) {
					LOG.severe(String.format("No item appearance for %s", slot));
				} else {
					for (AttachmentItem att : app.getAttachments()) {
						l.put(att, slot);
					}
				}
			}
		}
		return l;
	}

	@Override
	protected final void onCreatureLoad() throws Exception {
		if (skeletonControl == null) {
			LOG.warning("No skeleton for " + spatial.getName());
			return;
		}
		//
		// for (Map.Entry<Slot, ItemAppearance> m : itemAppearance.entrySet()) {
		// Slot slot = m.getKey();
		// if (slot.isBasic()) {
		// LOG.info(String.format("Adding attachments to %s", slot));
		// ItemAppearance app = m.getValue();
		// if (app == null) {
		// LOG.severe(String.format("No item appearance for %s", slot));
		// } else {
		// for (AttachmentItem att : app.getAttachments()) {
		// addAttachment(att, slot);
		// }
		// }
		// }
		// }

		LOG.info(String.format("Attaching head to %s", spatial.getName()));
		attachHead();
		LOG.info(String.format("Attaching forearms to %s", spatial.getName()));
		attachForearms();
		LOG.info(String.format("Attaching details to %s", spatial.getName()));
		attachDetails();
	}

	@Override
	public void updateSize() {
		// Adjust to race specific size multiplier
		float ts = (float) creature.getAppearance().getSize() * entityDefinition.getSizeMultiplier();
		LOG.info(String.format("Updating scale to %.3f (creature %.3f, %.3f)", ts, (float) creature.getAppearance().getSize(),
				entityDefinition.getSizeMultiplier()));
		bodyMesh.setLocalScale(ts);
		for (Map.Entry<DetailType, List<Spatial>> sen : detailSpatials.entrySet()) {
			for (Spatial s : sen.getValue()) {
				switch (sen.getKey()) {
				case EARS:
					s.setLocalScale(creature.getAppearance().getEarSize() * entityDefinition.getSizeMultiplier());
					break;
				case TAIL:
					s.setLocalScale(creature.getAppearance().getTailSize() * entityDefinition.getSizeMultiplier());
					break;
				default:
					break;
				}
			}
		}
	}

	public void reloadSkin() {
		super.reloadSkin();
		if (headModel != null) {
			new MaterialConfigurator(headModel) {
				@Override
				protected void configureGeometry(Geometry geom) {
					textureBodyPart(geom.getMaterial(), head.getTexture(), raceName, genderName);
				}
			};
		}
		if (leftModel != null) {
			new MaterialConfigurator(leftModel) {
				@Override
				protected void configureGeometry(Geometry geom) {
					onSpawnMaterial(geom.getMaterial());
				}
			};
		}
		if (rightModel != null) {
			new MaterialConfigurator(rightModel) {
				@Override
				protected void configureGeometry(Geometry geom) {
					onSpawnMaterial(geom.getMaterial());
				}
			};
		}

		Details creatureDetails = entityDefinition.getDetails();
		for (Map.Entry<DetailType, DetailVariants> den : creatureDetails.entrySet()) {
			// Only 'def' exists?
			DetailVariants variants = den.getValue();
			DetailElements var = variants.get("def");
			for (Detail detail : var) {
				List<Spatial> list = detailSpatials.get(den.getKey());
				if (list != null) {
					for (Spatial s : list) {
						new MaterialConfigurator(s) {
							@Override
							protected void configureGeometry(Geometry geom) {
								String textureName;
								if (detail.getTexture().equals("head")) {
									textureName = head.getTexture();
								} else {
									throw new RuntimeException("Unknown texture name " + detail.getTexture());
								}
								textureBodyPart(geom.getMaterial(), textureName, raceName, genderName);
							}
						};
					}
				}
			}
		}

	}

	protected final Material textureBodyPart(Material material, String part, String raceName, String genderName) {

		String tintColorMapPath = Icescene.checkAssetPath(
				String.format("%1$s/%2$s-%3$s_%4$s/%5$s-Tint.png.colormap", path, TYPE, raceName, genderName, part));
		String tintPath = Icescene
				.checkAssetPath(String.format("%1$s/%2$s-%3$s_%4$s/%5$s-Tint.png", path, TYPE, raceName, genderName, part));
		String texturePath = Icescene
				.checkAssetPath(String.format("%1$s/%2$s-%3$s_%4$s/%5$s.png", path, TYPE, raceName, genderName, part));
		return texture(tintColorMapPath, tintPath, texturePath, material);
	}

	@Override
	protected String getCreatureMeshPath() {
		String genderAndBodyType = genderName;
		if (BodyType.ROBED.equals(bodyType)) {
			genderAndBodyType = genderAndBodyType + "-Robed";
		}
		return Icescene.checkAssetPath(
				String.format("%1$s/%2$s-%4$s%5$s/%2$s-%4$s%5$s.mesh", path, TYPE, genderName, genderAndBodyType, bodySizeSuffix));
	}

	@Override
	protected AttachmentPlace getAttachmentPoint(AttachmentPoint node) {
		AttachmentPlace place = entityDefinition == null || entityDefinition.getAttachmentPoints() == null ? null
				: entityDefinition.getAttachmentPoints().get(node);
		return place;
	}

	protected void attachNameplate(BitmapText playerText) {
		if (skeletonControl == null)
			super.attachNameplate(playerText);
		else
			attachToBone(AttachmentPoint.NAMEPLATE, playerText);
	}

	private void attachForearms() {
		// Seems a bit weird these are not defined in CreatureDef.
		String suffix = bodySizeSuffix;
		if (BodyType.ROBED.equals(armBodyType)) {
			suffix += "-Robed";
		}

		// Left
		ExtendedOgreMeshKey leftKey = new ExtendedOgreMeshKey(Icescene.checkAssetPath(
				String.format("%1$s/%2$s-%3$s%4$s/%2$s-%3$s%4$s-Forearm_Left.mesh", path, TYPE, genderName, suffix))) {

			@Override
			public void configureMaterialKey(ExtendedMaterialListKey mk) {
				mk.setCache(false);
				if(fixedLighting != null)
					mk.setLighting(fixedLighting);
				mk.setLitMaterialDef("MatDefs/MOB_Lit.j3md");
				mk.setUnlitMaterialDef("MatDefs/MOB_Unlit.j3md");
			}
		};
		leftModel = context.getAssetManager().loadModel(leftKey);
		skeletonControl.getAttachmentsNode("Bone-LeftForearm").attachChild(leftModel);

		// Left
		ExtendedOgreMeshKey rightKey = new ExtendedOgreMeshKey(Icescene.checkAssetPath(
				String.format("%1$s/%2$s-%3$s%4$s/%2$s-%3$s%4$s-Forearm_Right.mesh", path, TYPE, genderName, suffix))) {

			@Override
			public void configureMaterialKey(ExtendedMaterialListKey mk) {
				mk.setCache(false);
				if(fixedLighting != null)
					mk.setLighting(fixedLighting);
				mk.setLitMaterialDef("MatDefs/MOB_Lit.j3md");
				mk.setUnlitMaterialDef("MatDefs/MOB_Unlit.j3md");
			}
		};

		rightModel = context.getAssetManager().loadModel(rightKey);
		skeletonControl.getAttachmentsNode("Bone-RightForearm").attachChild(rightModel);
	}

	private void attachHead() {
		// Determine the Head to use
		head = entityDefinition.getHeads().get(creature.getAppearance().getHead().getCode());
		ExtendedOgreMeshKey headKey = new ExtendedOgreMeshKey(Icescene
				.checkAssetPath(String.format("%1$s/%2$s-%3$s_%4$s/%5$s.mesh", path, TYPE, raceName, genderName, head.getMesh()))) {

			@Override
			public void configureMaterialKey(ExtendedMaterialListKey mk) {
				mk.setCache(false);
				if(fixedLighting != null)
					mk.setLighting(fixedLighting);
				mk.setLitMaterialDef("MatDefs/MOB_Lit.j3md");
				mk.setUnlitMaterialDef("MatDefs/MOB_Unlit.j3md");
			}
		};
		Node headAttach = skeletonControl.getAttachmentsNode("Bone-Head");
		if (headAttach == null) {
			LOG.warning(String.format("No Bone-Head for head attachment"));
		} else {
			headModel = context.getAssetManager().loadModel(headKey);
			headAttach.attachChild(headModel);
		}
	}

	private void attachDetails() {
		// Add details details
		AttachmentPointSet set = attachmentPoints.getSet(appearance);
		detailSpatials.clear();
		Details creatureDetails = entityDefinition.getDetails();
		LOG.info(String.format("Creature %s has %d details (%s)", getCreature().getDisplayName(), creatureDetails.size(),
				creatureDetails));

		for (Map.Entry<DetailType, DetailVariants> den : creatureDetails.entrySet()) {

			// Only 'def' exists?
			DetailVariants variants = den.getValue();
			DetailElements var = variants.get("def");
			for (Detail detail : var) {
				final String detailMeshPath = Icescene.checkAssetPath(
						String.format("%1$s/%2$s-%3$s_%4$s/%5$s.mesh", path, TYPE, raceName, genderName, detail.getMesh()));

				ExtendedOgreMeshKey detailKey = new ExtendedOgreMeshKey(detailMeshPath) {
					@Override
					public void configureMaterialKey(ExtendedMaterialListKey mk) {
						mk.setCache(false);
						if(fixedLighting != null)
							mk.setLighting(fixedLighting);
						mk.setLitMaterialDef("MatDefs/MOB_Lit.j3md");
						mk.setUnlitMaterialDef("MatDefs/MOB_Unlit.j3md");
					}
				};

				/*
				 * Check the attachment points of any attachments, as they may
				 * be configured to hide either the ears, tail or horns. NOTE,
				 * while in theory ears may be hidden separately, in practice
				 * they are both hidden when using the 'helmet' attachment point
				 * only. Hiding of tails is new for capes.
				 */
				boolean hide = false;
				for (AttachmentItem attachment : allAttachments.keySet()) {
					AttachmentPoint node = attachment.getNode();
					if (node == null) {
						node = getDefaultNode(attachment.getKey(), null);
					}
					if (node == null) {
						LOG.warning(String.format("Could not determine attachment point for %s", attachment.getKey()));
					} else {
						AttachmentPlace place = set.getPlaces().get(node);
						if (place != null) {
							List<String> hidden = place.getHidden();
							if (hidden != null && ((hidden.contains("tail") && den.getKey().equals(DetailType.TAIL))
									|| ((hidden.contains("left_ear") || hidden.contains("right_ear"))
											&& den.getKey().equals(DetailType.EARS)))) {
								hide = true;
								break;
							}
						}
						if (hide) {
							break;
						}
					}
				}

				//
				if (hide) {
					LOG.info(String.format("Hiding %s because an attachment requested this.", den.getKey()));
					continue;
				}

				Spatial detailSpatial = context.getAssetManager().loadModel(detailKey);

				// Some special IceClient only properties I hope to support
				switch (den.getKey()) {
				case EARS:
					detailSpatial.setLocalScale(creature.getAppearance().getEarSize());
					break;
				case TAIL:
					detailSpatial.setLocalScale(creature.getAppearance().getTailSize());
					break;
				default:
					break;
				}

				attachToBone(detail.getPoint(), detailSpatial);
				List<Spatial> s = detailSpatials.get(den.getKey());
				if (s == null) {
					s = new ArrayList<Spatial>();
					detailSpatials.put(den.getKey(), s);
				}
				s.add(detailSpatial);
			}
		}
	}

	// public void setAppearanceFromInventory(InventoryAndEquipment inventory) {
	// itemAppearance.clear();
	// for (InventoryAndEquipment.EquipmentItem s : inventory.getEquipment()) {
	// if (s.getItem() != null && s.getSlot().isBasic()) {
	// itemAppearance.put(s.getSlot(), s.getItem().getAppearance());
	// }
	// }
	// }
	private void addClothingTexture(ClothingTemplateKey clothingType, Appearance.ClothingType slot, List<RGB> itemColours) {
		ClothingTemplate itemDefinition = clothingDef.get(clothingType);
		if (itemDefinition == null) {
			LOG.severe(String.format("No item appearance for %s (%s)", slot, clothingType));
		} else {
			if (slot.equals(Appearance.ClothingType.ARMS)) {
				armBodyType = itemDefinition.getBodyType();
			} else if (slot.equals(Appearance.ClothingType.LEGGINGS)) {
				bodyType = itemDefinition.getBodyType();
			}
			Region region = slot.toEquipType().toRegion();
			String tex = itemDefinition.getRegions().get(region);
			if (tex == null) {
				LOG.severe(String.format("Region %s in clothing definition for %s (%s)", region, slot, clothingType));
			} else {
				ClothingTextureKey texKey = new ClothingTextureKey(clothingType.getName(), tex, region);
				ClothingTexture clothingTexture = clothingTextures.get(texKey);
				if (clothingTexture == null) {
					clothingTexture = new ClothingTexture(context.getAssetManager(), texKey, itemDefinition, itemColours);
					clothingTextures.put(texKey, clothingTexture);
				}
			}
		}
	}
}
