package org.icescene.configuration;

import icemoon.iceloader.AbstractConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.icelib.ConsoleErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.jme3.asset.AssetManager;

/**
 * Loads and stores a Component configuration file. All props have one of these.
 */
public class AbstractXMLConfiguration<T extends ContentHandler> extends AbstractConfiguration<T> {

    public AbstractXMLConfiguration(AssetManager assetManager, String resourceName, T backingObject) {
        super(resourceName, assetManager, backingObject);
    }


    @Override
    protected void load(InputStream in, T handler) throws IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(false);
        try {
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.setErrorHandler(new ConsoleErrorHandler());
            InputSource is = new InputSource(new InputStreamReader(in));
            xmlReader.parse(is);
        } catch (SAXException se) {
            throw new IOException(se);
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }


}
