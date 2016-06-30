package org.icescene.scene;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

/**
 * Simple animated sprite geometry used for stuff like ability buttons. Expects
 * a texture split up into equal size cells. Each of these cells is treated as a
 * frame. These can then be animated with a specified delay between each frame.
 */
public class AnimSprite extends Geometry {

	private List<AnimFrame> frames = new ArrayList<>();
	private float cellWidth;
	private float cellHeight;
	private int tw;
	private int th;
	private VertexBuffer uv;

	public AnimSprite(AssetManager assetManager, String texturePath, int cellSize) {
		this(null, assetManager, texturePath, cellSize, cellSize);
	}

	public AnimSprite(AssetManager assetManager, String texturePath, int cellWidth, int cellHeight) {
		this(null, assetManager, texturePath, cellWidth, cellHeight);
	}

	public AnimSprite(String name, AssetManager assetManager, String texturePath, int cellWidth, int cellHeight) {
		super(name, new Quad(1, 1));

		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;

		getLocalTranslation().z = Float.MAX_VALUE;

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

		tw = tex.getImage().getWidth();
		th = tex.getImage().getHeight();

		uv = getMesh().getBuffer(VertexBuffer.Type.TexCoord);
	}

	public void start(float timePerFrame) {
		if (frames.isEmpty()) {
			useAllFrames();
			if (frames.isEmpty()) {
				throw new IllegalStateException("No frames.");
			}
		}
		addControl(new AnimSpriteControl(timePerFrame));
	}

	public void useAllFrames() {
		frames.clear();
		for (int r = 0; r < th / cellHeight; r++) {
			addRow(r);
		}
	}

	public void addRow(int row) {
		for (int c = 0; c < tw / cellWidth; c++) {
			addFrame(row, c);
		}
	}

	public void addFrame(int row, int col) {
		frames.add(new AnimFrame(row, col));
	}

	public float getCellWidth() {
		return cellWidth;
	}

	public float getCellHeight() {
		return cellHeight;
	}

	class AnimFrame {
		private float left;
		private float right;
		private float bottom;
		private float top;

		AnimFrame(int row, int col) {
			left = (cellWidth * col) / tw;
			right = ((cellWidth * (col + 1f))) / tw;
			top = (th - (((row + 1f) * cellHeight))) / th;
			bottom = (th - (row * cellHeight)) / th;
		}

		void update(Vector2f[] v) {
			v[0].x = left;
			v[0].y = top;
			v[1].x = right;
			v[1].y = top;
			v[2].x = right;
			v[2].y = bottom;
			v[3].x = left;
			v[3].y = bottom;
		}
	}

	class AnimSpriteControl extends AbstractControl {

		private Vector2f[] tc = new Vector2f[] { new Vector2f(0, 0), new Vector2f(0, 0), new Vector2f(0, 0), new Vector2f(0, 0) };
		private int frameNo = 0;
		private float time = 0;
		private float timePerFrame;

		AnimSpriteControl(float timePerFrame) {
			this.timePerFrame = timePerFrame;
			uv = getMesh().getBuffer(VertexBuffer.Type.TexCoord);
		}

		@Override
		protected void controlRender(RenderManager arg0, ViewPort arg1) {
		}

		@Override
		protected void controlUpdate(float tpf) {
			time += tpf;
			if (time > timePerFrame) {
				frameNo++;
				if (frameNo >= frames.size()) {
					frameNo = 0;
				}
				time = 0;
			}
			frames.get(frameNo).update(tc);
			uv.updateData(BufferUtils.createFloatBuffer(tc));
		}

	}
}
