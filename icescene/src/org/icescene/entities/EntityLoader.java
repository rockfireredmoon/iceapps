package org.icescene.entities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icelib.AbstractCreature;
import org.icelib.Appearance;
import org.icescene.Alarm;
import org.icescene.IcesceneApp;
import org.icescene.props.AbstractProp;
import org.icescene.props.ComponentManager;
import org.icescene.props.EntityFactory;
import org.icescene.scene.creatures.Biped;
import org.icescene.scene.creatures.CreatureEntity;
import org.icescene.scene.creatures.Prop;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;

/**
 * Responsible for loading creatures.
 */
public class EntityLoader implements EntityContext {

	private static final Logger LOG = Logger.getLogger(EntityLoader.class.getName());
	private final ExecutorService executor;
	private final SimpleApplication app;
	private final Map<AbstractSpawnEntity, Future<Void>> loading = new ConcurrentHashMap<AbstractSpawnEntity, Future<Void>>();
	private Map<String, AbstractSpawnEntity> entities = new LinkedHashMap<>();
	private final EntityFactory propFactory;
	private boolean stopExecutorOnClose = true;
	private boolean closed;
	private static long id;

	public EntityLoader(ExecutorService executor, SimpleApplication app, EntityFactory propFactory) {
		this.app = app;
		this.executor = executor;
		this.propFactory = propFactory;
	}

	public synchronized void reload(final AbstractSpawnEntity creatureSpatial) {
		LOG.info("Waiting to sync");
		synchronized (loading) {
			LOG.info("sync");
			final Future<Void> future = loading.get(creatureSpatial);
			if (future != null && future.isDone()) {
				LOG.info(String.format("Removing previous loaded for %s", creatureSpatial.getSpatial().getName()));
				loading.remove(creatureSpatial);
			}
			LOG.info(String.format("Queue load for %s", creatureSpatial.getSpatial().getName()));
			loading.put(creatureSpatial, executor.submit(new Callable<Void>() {
				@Override
				public String toString() {
					return creatureSpatial.getCreature().getDisplayName();
				}

				public Void call() throws Exception {
					if (future != null) {
						// Wait for previous reload to finish if there was one
						try {
							LOG.info(String.format("Waiting for previous reload to complete for %s", creatureSpatial));
							future.get();
						} catch (InterruptedException ex) {
							LOG.log(Level.SEVERE, "Interrupted waiting for previous reload to complete.", ex);
						} catch (ExecutionException ex) {
							LOG.log(Level.SEVERE, "Error waiting for previous reload to complete.", ex);
						}
					}
					doReload(creatureSpatial);
					return null;
				}
			}));
		}
	}

