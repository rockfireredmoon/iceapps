package org.icescene.clutter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.IcesceneApp;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainQuad;

public abstract class TerrainClutterGrid extends Node {

	private static final Logger LOG = Logger.getLogger(TerrainClutterGrid.class.getName());
	private Vector3f view; // the world location we are looking at
	private Vector2f viewTile = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE); // the
																				// clutter
																				// tile
																				// within
																				// the
																				// terrain
																				// tile
																				// we
																				// are
																				// current
																				// centered
																				// areay
	private Vector2f lastViewTile; // the last clutter tile within the terrain
									// tile we were looking at
	private final TerrainClutterHandler handler;
	private final TerrainClutterLoader loader;
	private float terrainCellSize;
	private Vector2f terrainCell = new Vector2f(); // the current terrain cell.
	private Vector2f withinCenter;
	private float tileSize;
	private int radius;

	public TerrainClutterGrid(IcesceneApp app, TerrainClutterHandler handler, int radius) {
		this.radius = radius;
		this.handler = handler;

		addControl(new ClutterCellControl());

		loader = new TerrainClutterLoader(app, this, handler);
		loader.setWorld(this);
	}

	public TerrainClutterLoader getLoader() {
		return loader;
	}

	public TerrainClutterGrid(IcesceneApp app, TerrainClutterHandler handler, int radius, float terrainCellSize, int clutterCells) {
		this(app, handler, radius);
		this.terrainCellSize = terrainCellSize;

		tileSize = terrainCellSize / (float) clutterCells;
	}

	public TerrainClutterGrid(IcesceneApp app, TerrainGrid grid, TerrainClutterHandler handler, int clutterCells, int radius) {
		this(app, handler, radius, ((float) grid.getTotalSize() - 1f) / 2f, clutterCells);
	}

	public Vector2f getTerrainCell() {
		return terrainCell;
	}

	public void setTileSize(float tileSize) {
		this.tileSize = tileSize;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public float getTerrainCellSize() {
		return terrainCellSize;
	}

	public void setTerrainCellSize(float terrainCellSize) {
		this.terrainCellSize = terrainCellSize;
	}

	public float getTileSize() {
		return tileSize;
	}

	public int getRadius() {
		return radius;
	}

	public void reload() {
		if (getControl(ClutterCellControl.class) != null) {
			lastViewTile = null;
			viewTile.x = viewTile.y = Float.MIN_VALUE;
			checkForViewTileChange();
		} else {
			LOG.warning("Something tried to reload clutter after it has been closed.");
		}
	}

	public void reloadTiles() {

		if (viewTile.x > Float.MIN_VALUE && viewTile.y > Float.MIN_VALUE) {

			List<TerrainClutterTile> w = new ArrayList<TerrainClutterTile>();

			for (int y = -radius; y <= radius; y += 1) {
				for (int x = -radius; x <= radius; x += 1) {
					final Vector2f inView = new Vector2f((viewTile.x + x) * tileSize, (viewTile.y + y) * tileSize);
					final Vector2f inWorld = new Vector2f(inView.x + (terrainCell.x * terrainCellSize), inView.y
							+ (terrainCell.y * terrainCellSize));
					final Vector2f t = new Vector2f(Math.round(inWorld.x / terrainCellSize),
							Math.round(inWorld.y / terrainCellSize));
					final Vector2f v = new Vector2f(Math.round((inWorld.x - (t.x * terrainCellSize)) / tileSize),
							Math.round((inWorld.y - (t.y * terrainCellSize)) / tileSize));

					final TerrainQuad quad = handler.getTerrainAt(this, inWorld);
					if (quad != null) {
						TerrainClutterTile tile = new TerrainClutterTile(quad, v, t);
						loader.load(tile);
						w.add(tile);
					} else {
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("No Terrain at %s (%s, %s), will not load clutter here", t, v, inWorld));
						}
					}
				}
			}

			// Unload everything that is now out of the radius
			for (TerrainClutter el : loader.getLoaded()) {
				TerrainClutterTile pl = el.getTileLocation();
				if (!w.contains(pl)) {
					loader.unload(el.getTileLocation());
				}
			}
		} else {
			LOG.warning(String.format("Terrain is not yet ready at %f, %f.", viewTile.x, viewTile.y));
		}
	}

	public void checkForViewTileChange() {
		view = getViewWorldTranslation();
		if (view != null) {
			view = view.subtract(new Vector3f(tileSize, 0, tileSize));

			terrainCell.x = (int) (view.x / terrainCellSize);
			terrainCell.y = (int) (view.z / terrainCellSize);

			withinCenter = new Vector2f(view.x - (terrainCell.x * terrainCellSize), view.z - (terrainCell.y * terrainCellSize));

			viewTile.x = (int) (withinCenter.x / tileSize);
			viewTile.y = (int) (withinCenter.y / tileSize);
			if (lastViewTile == null || !lastViewTile.equals(viewTile)) {
				reloadTiles();
				lastViewTile = viewTile.clone();
			}
		} else {
			LOG.warning("View position is not known, can't determine if clutter should be loaded");
		}
	}

	/**
	 * Return the location the view should be centered around
	 *
	 * @return view
	 */
	protected abstract Vector3f getViewWorldTranslation();

	public void reset() {
		LOG.info("Resetting clutter grid");
		loader.unloadAll();
		lastViewTile = null;
		checkForViewTileChange();
	}

	/**
	 * Unload all clutter and stop loading any more
	 */
	public void close() {
		removeControl(ClutterCellControl.class);
		loader.close();
	}

	class ClutterCellControl extends AbstractControl {

		@Override
		protected void controlUpdate(float tpf) {
			checkForViewTileChange();
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}
	}
}
