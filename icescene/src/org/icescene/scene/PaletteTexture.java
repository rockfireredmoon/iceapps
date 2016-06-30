package org.icescene.scene;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Icelib;
import org.icelib.RGB;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

public class PaletteTexture extends Texture2D {

    private static final Logger LOG = Logger.getLogger(PaletteTexture.class.getName());

    public PaletteTexture(Collection<RGB> colorArr) {
        this(colorArr, colorArr.size());
    }

    public PaletteTexture(Collection<RGB> colorArr, int cols) {
        super(cols, 1, Image.Format.RGB8);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Making palette texture of %d", cols));
        }
        ByteBuffer paletteData = ByteBuffer.allocateDirect(cols * 3);
        for (RGB c : colorArr) {
            paletteData.put((byte) c.getRed());
            paletteData.put((byte) c.getGreen());
            paletteData.put((byte) c.getBlue());
            if (LOG.isLoggable(Level.FINE)) {
                LOG.info(String.format("    %s", Icelib.toHexString(c)));
            }
        }
        paletteData.rewind();
        setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        setMagFilter(Texture.MagFilter.Nearest);
        setAnisotropicFilter(0);
        Image paletteImage = new Image(Image.Format.RGB8, cols, 1, paletteData);
        setImage(paletteImage);
    }
}
