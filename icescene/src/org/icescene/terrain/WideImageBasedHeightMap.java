package org.icescene.terrain;

import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class WideImageBasedHeightMap extends AbstractHeightMap {

	protected Image colorImage;

	public float getHeightScale() {
		return heightScale;
	}

	public void setImage(Image image) {
		this.colorImage = image;
	}

	public WideImageBasedHeightMap(Image colorImage) {
		this.colorImage = colorImage;
	}

	public WideImageBasedHeightMap(Image colorImage, float heightScale) {
		this.colorImage = colorImage;
		this.heightScale = heightScale;
	}

	public boolean load() {
		return load(false, false);
	}

	protected ImageRaster getImageRaster() {
		return ImageRaster.create(colorImage);
	}

	protected float getValue(int x, int y) {
		return ((float) (colorImage.getData(0).getShort(getOffset(x, y)) & 0xffff) / heightScale);
	}

	public void fill(float height) {
		for (int i = 0; i < heightData.length; i++) {
			heightData[i] = height;
		}
	}

	public boolean load(boolean flipX, boolean flipY) {

		int imageWidth = colorImage.getWidth();
		int imageHeight = colorImage.getHeight();

		if (imageWidth != imageHeight) {
			throw new RuntimeException("imageWidth: " + imageWidth + " != imageHeight: " + imageHeight);
		}

		size = imageWidth;

		heightData = new float[(imageWidth * imageHeight)];

		int index = 0;
		if (flipY) {
			for (int h = 0; h < imageHeight; ++h) {
				if (flipX) {
					for (int w = imageWidth - 1; w >= 0; --w) {
						heightData[index++] = getValue(w, h);
					}
				} else {
					for (int w = 0; w < imageWidth; ++w) {
						heightData[index++] = getValue(w, h);
					}
				}
			}
		} else {
			for (int h = imageHeight - 1; h >= 0; --h) {
				if (flipX) {
					for (int w = imageWidth - 1; w >= 0; --w) {
						heightData[index++] = getValue(w, h);
					}
				} else {
					for (int w = 0; w < imageWidth; ++w) {
						heightData[index++] = getValue(w, h);
					}
				}
			}
		}

		return true;
	}

	protected int getOffset(int x, int y) {
		return (x + (colorImage.getWidth() * y)) * 2;
	}
}