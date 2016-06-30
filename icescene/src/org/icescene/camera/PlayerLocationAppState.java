package org.icescene.camera;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.icelib.PageLocation;
import org.icescene.IcemoonAppState;

import com.google.common.base.Objects;
import com.jme3.math.Vector3f;

/**
 * Manages the players current location and tile for several different tile
 * loading appstates. Several components of the
 * scene are managed in tiles. They often need to know when the player or camera
 * location changes so they can load new tiles and unload superflous ones.
 */
public class PlayerLocationAppState extends IcemoonAppState<IcemoonAppState<?>> {

	public interface TileSource {

		PageLocation viewToTile(Vector3f viewLocation);

		void viewChanged(Vector3f viewLocation);

		void tileChanged(Vector3f viewLocation, PageLocation tile);
	}

	private Map<TileSource, PageLocation> current = new HashMap<>();
	private Vector3f lastLocation;

	public PlayerLocationAppState(Preferences prefs) {
		super(prefs);
	}

	public void addTileSource(TileSource source) {
		if (current.containsKey(source)) {
			throw new IllegalStateException("Tile source already registered.");
		}
		current.put(source, null);
	}

	public void removeTileSource(TileSource source) {
		if (!current.containsKey(source)) {
			throw new IllegalStateException("Tile source not registered.");
		}
		current.remove(source);
	}

	public Vector3f getViewLocation() {
		return lastLocation;
	}

	public void updateViewLocation(Vector3f viewLocation) {

		if (!Objects.equal(lastLocation, viewLocation)) {
			this.lastLocation = viewLocation;
			for (Map.Entry<TileSource, PageLocation> en : current.entrySet()) {
				PageLocation loc = en.getKey().viewToTile(viewLocation);
				if (!Objects.equal(loc, en.getValue())) {
					en.getKey().tileChanged(viewLocation, loc);
				} else {
					en.getKey().viewChanged(viewLocation);
				}
			}
		}
	}

}
