package org.icescene.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jme3.asset.AssetManager;

public class MeshConfiguration extends AbstractXMLConfiguration<MeshConfiguration.MeshHandler> {

	final static Logger LOG = Logger.getLogger(MeshConfiguration.class.getName());
	private final static Map<String, MeshConfiguration> cache = new HashMap<String, MeshConfiguration>();

	public static MeshConfiguration get(AssetManager assetManager, String resourceName) {
		// TODO can we use AssetManager?
		MeshConfiguration cfg = cache.get(resourceName);
		if (cfg == null) {
			cfg = new MeshConfiguration(assetManager, resourceName);
			cache.put(resourceName, cfg);
		}
		return cfg;
	}

	private String material;

	private MeshConfiguration(AssetManager assetManager, String resourceName) {
		this(assetManager, resourceName, null);
	}

	private MeshConfiguration(AssetManager assetManager, String resourceName, ClassLoader classLoader) {
		super(assetManager, resourceName, new MeshHandler());
	}

	public TextureDefinition getTexture(String alias) {
		return getBackingObject().textures.get(alias);
	}

	public List<TextureDefinition> getTextures() {
		return Collections.unmodifiableList(new ArrayList<TextureDefinition>(getBackingObject().textures.values()));
	}

	public String getMaterial() {
		return getBackingObject().material;
	}

	public static String DIFFUSE = "Diffuse";
	public static String ILLUMINATED = "ILLUMINATED";

	public static class TextureDefinition {

		private final String alias;
		private final String name;

		public TextureDefinition(String alias, String name) {
			this.alias = alias;
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public String getName() {
			return name;
		}
	}

	static class MeshHandler extends DefaultHandler {

		private final Map<String, TextureDefinition> textures = new HashMap<String, TextureDefinition>();
		private String componentId;
		private String material;

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			String key = localName;
			if (key.equals("submesh")) {
				String mat = atts.getValue("material");
				if (mat != null) {
					material = mat;
				}
			}
			if (key.equals("texture")) {
				final String aliasName = atts.getValue("alias");
				textures.put(aliasName, new TextureDefinition(aliasName, atts.getValue("name")));
			}
		}

		@Override
		public void endDocument() throws SAXException {
		}
	}
}
