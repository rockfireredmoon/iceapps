package org.icescene.entities;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Appearance;
import org.icescene.Icescene;
import org.icescene.assets.ExtendedOgreMeshKey;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.BillboardControl;

/**
 * For spatials that may take a while to load. Initially, the dreaded "Cog" is
 * attached. The load method is called by creature loader (which is a thread
 * pool). When the load is completed, the cog will be detached and all queued
 * controls and spatials are attached.
 */
public abstract class AbstractLoadableEntity extends AbstractEntityWithAppearance {

	public enum When {

		BEFORE_MODEL_LOADED, AFTER_MODEL_LOADED, BEFORE_MODEL_UNLOADED, AFTER_MODEL_UNLOADED, BEFORE_SCENE_LOADED, AFTER_SCENE_LOADED, BEFORE_SCENE_UNLOADED, AFTER_SCENE_UNLOADED, BEFORE_DESTROY, AFTER_DESTROY
	}

	protected EntityLoader creatureFactory;
	private Spatial errorSpatial;
	private Exception error;
	private static final Logger LOG = Logger.getLogger(AbstractLoadableEntity.class.getName());
	private boolean loading;
	private Spatial loadingSpatial;
	private boolean loaded;
	private boolean loadedScene;
	private final Map<When, List<Callable<?>>> hooks = new EnumMap<When, List<Callable<?>>>(When.class);

	public AbstractLoadableEntity(EntityContext context, String name, Appearance appearance) {
		super(context, name, appearance);
	}

	// TODO - Temporary until I can figure out animations
	class CogRotator extends AbstractControl {

		@Override
		protected void controlUpdate(float tpf) {
			spatial.rotate(0, tpf * 3, 0);
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}
	}

	/**
	 * Indicate the spatial failed to load by attaching the default error model.
	 * Setting error of <code>null</code> will indicate the error model should
	 * be removed.
	 * 
	 * @param error
	 *            error
	 */
	public final void error(Exception error) {
		if (!Objects.equals(error, this.error)) {
			if (error != null) {
				setLoading(false);
			}
			if (error != null) {
				String meshPath = Icescene.checkAssetPath("Manipulator/Manipulator-QuestionMark.mesh.xml");
				// errorSpatial = assetManager.loadModel(new
				// OgreMeshKey(meshPath, new
				// DynamicMaterialList(assetManager)));
				setNameplateText(error.getMessage());
				setShowingNameplate(true);
				addNameplate();
				errorSpatial = context.getAssetManager().loadModel(new ExtendedOgreMeshKey(meshPath));
				// errorSpatial.move(0, 7, 0);
				errorSpatial.addControl(new BillboardControl());
				errorSpatial.rotate(0, FastMath.HALF_PI, 0);
				spatial.attachChild(errorSpatial);

			} else {
				errorSpatial.removeFromParent();
			}
			this.error = error;
		}
	}

	/**
	 * Set whether the loading cog should be displayed.
	 * 
	 * @param loading
	 *            whether loading or not
	 */
	public final void setLoading(boolean loading) {
		error(null);
		if (this.loading != loading) {
			if (loading) {
				String meshPath = Icescene.checkAssetPath("Manipulator/Manipulator-Creature_Load.mesh.xml");
				loadingSpatial = context.getAssetManager().loadModel(new ExtendedOgreMeshKey(meshPath));
				SkeletonControl skeletonControl = loadingSpatial.getControl(SkeletonControl.class);
				if (skeletonControl == null) {
					LOG.warning("No skeleton for loading cog?");
				} else {
					AnimControl anim = loadingSpatial.getControl(AnimControl.class);
					if (anim == null) {
						LOG.warning("No animation for loading cog?");
					} else {
						for (String a : anim.getAnimationNames()) {
							AnimChannel ch = anim.createChannel();
							ch.setAnim(a);
							ch.setLoopMode(LoopMode.Loop);
							break;
						}
					}
				}
				loadingSpatial.setQueueBucket(RenderQueue.Bucket.Transparent);
				loadingSpatial.move(0, 7, 0);
				loadingSpatial.addControl(new BillboardControl());
				spatial.attachChild(loadingSpatial);

			} else {
				loadingSpatial.removeFromParent();
			}
			this.loading = loading;
		}
	}

