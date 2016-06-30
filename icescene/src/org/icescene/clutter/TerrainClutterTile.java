package org.icescene.clutter;

import com.jme3.math.Vector2f;
import com.jme3.terrain.geomipmap.TerrainQuad;

public class TerrainClutterTile {
    private TerrainQuad terrain;
    private Vector2f tile;
    private final Vector2f terrainCell;

    public TerrainClutterTile(TerrainQuad terrain, Vector2f tile, Vector2f terrainCell) {
        this.terrain = terrain;
        this.tile = tile;
        this.terrainCell = terrainCell;
    }

    public Vector2f getTerrainCell() {
        return terrainCell;
    }

    public TerrainQuad getTerrain() {
        return terrain;
    }

    public Vector2f getTile() {
        return tile;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.tile != null ? this.tile.hashCode() : 0);
        hash = 47 * hash + (this.terrainCell != null ? this.terrainCell.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TerrainClutterTile other = (TerrainClutterTile) obj;
        if (this.tile != other.tile && (this.tile == null || !this.tile.equals(other.tile))) {
            return false;
        }
        if (this.terrainCell != other.terrainCell && (this.terrainCell == null || !this.terrainCell.equals(other.terrainCell))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TerrainClutterTile{" + "terrain=" + terrain + ", tile=" + tile + ", terrainCell=" + terrainCell + '}';
    }
    
    
}
