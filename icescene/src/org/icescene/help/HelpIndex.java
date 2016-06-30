package org.icescene.help;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.icescene.configuration.AbstractXMLConfiguration;
import org.icescene.configuration.MeshConfiguration;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jme3.asset.AssetManager;

public class HelpIndex extends AbstractXMLConfiguration<HelpIndex.HelpHandler> {

    final static Logger LOG = Logger.getLogger(MeshConfiguration.class.getName());
    private final static Map<String, HelpIndex> cache = new HashMap<String, HelpIndex>();

    public static HelpIndex get(AssetManager assetManager, String resourceName) {
        HelpIndex cfg = cache.get(resourceName);
        if (cfg == null) {
            cfg = new HelpIndex(assetManager, resourceName);
            cache.put(resourceName, cfg);
        }
        return cfg;
    }

    private HelpIndex(AssetManager assetManager, String resourceName) {
        this(assetManager, resourceName, null);
    }

    private HelpIndex(AssetManager assetManager, String resourceName, ClassLoader classLoader) {
        super(assetManager, resourceName, new HelpHandler());
    }

    public String getHomeId() {
        return getBackingObject().homeId;
    }

    public Map<String, HelpEntry> getRoots() {
        return getBackingObject().rootEntries;
    }

    public static class HelpEntry {

        private final String id;
        private final Map<String, String> paths = new HashMap<String, String>();
        private final Map<String, String> labels = new HashMap<String, String>();

        public HelpEntry(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public Map<String, String> getPaths() {
            return paths;
        }

        public Map<String, String> getLabels() {
            return labels;
        }
    }

    static class HelpHandler extends DefaultHandler {

        public enum State {

            PATH, LABEL, HELP, ENTRY;
        }
        private HelpEntry entry;
        private String locale;
        private final Map<String, HelpEntry> rootEntries = new HashMap<String, HelpEntry>();
        private String homeId;
        private State state;

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (state.equals(State.PATH)) {
                String path = new String(ch, start, length);
                entry.paths.put(locale, path);
            } else if (state.equals(State.LABEL)) {
                String label = new String(ch, start, length);
                entry.labels.put(locale, label);
            }
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            String key = localName;
            if (key.equals("help")) {
                homeId = atts.getValue("home");
                state = State.HELP;
            } else if (key.equals("entry")) {
                String id = atts.getValue("id");
                entry = new HelpEntry(id);
                rootEntries.put(id, entry);
                state = State.ENTRY;
            } else if (key.equals("path")) {
                locale = atts.getValue("locale");
                state = State.PATH;
            } else if (key.equals("label")) {
                locale = atts.getValue("locale");
                state = State.LABEL;
            }
        }

        @Override
        public void endDocument() throws SAXException {
        }
    }
}
