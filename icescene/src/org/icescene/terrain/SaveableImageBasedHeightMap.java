package org.icescene.terrain;

import java.io.OutputStream;

import com.jme3.math.ColorRGBA;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class SaveableImageBasedHeightMap extends ImageBasedHeightMap implements SaveableHeightMap {
    private float backwardsCompScale = 255f;

    public SaveableImageBasedHeightMap(Image colorImage) {
        super(colorImage);
    }

    public SaveableImageBasedHeightMap(Image colorImage, float heightScale) {
        super(colorImage, heightScale);
    }
    
    protected ColorRGBA calculateColor(ColorRGBA store, float val) {
        store.r = val;
        store.g = val;
        store.b = val;
        return store;
    }

    public float[] getHeightData() {
        return heightData;
    }

    public void setHeightData(float[] heightData) {
        this.heightData = heightData;
    }

    
    public Image save(OutputStream out) {
        return save(out, false, false);
    }
    
    public Image save(OutputStream out, boolean flipX, boolean flipY) {
        
        Image img = colorImage.clone();
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        ImageRaster raster = ImageRaster.create(img);
        
        ColorRGBA colorStore = new ColorRGBA();
        
        int index = 0;
        if (flipY) {
            for (int h = 0; h < imageHeight; ++h) {
                if (flipX) {
                    for (int w = imageWidth - 1; w >= 0; --w) {
                        //int baseIndex = (h * imageWidth)+ w;
                        //heightData[index++] = getHeightAtPostion(raster, baseIndex, colorStore)*heightScale;
//                        raster.setPixel(w, h, calculateColor(colorStore, heightData[index++] / heightScale / backwardsCompScale));
                        raster.setPixel(w, h, calculateColor(colorStore, heightData[index++]));
                    }
                } else {
                    for (int w = 0; w < imageWidth; ++w) {
                        //int baseIndex = (h * imageWidth)+ w;
                        //heightData[index++] = getHeightAtPostion(raster, baseIndex, colorStore)*heightScale;
                        //raster.setPixel(w, h, calculateColor(colorStore, heightData[index++] / heightScale / backwardsCompScale));
                    	raster.setPixel(w, h, calculateColor(colorStore, heightData[index++]));
                    }
                }
            }
        } else {
            for (int h = imageHeight - 1; h >= 0; --h) {
                if (flipX) {
                    for (int w = imageWidth - 1; w >= 0; --w) {
                        //int baseIndex = (h * imageWidth)+ w;
                        //heightData[index++] = getHeightAtPostion(raster, baseIndex, colorStore)*heightScale;
                        //raster.setPixel(w, h, calculateColor(colorStore, heightData[index++] / heightScale / backwardsCompScale));
                    	raster.setPixel(w, h, calculateColor(colorStore, heightData[index++]));
                    }
                } else {
                    for (int w = 0; w < imageWidth; ++w) {
                        //int baseIndex = (h * imageWidth)+ w;
                        //heightData[index++] = getHeightAtPostion(raster, baseIndex, colorStore)*heightScale;
//                        raster.setPixel(w, h, calculateColor(colorStore, heightData[index++] / heightScale / backwardsCompScale));
                        raster.setPixel(w, h, calculateColor(colorStore, heightData[index++]));
                    }
                }
            }
        }
        return img;
    }

    public Image getColorImage() {
        return colorImage;
    }

	@Override
	public float getHeightScale() {
		return heightScale;
	}
}
