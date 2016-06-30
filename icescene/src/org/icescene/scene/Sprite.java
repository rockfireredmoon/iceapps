package org.icescene.scene;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

/**
 * Simple sprite geometry used for stuff like minimap markers.
 */
public class Sprite extends Geometry {

	private float cellWidth;
	private float cellHeight;

	public Sprite(AssetManager assetManager, String texturePath, int row, int col, float cellWidth, float cellHeight) {
		this(null, assetManager, texturePath, row, col, cellWidth, cellHeight);
	}

	public Sprite(String name, AssetManager assetManager, String texturePath, int row, int col, float cellWidth, float cellHeight) {
		super(name, new Quad(1, 1));

		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;

		// Default to size of cell
		setLocalScale(cellWidth, cellHeight, 1f);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		final Texture tex = assetManager.loadTexture(texturePath);
		// tex.setMagFilter(Texture.MagFilter.Nearest);
		// tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
		mat.setTexture("ColorMap", tex);
		mat.setBoolean("SeparateTexCoord", true);
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		setMaterial(mat);

		float tw = tex.getImage().getWidth();
		float th = tex.getImage().getHeight();
		float left = (cellWidth * col) / tw;
		float right = ((cellWidth * (col + 1f))) / tw;
		float top = (th - (((row + 1f) * cellHeight))) / th;
		float bottom = (th - (row * cellHeight)) / th;

		VertexBuffer uv = getMesh().getBuffer(VertexBuffer.Type.TexCoord);
		Vector2f[] tc = new Vector2f[] { new Vector2f(left, top), new Vector2f(right, top), new Vector2f(right, bottom),
				new Vector2f(left, bottom), };

		uv.updateData(BufferUtils.createFloatBuffer(tc));
	}

	public float getCellWidth() {
		return cellWidth;
	}

	public float getCellHeight() {
		return cellHeight;
	}
}
