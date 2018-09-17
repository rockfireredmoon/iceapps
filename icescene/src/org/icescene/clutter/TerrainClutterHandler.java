package org.icescene.clutter;

import java.util.Collection;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;

public interface TerrainClutterHandler {

	/**
	 * Get the terrain quad at the given world location.
	 *
	 * @param world
	 *            world location
	 * @return terrain quad
	 */
	TerrainQuad getTerrainAt(TerrainClutterGrid grid, Vector2f world);

	/**
	 * Create the layers for the tile. The tile will be a subdivision of the
	 * {@link TerrainQuad} currently at the centre. Each layer will have it's
	 * geometries optimised.
	 *
	 * @param tile
	 *            tile
	 * @param world
	 *            world position of tile
	 * @return layers
	 */
	Collection<Node> createLayers(TerrainClutterGrid grid, TerrainClutterTile tile, Vector3f world);

}
