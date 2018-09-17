package org.icescene.assets;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.icescene.configuration.PNGPaletteReader;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoadException;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;
import com.sixlegs.png.RegisteredChunks;

/**
 * This custom loader is needed to help the "Tinter" shader used to colour
 * creatures. When an appropriate Tint-*.png image is encountered, an image that
 * contains the raw index data is returned instead of the processed one that
 * would normally be returned. This is then used to pick colours from a palette
 * to colour mobs, clothes etc.
 *
 */
public class ImageLoader extends AWTLoader {

	static {
		/**
		 * NOTE - A hacked version of RegisteredChunks that sets a default
		 * palette transparency color (black) for indexed PNG images. I don't
		 * know why EE images seem to have this weird format (no tRNS chunk to
		 * indicate the color index to use as transparency)
		 */

		RegisteredChunks.setDefaultPaletteAlpha(new byte[] { 0 });
	}

	private static final Logger LOG = Logger.getLogger(ImageLoader.class.getName());
	private ThreadLocal<AssetInfo> info = new ThreadLocal<AssetInfo>();
	private ThreadLocal<byte[]> imgData = new ThreadLocal<byte[]>();

	@Override
	public Object load(AssetInfo info) throws IOException {
		this.info.set(info);
		try {
			return super.load(info);
		} finally {
			this.info.remove();
		}
	}

	@Override
	public Image load(InputStream in, boolean flipY) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		/*
		 * Do not close the stream in here. If this method is used externally,
		 * it is up to client code to close
		 */
		IOUtils.copy(in, baos);
		final byte[] imageData = baos.toByteArray();
		imgData.set(imageData);

		// AssetInfo info = this.info.get();
		// if (info != null && !isIndexImage(info) &&
		// info.getKey().getName().toLowerCase().endsWith(".png")) {
		// /*
		// * This is to work around the fact AWT ImageIO can't read PNG
		// * indexed images with alpha channel
		// * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6371389
		// *
		// * TODO
		// *
		// * This is also probably a better solution for extracting the
		// * palette!
		// */
		// PngImage pimg = new PngImage();
		// BufferedImage img = pimg.read(new ByteArrayInputStream(imageData),
		// false);
		// if (img == null) {
		// return null;
		// }
		// return load(img, flipY);
		// }

