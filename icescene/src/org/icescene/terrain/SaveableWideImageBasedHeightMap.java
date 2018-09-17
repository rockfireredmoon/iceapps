package org.icescene.terrain;

import java.io.OutputStream;

import org.icescene.io.PNGSaver;

import com.jme3.texture.Image;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;

public class SaveableWideImageBasedHeightMap extends WideImageBasedHeightMap implements SaveableHeightMap {

	public SaveableWideImageBasedHeightMap(Image colorImage) {
		super(colorImage);
	}

	public SaveableWideImageBasedHeightMap(Image colorImage, float heightScale) {
		super(colorImage, heightScale);
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

		ImageInfo ii = new ImageInfo(img.getWidth(), img.getHeight(), 16, false, true, false);
		PngWriter pngw = new PngWriter(out, ii);

		int index = 0;
		if (flipY) {
			for (int h = 0; h < imageHeight; ++h) {
				ImageLineInt line = new ImageLineInt(ii, new int[] { imageWidth });
				if (flipX) {
					for (int w = imageWidth - 1; w >= 0; --w) {
						PNGSaver.setValue(line, w, (int)(heightData[index++] * heightScale));
					}
				} else {
					for (int w = 0; w < imageWidth; ++w) {
						PNGSaver.setValue(line, w, (int)(heightData[index++] * heightScale));
					}
				}
				pngw.writeRow(line);
			}
		} else {
			for (int h = imageHeight - 1; h >= 0; --h) {
				ImageLineInt line = new ImageLineInt(ii, new int[] { imageWidth });
				if (flipX) {
					for (int w = imageWidth - 1; w >= 0; --w) {
						PNGSaver.setValue(line, w, (int)(heightData[index++] * heightScale));
					}
				} else {
					for (int w = 0; w < imageWidth; ++w) {
						PNGSaver.setValue(line, w, (int)(heightData[index++] * heightScale));
					}
				}
				pngw.writeRow(line);
			}
		}
		pngw.end();
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
