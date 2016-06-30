package org.icescene.scene;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.ClothingTemplate;
import org.icelib.Icelib;
import org.icelib.RGB;
import org.icescene.Icescene;
import org.icescene.assets.ImageLoader;
import org.icescene.assets.IndexedTextureKey;
import org.icescene.configuration.ColorMapConfiguration;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

public class ClothingTexture {

    private static final Logger LOG = Logger.getLogger(ClothingTexture.class.getName());
    private ClothingTextureKey key;
    private final ClothingTemplate itemDefinition;
    private final AssetManager assetManager;
    private final List<RGB> itemColours;

    public ClothingTexture(AssetManager assetManager, ClothingTextureKey key, ClothingTemplate itemDefinition, List<RGB> itemColours) {
        this.key = key;
        this.assetManager = assetManager;
        this.itemColours = itemColours;
        this.itemDefinition = itemDefinition;
    }

    public ClothingTextureKey getKey() {
        return key;
    }

    public ClothingTemplate getItemDefinition() {
        return itemDefinition;
    }

    public void configureMaterial(Material material, int index) {
        String textureName = key.getSubTextureName();
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Loading texture for %s (%s)", textureName, itemDefinition.getKey()));
        }
        // Get the grey scale texture for the item
        final String itemTexturePath = Icescene.checkAssetPath(String.format("%1$s/%2$s-%3$s.png", itemDefinition.getKey().getPath(), itemDefinition.getKey().getName(), textureName));
        Texture itemTexture = assetManager.loadTexture(new TextureKey(itemTexturePath, false));
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Item texture is %s", itemTexturePath));
        }

        // Get the clothing map (determines which area of image is for what item).
        // The colormap configuration file that goes with it defines which
        // colors are used for each piece. From this map we build an alpha map containing
        // ONLY the pixels for the colours to use for this item
        final String clothingMapPath = Icescene.checkAssetPath(String.format("%1$s/%2$s-%3$s-Clothing_Map.png", itemDefinition.getKey().getPath(), itemDefinition.getKey().getName(), textureName));
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Clothing map texture is %s", itemTexturePath));
        }
        Texture clothingMapTexture = assetManager.loadTexture(new IndexedTextureKey(clothingMapPath, false));

        Image rawClothingMapImage = clothingMapTexture.getImage();
        ColorMapConfiguration clothingMapColors = ColorMapConfiguration.get(assetManager, clothingMapPath + ".colormap");
        RGB rgb = clothingMapColors.getMappedColor(key.getRegion().name().toLowerCase());
        ByteBuffer maskData = ByteBuffer.allocateDirect(rawClothingMapImage.getWidth() * rawClothingMapImage.getHeight());
        ByteBuffer clothingMapData = rawClothingMapImage.getData(0); // TODO: Think there is only 1. Check

        if (rawClothingMapImage instanceof ImageLoader.IndexImage) {
            // Using an indexed image
            ImageLoader.IndexImage clothingMapImage = (ImageLoader.IndexImage) rawClothingMapImage;
            // Find the index of the colour we need to use
            int maskPixelValue = clothingMapImage.getPalette().getIndex(rgb);
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info(String.format("Looking for clothing map pixels of index %d", maskPixelValue));
            }
            int i = 0;
            for (int y = 0; y < clothingMapImage.getHeight(); y++) {
                for (int x = 0; x < clothingMapImage.getHeight(); x++) {
                    byte m = clothingMapData.get(i);
                    if (m == maskPixelValue) {
                        maskData.put((byte) 255);
                    } else {
                        maskData.put((byte) 0);
                    }
                    i++;
                }
            }
        } else {
            // Using a full colour image
            int i = 0;
            System.err.println(rawClothingMapImage.getFormat());
            System.err.println("FULL COLOR CLOTHING MAP IMAGE NOT NOT YET SUPPORTED");
            System.exit(0);
//            for (int y = 0; y < rawClothingMapImage.getHeight(); y++) {
//                for (int x = 0; x < rawClothingMapImage.getHeight(); x++) {
//                    byte m = clothingMapData.get(i);
//                    if (m == 1) {
//                        maskData.put((byte) 255);
//                    } else {
//                        maskData.put((byte) 0);
//                    }
//                    i++;
//                }
//            }
        }
        maskData.rewind();
        Image clothingMaskImage = new Image(Image.Format.Alpha8, rawClothingMapImage.getWidth(), rawClothingMapImage.getHeight(), maskData);
        clothingMapTexture.setImage(clothingMaskImage);

        // Get the tint map (determines how each piece is colored).
        final String clothingTintTexturePath = Icescene.checkAssetPath(String.format("%1$s/%2$s-%3$s-Tint.png", itemDefinition.getKey().getPath(), itemDefinition.getKey().getName(), textureName));
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Clothing tint texture is %s", clothingTintTexturePath));
        }
        Texture clothingTintTexture = assetManager.loadTexture(new IndexedTextureKey(clothingTintTexturePath, false));
        ColorMapConfiguration clothingTintColors = ColorMapConfiguration.get(assetManager, clothingTintTexturePath + ".colormap");
        Map<String, RGB> clothingTintColorValues = new HashMap<String, RGB>();
        if (itemColours != null) {
            for (int i = 0; i < itemColours.size(); i++) {
                clothingTintColorValues.put(Icelib.toNumberName(i + 1), itemColours.get(i));
            }
        }

        // Some clothing tint images are indexed, some are full colour
        if (clothingTintTexture.getImage() instanceof ImageLoader.IndexImage) {
        	
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info(String.format("Clothing tint texture %s is an indexed image", clothingTintTexturePath));
            }
            ImageLoader.IndexImage clothingTintTextureImage = (ImageLoader.IndexImage) clothingTintTexture.getImage();
            
            List<RGB> colorArr = clothingTintColors.createPalette(clothingTintColorValues, clothingTintTextureImage);
            
