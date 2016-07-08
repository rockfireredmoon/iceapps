package org.icescene.entities;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.icelib.AbstractCreature;
import org.icescene.configuration.creatures.AbstractCreatureDefinition;
import org.icescene.controls.AnimationHandler;
import org.icescene.props.PropUserDataBuilder;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

public abstract class AbstractSpawnEntity extends AbstractLoadableEntity {

	private final static Logger LOG = Logger.getLogger(AbstractSpawnEntity.class.getName());
	public final static String CLOTHING_PATH = "Armor";
	protected Skeleton skel;
	protected SkeletonControl skeletonControl;
	protected Spatial bodyMesh;
	protected AbstractCreature creature;
	// private BuildableControl buildableControl;
	protected final String path;
	protected Set<Material> materials = new LinkedHashSet<Material>();

	public AbstractSpawnEntity(EntityContext context, final AbstractCreature creature, String path, String entityInstanceId) {
		super(context, entityInstanceId, creature.getAppearance());

		this.path = path;
		this.creature = creature;

		PropUserDataBuilder.setSelectable(spatial, true);
	}
	
	public String getPath() {
		return path;
	}

	/**
	 * Reload this spatial using the {@link EntityLoader}. This method will
	 * return immediately.
	 */
	public final void reload() {
		if (creatureFactory == null) {
			throw new IllegalStateException("CreatureFactory is not set.");
		}
		creatureFactory.reload(this);
	}

	@Override
	protected final void doLoad() throws Exception {

		onBeforeSpawnLoad();

		// All creatures are buildable
		// buildableControl = new BuildableControl(assetManager) {
		// @Override
		// protected void onApply(BuildableControl actualBuildable) {
		// AbstractSpawnSpatial cs = (AbstractSpawnSpatial)
		// actualBuildable.getSpatial();
		// try {
		// stateManager.getState(NetworkAppState.class).sendCreatureUpdate(cs.getCreature());
		// } catch (NetworkException ex) {
		// LOG.log(Level.SEVERE, "Could not apply creature update.", ex);
		// }
		// }
		// };
		//

		// Sub-class hook
		onSpawnLoad();

		// Initial size
		updateSize();

		// TODO spawn in wrong place when no sleep
		// Thread.sleep(SceneConstants.CREATURE_LOAD_DELAY);
	}

	/**
	 * Invoked when after the body material has been created.
	 */
	protected final void onMaterial(Material material) {
		materials.add(material);
		onSpawnMaterial(material);
	}

	@Override
	protected final void doLoadScene() {
		// addControl(buildableControl);
		spatial.attachChild(bodyMesh);
	}

	@Override
	protected final void doCleanUpScene() {
		bodyMesh.removeFromParent();
		onCleanUpScene();
		materials.clear();
		// removeControl(BuildableControl.class);
	}

	@Override
	protected final void doCleanUp() {
	}

	protected void onCleanUpScene() {
		// For sub-classes to override, for any scene thread unloading
	}

	protected void onBeforeSpawnLoad() throws Exception {
		// For sub-classes to override, for any non-scene thread loading
	}

	protected void onSpawnMaterial(Material material) {
		// For sub-classes to override, to configure the body material
	}

	protected void onSpawnLoad() throws Exception {
		// For sub-classes to override, for any non-scene thread loading
	}

	public Spatial getBodyMesh() {
		return bodyMesh;
	}

	public void updateSize() {
		LOG.info(String.format("Updating scale to %.3f", (float) creature.getAppearance().getSize()));
		bodyMesh.setLocalScale((float) creature.getAppearance().getSize());
	}

	public AnimControl getAnimControl() {
		return bodyMesh == null ? null : bodyMesh.getControl(AnimControl.class);
	}

	public AnimationHandler<? extends AbstractCreatureDefinition, AbstractCreatureEntity<? extends AbstractCreatureDefinition>> getAnimationHandler() {
		return spatial.getControl(AnimationHandler.class);
	}

	// protected final Material textureBodyPart(String meshPath, String part,
	// String raceName, String genderName) {
	// MeshConfiguration cfg = MeshConfiguration.get(meshPath);
	// return textureBodyPart(MaterialFactory.getManager().getMaterial(cfg,
	// assetManager), part, raceName, genderName);
	// }
	// protected abstract Material textureBodyPart(Material material, String
	// part, String raceName, String genderName);
	// protected final Material textureBodyPart(Material material, String part,
	// String raceName, String genderName) {
	// String tintColorMapPath =
	// Icescene.checkAssetPath(String.format("%1$s/%2$s-%3$s_%4$s/%5$s-Tint.png.colormap",
	// path, type, raceName, genderName, part));
	// String tintPath =
	// Icescene.checkAssetPath(String.format("%1$s/%2$s-%3$s_%4$s/%5$s-Tint.png",
	// path, type, raceName, genderName, part));
	// String texturePath =
	// Icescene.checkAssetPath(String.format("%1$s/%2$s-%3$s_%4$s/%5$s.png",
	// path, type, raceName, genderName, part));
	// return texture(tintColorMapPath, tintPath, texturePath, material);
	// }
	public AbstractCreature getCreature() {
		return creature;
	}

	protected AnimationHandler<?, ?> createAnimationHandler() {
		throw new UnsupportedOperationException();
	}

	public void reloadSkin() {

	}

	// @Override
	// protected Material texture(String tintColorMapPath, String tintPath,
	// String texturePath) {
	// Material material = super.texture(tintColorMapPath, tintPath,
	// texturePath);
	// if (Config.get().getBoolean(Config.SCENE_LIT_CREATURES,
	// Config.SCENE_LIT_CREATURES_DEFAULT)) {
	// material.setFloat("Shininess", 10f);
	// }
	// return material;
	// }
}
