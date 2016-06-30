package org.icescene.assets;

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
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;

/**
 * This custom loader is needed to help the "Tinter" shader used to colour creatures. When
 * an appropriate Tint-*.png image is encountered, an image that contains the raw index
 * data is returned instead of the processed one that would normally be returned. This is
 * then used to pick colours from a palette to colour mobs, clothes etc.
 *
 */
public class ImageLoader extends AWTLoader {

    private static final Logger LOG = Logger.getLogger(ImageLoader.class.getName());
    private ThreadLocal<AssetInfo> info = new ThreadLocal<AssetInfo>();
    private ThreadLocal<byte[]> imgData = new ThreadLocal<byte[]>();

    @Override
    public Object load(AssetInfo info) throws IOException {
        this.info.set(info);
        return super.load(info);
    }

    @Override
    public Image load(InputStream in, boolean flipY) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        try {
            IOUtils.copy(in, baos);
        } finally {
            in.close();
        }
        final byte[] imageData = baos.toByteArray();
        imgData.set(imageData);
        return super.load(new ByteArrayInputStream(imageData), flipY);
    }

    @Override
    public Image load(BufferedImage img, boolean flipY) {
        int width = img.getWidth();
        int height = img.getHeight();
        AssetInfo thisInfo = info.get();
        info.remove();
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Asset %s is %d (num. color %d, num comp. %d, pix. %d)",
                    thisInfo.getKey().toString(),
                    img.getType(),
                    img.getColorModel().getNumColorComponents(),
                    img.getColorModel().getNumComponents(),
                    img.getColorModel().getPixelSize()));
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
        }
        return super.load(img, flipY);
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
            super(Format.Alpha8, width, height, data);
            this.palette = palette;
        }

        public PNGPaletteReader getPalette() {
            return palette;
        }
    }
}