		return super.load(new ByteArrayInputStream(imageData), flipY);
	}

	public Image doLoad(BufferedImage img, boolean flipY) {

		/**
		 * This is here because JME seems to have lost Luminance16 format whiich
		 * im sure worked before!
		 * 
		 * UPDATE: I know what's happening. It appears the Luminance16 has been deprecated
		 * in OpenGL 3.1 to be removed in OpenGL 3.2. However, I am not actually using 
		 * in a shader, it's for a 16 bit height map image and never reaches to graphics
		 * card.
		 * 
		 *  So personally I think it's a bit extreme this was removed from JME3.
		 */

		int width = img.getWidth();
		int height = img.getHeight();

		switch (img.getType()) {
		case BufferedImage.TYPE_4BYTE_ABGR: // most common in PNG images w/
											// alpha
			byte[] dataBuf1 = (byte[]) extractImageData(img);
			if (flipY)
				flipImage(dataBuf1, width, height, 32);

			ByteBuffer data1 = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
			data1.put(dataBuf1);
			return new Image(Format.ABGR8, width, height, data1, null, com.jme3.texture.image.ColorSpace.sRGB);
		case BufferedImage.TYPE_3BYTE_BGR: // most common in JPEG images
			byte[] dataBuf2 = (byte[]) extractImageData(img);
			if (flipY)
				flipImage(dataBuf2, width, height, 24);

			ByteBuffer data2 = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 3);
			data2.put(dataBuf2);
			return new Image(Format.BGR8, width, height, data2, null, com.jme3.texture.image.ColorSpace.sRGB);
		case BufferedImage.TYPE_BYTE_GRAY: // grayscale fonts
			byte[] dataBuf3 = (byte[]) extractImageData(img);
			if (flipY)
				flipImage(dataBuf3, width, height, 8);
			ByteBuffer data3 = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight());
			data3.put(dataBuf3);
			return new Image(Format.Luminance8, width, height, data3, null, com.jme3.texture.image.ColorSpace.sRGB);

		case BufferedImage.TYPE_USHORT_GRAY: // grayscale heightmap
			short[] dataBuf4 = (short[]) extractImageData(img);
			if (flipY)
				flipImage(dataBuf4, width, height, 16);

			ByteBuffer data4 = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 2);
			data4.asShortBuffer().put(dataBuf4);
			return new Image(Format.Luminance16F, width, height, data4, null, com.jme3.texture.image.ColorSpace.sRGB);
		default:
			break;
		}

		if (img.getTransparency() == Transparency.OPAQUE) {
			ByteBuffer data = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 3);
			// no alpha
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int ny = y;
					if (flipY) {
						ny = height - y - 1;
					}

					int rgb = img.getRGB(x, ny);
					byte r = (byte) ((rgb & 0x00FF0000) >> 16);
					byte g = (byte) ((rgb & 0x0000FF00) >> 8);
					byte b = (byte) ((rgb & 0x000000FF));
					data.put(r).put(g).put(b);
				}
			}
			data.flip();
			return new Image(Format.RGB8, width, height, data, null, com.jme3.texture.image.ColorSpace.sRGB);
		} else {
			ByteBuffer data = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
			// alpha
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int ny = y;
					if (flipY) {
						ny = height - y - 1;
					}

					int rgb = img.getRGB(x, ny);
					byte a = (byte) ((rgb & 0xFF000000) >> 24);
					byte r = (byte) ((rgb & 0x00FF0000) >> 16);
					byte g = (byte) ((rgb & 0x0000FF00) >> 8);
					byte b = (byte) ((rgb & 0x000000FF));
					data.put(r).put(g).put(b).put(a);
				}
			}
			data.flip();
			return new Image(Format.RGBA8, width, height, data, null, com.jme3.texture.image.ColorSpace.sRGB);
		}
	}

	@Override
	public Image load(BufferedImage img, boolean flipY) {
		int width = img.getWidth();
		int height = img.getHeight();
		AssetInfo thisInfo = info.get();
		info.remove();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Asset %s is %d (num. color %d, num comp. %d, pix. %d)",
					thisInfo.getKey().toString(), img.getType(), img.getColorModel().getNumColorComponents(),
					img.getColorModel().getNumComponents(), img.getColorModel().getPixelSize()));
		}
		if (img.getType() == BufferedImage.TYPE_BYTE_BINARY && isIndexImage(thisInfo)) {
			byte[] dataBuf1 = (byte[]) extractImageData(img);
			if (flipY) {
				flipImage(dataBuf1, width, height, img.getColorModel().getPixelSize());
			}

			int idx = 0;
			ByteBuffer data1 = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight());
			if (img.getColorModel().getPixelSize() == 4) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (idx % 2 == 0) {
							data1.put((byte) (dataBuf1[idx / 2] >>> 4));
						} else {
							data1.put((byte) (dataBuf1[idx / 2] & 0x0f));
						}
						idx++;
					}
				}
			} else if (img.getColorModel().getPixelSize() == 2) {
				byte b;
				byte a;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						b = dataBuf1[idx / 4];
						if (idx % 4 == 0) {
							a = ((byte) ((b >>> 6) & 0x03));
						} else if (idx % 4 == 1) {
							a = ((byte) ((b >>> 4) & 0x03));
						} else if (idx % 4 == 2) {
							a = ((byte) ((b >>> 2) & 0x03));
						} else {
							a = ((byte) (b & 0x03));
						}
						data1.put(a);
						idx++;
					}
				}
			} else {
				throw new UnsupportedOperationException("Unsupported depth " + img.getColorModel().getPixelSize());
			}
			return loadIndexedImageAndPalette(thisInfo, width, height, data1);
		} else if (img.getType() == BufferedImage.TYPE_BYTE_INDEXED && isIndexImage(thisInfo)) {
			// TODO more depths?
			byte[] dataBuf1 = (byte[]) extractImageData(img);
			if (flipY) {
				flipImage(dataBuf1, width, height, img.getColorModel().getPixelSize());
			}
			int idx = 0;
			ByteBuffer data1 = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight());
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					data1.put((byte) (dataBuf1[idx]));
					idx++;
				}
			}
			return loadIndexedImageAndPalette(thisInfo, width, height, data1);
		} else if (img.getType() == BufferedImage.TYPE_BYTE_INDEXED && img.getTransparency() == BufferedImage.BITMASK) {
			// TODO bitmask? huh?

			ByteBuffer data = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
			// no alpha
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int ny = y;
					if (flipY) {
						ny = height - y - 1;
					}

					int rgb = img.getRGB(x, ny);
					byte a = (byte) ((rgb & 0xFF000000) >> 24);
					byte r = (byte) ((rgb & 0x00FF0000) >> 16);
					byte g = (byte) ((rgb & 0x0000FF00) >> 8);
					byte b = (byte) ((rgb & 0x000000FF));
					data.put(r).put(g).put(b).put(a);
				}
			}
			data.flip();
			return new Image(Format.RGBA8, width, height, data,  com.jme3.texture.image.ColorSpace.sRGB);
		}
		return doLoad(img, flipY);
	}

	// Meh
	private void flipImage(byte[] img, int width, int height, int bpp) {
		int scSz = (width * bpp) / 8;
		byte[] sln = new byte[scSz];
		int y2 = 0;
		for (int y1 = 0; y1 < height / 2; y1++) {
			y2 = height - y1 - 1;
			System.arraycopy(img, y1 * scSz, sln, 0, scSz);
			System.arraycopy(img, y2 * scSz, img, y1 * scSz, scSz);
			System.arraycopy(sln, 0, img, y2 * scSz, scSz);
		}
	}

	// Meh
    private void flipImage(short[] img, int width, int height, int bpp){
        int scSz = (width * bpp) / 8;
        scSz /= 2; // Because shorts are 2 bytes
        short[] sln = new short[scSz];
        int y2 = 0;
        for (int y1 = 0; y1 < height / 2; y1++){
            y2 = height - y1 - 1;
            System.arraycopy(img, y1 * scSz, sln, 0,         scSz);
            System.arraycopy(img, y2 * scSz, img, y1 * scSz, scSz);
            System.arraycopy(sln, 0,         img, y2 * scSz, scSz);
        }
    }

	// Meh
	private Object extractImageData(BufferedImage img) {
		DataBuffer buf = img.getRaster().getDataBuffer();
		switch (buf.getDataType()) {
		case DataBuffer.TYPE_BYTE:
			DataBufferByte byteBuf = (DataBufferByte) buf;
			return byteBuf.getData();
		case DataBuffer.TYPE_USHORT:
			DataBufferUShort shortBuf = (DataBufferUShort) buf;
			return shortBuf.getData();
		}
		return null;
	}

	@Deprecated
	private boolean isIndexImage(AssetInfo thisInfo) {
		return thisInfo.getKey() instanceof IndexedTextureKey;
	}

	private Image loadIndexedImageAndPalette(AssetInfo thisInfo, int width, int height, ByteBuffer data1) {
		// TODO There MUST be another way to do this
		LOG.info(String.format("Loading palette for %s", thisInfo.getKey().getName()));
		InputStream in = new ByteArrayInputStream(imgData.get());
		imgData.remove();
		try {
			try {
				// Wrap a subclass that can store the palette
				return new IndexImage(width, height, data1, new PNGPaletteReader(in));
			} finally {
				in.close();
			}
		} catch (IOException ioe) {
			throw new AssetLoadException("Failed to load PNG palette.", ioe);
		}
	}

	public class IndexImage extends Image {

		private PNGPaletteReader palette;

		public IndexImage(int width, int height, ByteBuffer data, PNGPaletteReader palette) {
			super(Format.Alpha8, width, height, data, com.jme3.texture.image.ColorSpace.sRGB);
			this.palette = palette;
		}

		public PNGPaletteReader getPalette() {
			return palette;
		}
	}
}
