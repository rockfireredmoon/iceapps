package org.icescene.clutter;

import static java.lang.String.format;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import jme3tools.optimize.GeometryBatchFactory;

import org.icescene.IcesceneApp;
import org.icescene.scene.AbstractSceneQueue;

import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

public class TerrainClutterLoader extends AbstractSceneQueue<TerrainClutterTile, TerrainClutter> {

    private static final Logger LOG = Logger.getLogger(TerrainClutterLoader.class.getName());
    private final TerrainClutterHandler handler;
    private Node world;
    private final TerrainClutterGrid grid;

    public TerrainClutterLoader(IcesceneApp app, TerrainClutterGrid grid, TerrainClutterHandler handler) {
        super(app);
        this.grid = grid;
        LOG.info(String.format("Starting clutter load for tile size of %s and radius of %f", grid.getTileSize(), grid.getTileSize()));
        this.handler = handler;
    }

    public Node getWorld() {
        return world;
    }

    void setWorld(Node world) {
        this.world = world;
    }

    @Override
    public String getTaskName(TerrainClutterTile key) {
        return String.format("Clutter for %3.0f, %3.0f (%3.0f, %3.0f)", key.getTerrainCell().x, key.getTerrainCell().y,
                key.getTile().x, key.getTile().y);
    }

    @Override
    protected TerrainClutter doReload(final TerrainClutterTile page) {
        final TerrainClutter pageInstance = new TerrainClutter(page);
        final Vector3f worldTranslation = new Vector3f((page.getTerrainCell().x * grid.getTerrainCellSize()) + (page.getTile().x * grid.getTileSize()),
                0,
                (page.getTerrainCell().y * grid.getTerrainCellSize()) + (page.getTile().y * grid.getTileSize()));

        pageInstance.setLocalTranslation(worldTranslation);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Loading clutter for %s", page));
        }
        
        try {
            for (final Node s : handler.createLayers(grid, page, worldTranslation)) {
                pageInstance.attachChild(s);
            }
            GeometryBatchFactory.optimize(pageInstance);
            pageInstance.setQueueBucket(RenderQueue.Bucket.Transparent);
            app.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    if(!pageInstance.isUnloaded()) {
                        world.attachChild(pageInstance);
                    }
                    return null;
                }
            });

        } catch (Exception e) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.SEVERE, format("Failed to load clutter for %s", page), e);
            } else {
                LOG.log(Level.SEVERE, format("Failed to load clutter for %s. %s", page, e.getMessage()));
            }
        }
        return pageInstance;
    }

    @Override
    protected TerrainClutter doUnload(TerrainClutter instance) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Unloading clutter spatials for %s", instance.getTileLocation()));
        }
        instance.removeFromParent();
        return instance;
    }
}