//            Icelib.removeMe("Using indexed tint");
//            for(RGB r : colorArr) {
//            	Icelib.removeMe("COL: %s",  Icelib.toHexNumber(r));
//            }

            // Build a Texture2D that contains the palette
            Texture paletteTexture = new PaletteTexture(colorArr, 256);

            material.setParam("ClothingPaletteColors" + index, VarType.Texture2D, paletteTexture);
            material.setInt("ClothingPaletteSize" + index, colorArr.size());
        } else {
            
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info(String.format("Clothing tint texture %s is a true colour image", clothingTintTexturePath));
            }
//            Icelib.removeMe("Using RGB tint");

            // Build a Texture2D that contains the palette
            List<RGB> newColorArr = new ArrayList<RGB>();
            for (Map.Entry<String, RGB> cEn : clothingTintColors.getColors().entrySet()) {
                final RGB crgb = clothingTintColorValues.containsKey(cEn.getKey())
                        ? clothingTintColorValues.get(cEn.getKey()) : cEn.getValue();
                newColorArr.add(crgb);
//            	Icelib.removeMe("COL: %s %s",  cEn.getKey(), cEn.getValue(), Icelib.toHexNumber(crgb));
            }
            material.setParam("ClothingTrueMapOrgColors" + index, VarType.Texture2D,
                    new PaletteTexture(clothingTintColors.getColors().values()));
            material.setParam("ClothingTrueMapNewColors" + index, VarType.Texture2D,
                    new PaletteTexture(newColorArr));
            material.setInt("ClothingTrueMapSize" + index, newColorArr.size());
        }

        // Configure the material textures
        material.setTexture("ClothingTexture" + index, itemTexture);
        material.setTexture("ClothingMapTexture" + index, clothingMapTexture);
        material.setTexture("ClothingTintTexture" + index, clothingTintTexture);
    }

	@Override
	public String toString() {
		return "ClothingTexture [key=" + key + ", itemDefinition=" + itemDefinition + ", itemColours=" + itemColours + "]";
	}

}
