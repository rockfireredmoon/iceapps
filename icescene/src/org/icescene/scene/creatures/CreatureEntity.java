package org.icescene.scene.creatures;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.icelib.AbstractCreature;
import org.icelib.AttachmentPoint;
import org.icescene.Icescene;
import org.icescene.ServiceRef;
import org.icescene.configuration.attachments.AttachmentPlace;
import org.icescene.configuration.creatures.CreatureKey;
import org.icescene.configuration.creatures.ModelDef;
import org.icescene.configuration.creatures.ModelDefinition;
import org.icescene.controls.AnimationHandler;
import org.icescene.controls.HordeAnimationHandler;
import org.icescene.controls.HordeBipedAnimationHandler;
import org.icescene.entities.AbstractCreatureEntity;
import org.icescene.entities.EntityContext;

import com.jme3.font.BitmapText;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;

public class CreatureEntity extends AbstractCreatureEntity<ModelDefinition> {

	private String bodyTemplate;

	@ServiceRef
	protected static ModelDef modelDef;

	public CreatureEntity(EntityContext context, AbstractCreature creature, String path, String entityInstanceId) {
		super(context, creature, path, entityInstanceId);
	}

	protected void attachNameplate(BitmapText playerText) {
		if (skeletonControl == null)
			super.attachNameplate(playerText);
		else
			attachToBone(AttachmentPoint.NAMEPLATE, playerText);
	}

	protected AnimationHandler<ModelDefinition, CreatureEntity> createAnimationHandler() {
		if ("Horde".equals(entityDefinition.getAnimationHandler())) {
			return new HordeAnimationHandler(this, context);
		} else if ("Biped".equals(entityDefinition.getAnimationHandler())) {
			return new HordeBipedAnimationHandler(this, context);
		}
		throw new UnsupportedOperationException(String.format("Unknown animation handler %s",
				entityDefinition.getAnimationHandler()));
	}

	// @Override
	// public Collection<AnimationSequence> getAnimationPresets() {
	// if (entityDefinition != null) {
	// List<AnimationSequence> s = new ArrayList<AnimationSequence>();
	// for (Map.Entry<String, AnimationOption> o :
	// entityDefinition.getAnimations().entrySet()) {
	// AnimationSequence seq = new AnimationSequence(o.getKey());
	// seq.setSounds(o.getValue().getSounds().values());
	//
	// Anim anim = new Anim(SubGroup.NONE, Part.ALL, o.getKey());
	//
	// // TODO a better way?
	// if (DEFAULT_LOOP_ANIMS.contains(o.getKey().toLowerCase())) {
	// anim.setLoopMode(LoopMode.Loop);
	// }
	//
	// seq.addAnim(anim);
	// s.add(seq);
	// }
	// return s;
	// }
	// return Collections.emptyList();
	// }

	@Override
	protected AttachmentPlace getAttachmentPoint(AttachmentPoint node) {
		AttachmentPlace place = entityDefinition.getAttachmentPointSet().getPlaces().get(node);
		return place;
	}

	@Override
	protected final void onBeforeCreatureLoad() throws Exception {
		bodyTemplate = creature.getAppearance().getBodyTemplate();

		// Get the static creature definition
		CreatureKey key = new CreatureKey(bodyTemplate);
		entityDefinition = modelDef.get(key);
		if (entityDefinition == null) {
			throw new IllegalArgumentException(String.format("No creature definition for %s", key));
		}
	}

	@Override
	protected final void onSpawnMaterial(Material material) {
		String diffuseMatParamName = getDiffuseParamName(material);
		MatParamTexture diffuseMapParam = material.getTextureParam(diffuseMatParamName);
		String texturePath = diffuseMapParam == null || diffuseMapParam.getTextureValue() == null
				|| diffuseMapParam.getTextureValue().getImage() == null || diffuseMapParam.getTextureValue().getKey() == null ? Icescene
				.checkAssetPath(String.format("%1$s/%2$s/%2$s.png", path, bodyTemplate)) : diffuseMapParam.getTextureValue()
				.getKey().getName();
		texturePart(material, FilenameUtils.getBaseName(texturePath), texturePath);
	}

	@Override
	protected String getCreatureMeshPath() {
		String mesh = entityDefinition.getMesh();
		String thisPath = Icescene.checkAssetPath(String.format("%1$s/%2$s/%2$s.mesh", path, bodyTemplate));
		if (StringUtils.isBlank(mesh)) {
			return thisPath;
		} else {
			if (mesh.startsWith("../")) {
				String rel = FilenameUtils.getPath(thisPath);
				return Icescene.checkAssetPath(FilenameUtils.normalize(rel + mesh) + ".mesh");
			} else if (!mesh.contains("/")) {
				return Icescene.checkAssetPath(String.format("%1$s/%2$s/%3$s.mesh", path, bodyTemplate, mesh));
			} else {
				return Icescene.checkAssetPath(mesh + ".mesh.xml");
			}
		}
	}

	protected final Material textureBodyPart(Material material, String part) {

		String suffix = "";
		if (part != null) {
			suffix = "-" + part;
		}
		String name = String.format("%1$s/%2$s", bodyTemplate, suffix);
		String texturePath = Icescene.checkAssetPath(String.format("%1$s/%2$s/%3$s.png", path, bodyTemplate, name));
		return texturePart(material, name, texturePath);
	}

	protected final Material texturePart(Material material, String name, String texturePath) {
		// Icelib.removeMe("texturePath(%s,%s)", name, texturePath);
		String tintColorMapPath = Icescene.checkAssetPath(String.format("%1$s/%2$s/%3$s-Tint.png.colormap", path, bodyTemplate,
				name));
		String tintPath = Icescene.checkAssetPath(String.format("%1$s/%2$s/%3$s-Tint.png", path, bodyTemplate, name));
		return texture(tintColorMapPath, tintPath, texturePath, material);
	}
}
