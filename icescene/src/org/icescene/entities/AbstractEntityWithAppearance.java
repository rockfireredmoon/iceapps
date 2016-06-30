package org.icescene.entities;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Appearance;
import org.icelib.Icelib;
import org.icelib.RGB;
import org.icescene.assets.ExtendedMaterialKey;
import org.icescene.assets.ImageLoader;
import org.icescene.assets.IndexedTextureKey;
import org.icescene.configuration.ColorMapConfiguration;
import org.icescene.scene.PaletteTexture;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.texture.Texture;

public abstract class AbstractEntityWithAppearance extends AbstractEntity {

	private final static Logger LOG = Logger.getLogger(AbstractEntityWithAppearance.class.getName());
	protected final Appearance appearance;
	protected Node spatial;
	protected EntityContext context;
	private BitmapText playerText;
	private BitmapFont nameplateFont;
	private boolean showingNameplate;
	private String nameplateText;

	public AbstractEntityWithAppearance(EntityContext context, String name, Appearance appearance) {
		this.appearance = appearance;
		this.context = context;

		spatial = createNode();
		spatial.setName(name);
		spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
		spatial.setQueueBucket(RenderQueue.Bucket.Opaque);
	}

	/**
	 * Set whether the nameplate is visible or not.
	 * 
	 * @param showNameplate
	 *            show name plate
	 */
	public void setShowingNameplate(boolean showNameplate) {
		// If we have the font, the spawn is actually loading, so actully attach
		// the nameplate node
		if (showNameplate != isShowingNameplate()) {
			if (nameplateFont == null) {
				nameplateFont = context.getAssetManager().loadFont("Interface/Styles/Gold/Fonts/MaiandraOutline_32.fnt");
			}
			if (showNameplate) {
				addNameplate();
			} else {
				playerText.removeFromParent();
				playerText = null;
			}
			this.showingNameplate = showNameplate;
		}
	}

	public Spatial getSpatial() {
		return spatial;
	}

	protected Node createNode() {
		return new Node();
	}

	public BoundingBox getBoundingBox() {
		BoundingVolume bound = spatial.getWorldBound();
		BoundingBox bbox = (BoundingBox) bound;
		return bbox;
	}

	protected abstract Map<String, RGB> createColorMap();

	protected Material texture(String tintColorMapPath, String tintPath, String texturePath, Material material) {
		return texture(createColorMap(), context.getAssetManager(), material, tintColorMapPath, tintPath, texturePath);
	}

	protected static Material texture(Map<String, RGB> colorMapValues, AssetManager assetManager, String materialName,
			String tintColorMapPath, String tintPath, String texturePath) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Loading materials %s", materialName));
			// Material propMaterial =
			// MaterialFactory.getManager().getMaterial(materialName,
			// assetManager);
		}
		Material propMaterial = new Material(assetManager, materialName);
		return texture(colorMapValues, assetManager, propMaterial, tintColorMapPath, tintPath, texturePath);
	}

	protected static Material texture(Map<String, RGB> colorMapValues, AssetManager assetManager, Material propMaterial,
			String tintColorMapPath, String tintPath, String texturePath) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Tint path is %s", tintPath));
		}

		String matParamName = getDiffuseParamName(propMaterial);

		// Icelib.removeMe("texture(%s,%s,%s)", tintColorMapPath, tintPath,
		// texturePath);
		ColorMapConfiguration colorMap = null;

		// Diffuse texture (may already be provided by material if
		// texturePath is null)
		Texture texture = null;
		MatParamTexture textureParam = propMaterial.getTextureParam(matParamName);
		if (texturePath == null) {
			if (textureParam == null || textureParam.getTextureValue() == null) {
				LOG.warning("No Diffuse map either in the material or provided as a fixed texture path.");
				return propMaterial;
			}
		} else {
			texture = assetManager.loadTexture(new TextureKey(texturePath, false));
			if (textureParam != null && textureParam.getTextureValue() != null
					&& textureParam.getTextureValue().getImage() != null) {
				LOG.warning(String.format(
						"DiffuseMap provided in material (%s) and as a path (%s). This is probably not what is wanted.",
						textureParam.getTextureValue().getKey(), texturePath));
			} else {
				texture.setWrap(Texture.WrapMode.Repeat);
				propMaterial.setTexture(matParamName, texture);
			}
		}

		tintMaterial(colorMapValues, assetManager, propMaterial, tintColorMapPath, tintPath);
		return propMaterial;
	}

	protected static String getDiffuseParamName(Material propMaterial) {
		if (propMaterial.getKey() instanceof ExtendedMaterialKey) {
			if (!((ExtendedMaterialKey) propMaterial.getKey()).isLit()) {
				return "ColorMap";
			}
		}
		return "DiffuseMap";
	}

	protected static void tintMaterial(Map<String, RGB> colorMapValues, AssetManager assetManager, Material propMaterial,
			String tintColorMapPath, String tintPath) {
		ColorMapConfiguration colorMap;
		try {
			colorMap = ColorMapConfiguration.get(assetManager, tintColorMapPath);

			// Use IndexedTextureKey to signal we want an image appropriate for
			// color maps
			final IndexedTextureKey tintKey = new IndexedTextureKey(tintPath, false);

			Texture tint = assetManager.loadTexture(tintKey);
			ImageLoader.IndexImage indexImage = (ImageLoader.IndexImage) tint.getImage();

			// Tinting
			List<RGB> colorArr = colorMap.createPaletteArray(colorMapValues, indexImage);
			// List<RGB> colorArr = new ArrayList<RGB>(colorMapValues.values());
			propMaterial.setTexture("PaletteColors", new PaletteTexture(colorArr, 256));
			propMaterial.setInt("PaletteSize", colorArr.size());
			propMaterial.setTexture("TintMap", tint);
		} catch (AssetNotFoundException anfe) {
			// Just a texture?
			LOG.info(String.format("No tint files for %s", tintColorMapPath));
			// Texture texture = assetManager.loadTexture(new
			// TextureKey(texturePath, false));
			// texture.setWrap(Texture.WrapMode.Repeat);
			// propMaterial.setTexture("DiffuseMap", texture);
		}
	}

	protected void addNameplate() {
		playerText = new BitmapText(nameplateFont);
		String displayName = getNameplateText();
		playerText.setText(displayName);
		float textWidth = playerText.getLineWidth() + 20;
		float textOffset = textWidth / 2;
		playerText.setBox(new Rectangle(-textOffset, 0, textWidth, playerText.getHeight()));
		playerText.setSize(1);
		playerText.setQueueBucket(RenderQueue.Bucket.Transparent);
		playerText.addControl(new BillboardControl());
		playerText.setAlignment(BitmapFont.Align.Center);
		attachNameplate(playerText);
	}

	protected void attachNameplate(BitmapText playerText) {
		Node n = new Node();
		n.attachChild(playerText);
		n.setLocalTranslation(0, 5, 0);
		spatial.attachChild(n);
	}

	protected String getNameplateText() {
		return nameplateText == null ? spatial.getName() : nameplateText;
	}

	public void setNameplateText(String nameplateText) {
		this.nameplateText = nameplateText;
	}

	public boolean isShowingNameplate() {
		return showingNameplate;
	}
}
