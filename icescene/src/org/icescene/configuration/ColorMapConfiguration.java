package org.icescene.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Color;
import org.icelib.Icelib;
import org.icelib.RGB;
import org.icescene.assets.ImageLoader;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Image;

public class ColorMapConfiguration extends AbstractPropertiesConfiguration<ColorMapConfiguration> {

	private static final Logger LOG = Logger.getLogger(ColorMapConfiguration.class.getName());
	private final Map<String, RGB> colors = new LinkedHashMap<String, RGB>();
	private final Map<RGB, String> reverseColors = new HashMap<RGB, String>();
	private final static Map<String, ColorMapConfiguration> cache = new HashMap<String, ColorMapConfiguration>();

	public static ColorMapConfiguration get(AssetManager assetManager, String resourceName) {
		// TODO can we use AssetManager?
		ColorMapConfiguration cfg = cache.get(resourceName);
		if (cfg == null) {
			cfg = new ColorMapConfiguration(assetManager, resourceName);
			cache.put(resourceName, cfg);
		}
		return cfg;
	}

	public static void resetCache() {
		cache.clear();
	}

	public ColorMapConfiguration(AssetManager assetManager) {
		this(assetManager, null);
	}

	public ColorMapConfiguration(AssetManager assetManager, String resourceName) {
		this(assetManager, resourceName, (ColorMapConfiguration) null);
	}

	public ColorMapConfiguration(AssetManager assetManager, String resourceName, ColorMapConfiguration base) {
		super(assetManager, resourceName, base);
		if (resourceName != null) {
			load();
		}
	}

	public final void load() {
		for (Map.Entry<String, String> k : getBackingObject().entrySet()) {
			RGB col = new Color(k.getKey().toString());
			final String colName = k.getValue().toString();
			colors.put(colName, col);
			if (reverseColors.containsKey(col)) {
				throw new IllegalArgumentException("Tint map contains same colour twice");
			}
			reverseColors.put(col, k.getValue().toString());
		}
	}

	public List<RGB> createPalette(Map<String, RGB> colors, ImageLoader.IndexImage indexImage) {
		// TODO Must be same size as number of colours in the indexed texture
		int texSize = 256;
		final Image.Format format = Image.Format.RGB8;
		int bytesPerPixel = format.getBitsPerPixel() / 8;
		PNGPaletteReader palette = indexImage.getPalette();
		Iterator<Map.Entry<Integer, RGB>> enit = palette.entrySet().iterator();
		List<RGB> colorArr = new ArrayList<RGB>();
		for (int i = 0; i < texSize; i++) {
			if (enit.hasNext()) {
				Map.Entry<Integer, RGB> ent = enit.next();
				RGB defColor = ent.getValue();
				String colorName = getName(ent.getValue());
				RGB el = colorName == null || colors == null ? null : colors.get(colorName);
				RGB col = el == null ? defColor : el;
				if (col == null) {
					col = Color.BLACK;
				}
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("    Name: %s     Index: %d    Color: %s", colorName, i, Icelib.toHexString(col)));
				}
				colorArr.add(col);
			}
		}
		return colorArr;
	}

	public List<RGB> createPaletteArray(Map<String, RGB> colors, ImageLoader.IndexImage indexImage) {
		// TODO Must be same size as number of colours in the indexed texture
		int texSize = 256;
		final Image.Format format = Image.Format.RGB8;
		int bytesPerPixel = format.getBitsPerPixel() / 8;
		PNGPaletteReader palette = indexImage.getPalette();
		Iterator<Map.Entry<Integer, RGB>> enit = palette.entrySet().iterator();
		List<RGB> colorArr = new ArrayList<RGB>();
		for (int i = 0; i < texSize; i++) {
			if (enit.hasNext()) {
				Map.Entry<Integer, RGB> ent = enit.next();
				RGB defColor = ent.getValue();
				String colorName = getName(ent.getValue());
				RGB el = colorName == null || colors == null ? null : colors.get(colorName);
				RGB col = el == null ? defColor : el;
				if (col == null) {
					col = Color.BLACK;
				}
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("    Name: %s     Index: %d    Color: %s", colorName, i, Icelib.toHexString(col)));
				}
//				Icelib.removeMe("    Name: %s     Index: %d    Color: %s", colorName, i, Icelib.toHexString(col));
				colorArr.add(col);
			}
		}
		return colorArr;
	}

	public RGB getMappedColor(String name) {
		return colors.get(name);
	}

	public Set<String> names() {
		return colors.keySet();
	}

	public List<RGB> colors() {
		return new ArrayList<RGB>(colors.values());
	}

	public Map<String, RGB> getColors() {
		return Collections.unmodifiableMap(colors);
	}

	public String getName(RGB color) {
		StringBuilder bui = new StringBuilder();
		for (RGB x : reverseColors.keySet()) {
			if (bui.length() > 0) {
				bui.append(",");
			}
			bui.append(Icelib.toHexNumber(x, true));
		}
		return reverseColors.get(color);
	}

	@Override
	protected void fill(boolean partial) {
		getBackingObject().clear();
		for (Map.Entry<String, RGB> en : colors.entrySet()) {
			getBackingObject().put(Icelib.toHexNumber(en.getValue()), en.getKey());
		}
	}

	public void add(String key, RGB colour) {
		if (colors.containsKey(key)) {
			RGB col = colors.get(key);
			reverseColors.remove(col);
		}
		colors.put(key, colour);
		reverseColors.put(colour, key);
	}

	public void remove(String key) {
		final RGB rev = colors.remove(key);
		if (rev != null) {
			reverseColors.remove(rev);
		}
	}
}
