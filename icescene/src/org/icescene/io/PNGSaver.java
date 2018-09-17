package org.icescene.io;

import java.io.OutputStream;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;

public class PNGSaver {

	public static void setValue(ImageLineInt image, int x, int val) {
		image.getScanline()[x] = val;
	}

	public static void save(OutputStream out, boolean flipX, boolean flipY, Image img) {

		int imageWidth = img.getWidth();
		int imageHeight = img.getHeight();

		if (img.getFormat().equals(Image.Format.Luminance16F)) {

			ImageRaster r = ImageRaster.create(img);
			ImageInfo ii = new ImageInfo(img.getWidth(), img.getHeight(), 16, false, true, false);
			PngWriter pngw = new PngWriter(out, ii);

			int py = 0;
			if (flipY) {
				for (int h = imageHeight - 1; h >= 0; --h) {
					int px = 0;
					ImageLineInt line = new ImageLineInt(ii, new int[] { imageWidth });
					if (flipX) {
						for (int w = imageWidth - 1; w >= 0; --w) {
							setValue(line, w, (int) (r.getPixel(px++, py).r * 65535f));
						}
					} else {
						for (int w = 0; w < imageWidth; ++w) {
							setValue(line, w, (int) (r.getPixel(px++, py).r * 65535f));
						}
					}
					pngw.writeRow(line);
					py++;
				}
			} else {
				for (int h = 0; h < imageHeight; ++h) {
					int px = 0;
					ImageLineInt line = new ImageLineInt(ii, new int[] { imageWidth });
					if (flipX) {
						for (int w = imageWidth - 1; w >= 0; --w) {
							setValue(line, w, (int) (r.getPixel(px++, py).r * 65535f));
						}
					} else {
						for (int w = 0; w < imageWidth; ++w) {
							setValue(line, w, (int) (r.getPixel(px++, py).r * 65535f));
						}
					}
					pngw.writeRow(line);
					py++;
				}
			}

			pngw.end();
		} else if (img.getFormat().equals(Image.Format.ABGR8)) {

			ImageInfo ii = new ImageInfo(img.getWidth(), img.getHeight(), 8, true);
			PngWriter pngw = new PngWriter(out, ii);
			ImageRaster raster = ImageRaster.create(img);

			if (flipY) {
				for (int h = imageHeight - 1; h >= 0; --h) {
					ImageLineInt line = new ImageLineInt(ii);
					if (flipX) {
						for (int w = imageWidth - 1; w >= 0; --w) {
							setPixel(raster, w, h, line);
						}
					} else {
						for (int w = 0; w < imageWidth; ++w) {
							setPixel(raster, w, h, line);
						}
					}
					pngw.writeRow(line);
				}
			} else {
				for (int h = 0; h < imageHeight; ++h) {
					ImageLineInt line = new ImageLineInt(ii);
					if (flipX) {
						for (int w = imageWidth - 1; w >= 0; --w) {
							setPixel(raster, w, h, line);
						}
					} else {
						for (int w = 0; w < imageWidth; ++w) {
							setPixel(raster, w, h, line);
						}
					}
					pngw.writeRow(line);
				}
			}
			pngw.end();
		} else {
			throw new UnsupportedOperationException("Unsupported format " + img.getFormat());
		}
		// return img;
	}

	protected static void setPixel(ImageRaster raster, int w, int h, ImageLineInt line) {
		ColorRGBA col = raster.getPixel(w, h);
		ImageLineHelper.setPixelRGBA8(line, w, (int) (col.r * 255), (int) (col.g * 255), (int) (col.b * 255), (int) (col.a * 255));
	}
}