	@SuppressWarnings("unchecked")
	public AbstractSpawnEntity create(AbstractCreature player) {
		final Appearance appearance = player.getAppearance();
		AbstractSpawnEntity newInstance = null;
		String entityInstanceId = "Entity" + (id++);
		switch (appearance.getName()) {
		case N4:
			// Creature
			String bodyTemplate = player.getAppearance().getBodyTemplate();
			String path = "Boss";
			if (bodyTemplate.startsWith("Horde-")) {
				path = "Horde";
			}
			LOG.info(String.format("Creating creature spatial for %s", player.getAppearance().getBodyTemplate()));
			try {
				Class<? extends AbstractSpawnEntity> clazz = (Class<? extends AbstractSpawnEntity>) Class.forName(String.format(
						"%s.creatures.%s.%s", AbstractSpawnEntity.class.getPackage().getName(), path.toLowerCase(),
						bodyTemplate.replace("-", "")), false, AbstractSpawnEntity.class.getClassLoader());
				newInstance = clazz.getConstructor(EntityContext.class, AbstractCreature.class, String.class, String.class)
						.newInstance(this, player, path, entityInstanceId);
			} catch (ClassNotFoundException cnfe) {
				LOG.info(String.format("Using default %s for %s", path, bodyTemplate));
				newInstance = new CreatureEntity(this, player, path, entityInstanceId);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			newInstance.setCreatureFactory(this);
			break;
		case P1:
			String propName = player.getAppearance().getProp();
			LOG.info(String.format("Creating prop spatial for %s", propName));
			try {
				// AbstractProp prop = propFactory.findProp(propName);
				AbstractProp prop = propFactory.getProp(propName);
				newInstance = new Prop(this, player, prop, entityInstanceId);
				newInstance.setCreatureFactory(this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			break;
		default:
			// Bipeds
			LOG.info(String.format("Creating biped spatial for %s", player.getAppearance().getRace()));
			newInstance = new Biped(this, player, entityInstanceId);
			newInstance.setCreatureFactory(this);
			break;
		}
		entities.put(entityInstanceId, newInstance);
		return newInstance;
	}

	public boolean isStopExecutorOnClose() {
		return stopExecutorOnClose;
	}

	public void setStopExecutorOnClose(boolean stopExecutorOnClose) {
		this.stopExecutorOnClose = stopExecutorOnClose;
	}

	public void close() {
		if (closed) {
			throw new IllegalStateException("Already closed.");
		}
		closed = true;
		if (stopExecutorOnClose) {
			executor.shutdownNow();
		}
		for (AbstractSpawnEntity s : entities.values()) {
			if (s.isLoadedScene()) {
				try {
					s.unloadScene();
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Failed to unload from scene during close.");
				}
			}
			if (s.isLoaded()) {
				try {
					s.unload();
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Failed to unload during close.");
				}
			}
			s.getSpatial().removeFromParent();
		}
		entities.clear();
	}

	public void remove(final AbstractSpawnEntity entity) {
		entities.remove(entity.getSpatial().getName());
		executor.submit(new Callable<Void>() {
			public Void call() throws Exception {
				unloadCreatureFromScene(entity);
				unloadCreature(entity);
				app.enqueue(new Callable<Void>() {
					public Void call() throws Exception {
						entity.getSpatial().removeFromParent();
						return null;
					}
				});
				return null;
			}
		});
	}

	private void doReload(final AbstractLoadableEntity creatureSpatial) {
		try {
			LOG.info(String.format("Reloading %s", creatureSpatial));

			unloadCreatureFromScene(creatureSpatial);
			unloadCreature(creatureSpatial);

			// Show the cog
			if (!creatureSpatial.isLoading()) {
				LOG.info(String.format("Adding cog for %s", creatureSpatial));
				app.enqueue(new Callable<Void>() {
					public Void call() throws Exception {
						creatureSpatial.setLoading(true);
						return null;
					}
				}).get();
			}

			LOG.info(String.format("Loading for %s", creatureSpatial));
			creatureSpatial.load();

			// Finally add back to scene and remove cog
			LOG.info(String.format("Queuing loading scene for %s", creatureSpatial));
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					creatureSpatial.loadScene();
					creatureSpatial.setLoading(false);
					return null;
				}
			}).get();

		} catch (InterruptedException ie) {
			LOG.warning(String.format("Interrupted loading %s", creatureSpatial));
		} catch (final Exception ie) {
			LOG.log(Level.SEVERE, "Failed to load creature.", ie);
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					creatureSpatial.error(ie);
					return null;
				}
			});
		}
	}

	private void unloadCreatureFromScene(final AbstractLoadableEntity creatureSpatial) throws ExecutionException,
			InterruptedException {
		if (creatureSpatial.isLoadedScene()) {
			LOG.info(String.format("Queuing unloading from scene for %s", creatureSpatial));
			// First unload from scene and set as loading
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					creatureSpatial.unloadScene();
					return null;
				}
			}).get();
		}
	}

	private void unloadCreature(final AbstractLoadableEntity creatureSpatial) throws Exception {
		// Now unload / load
		if (creatureSpatial.isLoaded()) {
			LOG.info(String.format("Unloading for %s", creatureSpatial));
			creatureSpatial.unload();
		}
	}

	public AbstractSpawnEntity get(String name) {
		return entities.get(name);
	}

	@Override
	public AssetManager getAssetManager() {
		return app.getAssetManager();
	}

	@Override
	public Alarm getAlarm() {
		return ((IcesceneApp) app).getAlarm();
	}

	@Override
	public AppStateManager getStateManager() {
		return app.getStateManager();
	}

	@Override
	public Preferences getPreferences() {
		return ((IcesceneApp) app).getPreferences();
	}

	@Override
	public IcesceneApp getApp() {
		return (IcesceneApp) app;
	}

	@Override
	public ComponentManager getComponentManager() {
		return ((IcesceneApp) app).getComponentManager();
	}
}