	public synchronized void invoke(When when, Callable<?> callable) {
		List<Callable<?>> l = hooks.get(when);
		if (l == null) {
			l = new ArrayList<Callable<?>>();
			hooks.put(when, l);
		}
		synchronized (l) {
			l.add(callable);
		}
	}

	public void setCreatureFactory(EntityLoader creatureFactory) {
		this.creatureFactory = creatureFactory;
	}

	public EntityLoader getCreatureFactory() {
		return creatureFactory;
	}

	@Override
	protected Node createNode() {
		return new Node() {
			@Override
			public boolean removeFromParent() {
				try {
					invoke(When.BEFORE_DESTROY);
				} catch (Exception ex) {
					LOG.log(Level.SEVERE, "Failed to run run BEFORE_DESTROY hooks for " + getName(), ex);
				}
				try {
					return super.removeFromParent();
				} finally {
					try {
						invoke(When.AFTER_DESTROY);
					} catch (Exception ex) {
						LOG.log(Level.SEVERE, "Failed to run run AFTER_DESTROY hooks for " + getName(), ex);
					}
				}
			}

			@Override
			protected void setParent(Node parent) {
				super.setParent(parent);
				if (parent == null) {
					try {
						unloadAndUnloadScene();
					} catch (Exception ex) {
						LOG.log(Level.SEVERE, "Failed to unload scene when detached from parent.", ex);
					}
				}
			}
		};
	}

	void invoke(When when) throws Exception {
		final List<Callable<?>> l = hooks.get(when);
		if (l != null) {
			synchronized (l) {
				for (Callable<?> c : l) {
					c.call();
				}
			}
		}
	}

	/**
	 * Load non-scene (done first).
	 * 
	 * @throws Exception
	 */
	public final void load() throws Exception {
		invoke(When.BEFORE_MODEL_LOADED);
		doLoad();
		// spatial.updateWorldBound();
		spatial.updateGeometricState();
		loaded = true;
		invoke(When.AFTER_MODEL_LOADED);
	}

	/**
	 * Load the scene
	 */
	public final void loadScene() throws Exception {
		invoke(When.BEFORE_SCENE_LOADED);
		loadedScene = true;
		doLoadScene();
		invoke(When.AFTER_SCENE_LOADED);
	}

	/**
	 * Load and attach in the same thread.
	 */
	public final void loadAndUpdateScene() throws Exception {
		load();
		loadScene();
	}

	/**
	 * Unlock and detach in the same thread.
	 */
	public final void unloadAndUnloadScene() throws Exception {
		unloadScene();
		unload();
	}

	/**
	 * Unload from scene
	 */
	public final void unloadScene() throws Exception {
		if (loadedScene) {
			invoke(When.BEFORE_SCENE_UNLOADED);
			LOG.info(String.format("Unloading scene for %s", this));
			doCleanUpScene();
			invoke(When.AFTER_SCENE_UNLOADED);
			loadedScene = false;
		}
	}

	/**
	 * Unload from scene
	 */
	public final void unload() throws Exception {
		if (loaded) {
			invoke(When.BEFORE_MODEL_UNLOADED);
			LOG.info(String.format("Unloading for %s", this));
			doCleanUp();
			loaded = false;
			invoke(When.AFTER_MODEL_UNLOADED);
		}
	}

	public Exception getLastError() {
		return error;
	}

	/**
	 * Get if currently in a "loading" state.
	 * 
	 * @return is loading
	 */
	public boolean isLoading() {
		return loading;
	}

	/**
	 * Get if the the spatial has been loaded.
	 * 
	 * @return loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Get if the scene has been loaded
	 * 
	 * @return loaded scene
	 */
	public boolean isLoadedScene() {
		return loadedScene;
	}

	/**
	 * Implement to do the actual loading. This will usually NOT be called on
	 * the scene thread, so no alteration to the scene should be done here
	 * (instead use ).
	 */
	protected abstract void doLoad() throws Exception;

	/**
	 * Implement to add anything loaded to the scene or anything else that must
	 * be done on the scene thread.
	 */
	protected abstract void doLoadScene();

	/**
	 * Implement to remove from the scene. Called on the scene thread.
	 */
	protected abstract void doCleanUpScene();

	/**
	 * Implement to clean up anything else (not on the scene thread).
	 */
	protected abstract void doCleanUp();
}
