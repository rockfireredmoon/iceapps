package org.icescene.assets;

/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

/**
 * Loads CSM component definition files
 */
public class ComponentLoader extends DefaultHandler implements AssetLoader {

	final static Logger LOG = Logger.getLogger(ComponentLoader.class.getName());

	private ComponentDefinition def;

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		String key = localName;
		if (key.equals("Component")) {
			def.setComponentId(atts.getValue("id"));
		} else if (key.equals("Entity")) {
			def.getEntities().add(attributesToProperties(atts));
		} else if (key.equals("Sound")) {
			def.getSounds().add(attributesToProperties(atts));
		}  else if (key.equals("ParticleSystem")) {
			def.getParticleSystems().add(attributesToProperties(atts));
		} else if (key.equals("Light")) {
			def.getLights().add(attributesToProperties(atts));
		} else if (key.equals("XRef")) {
			def.getXrefs().add(attributesToProperties(atts));
		}
	}

	private Properties attributesToProperties(Attributes atts) {
		Properties p = new Properties();
		for (int i = 0; i < atts.getLength(); i++) {
			p.put(atts.getLocalName(i), atts.getValue(i));
		}
		return p;
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		try {
			def = new ComponentDefinition();

			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);

			XMLReader xr = factory.newSAXParser().getXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);

			InputStreamReader r = null;
			try {
				r = new InputStreamReader(assetInfo.openStream());
				xr.parse(new InputSource(r));
			} finally {
				if (r != null) {
					r.close();
				}
			}
			return def;
		} catch (SAXException ex) {
			IOException ioEx = new IOException("Error while parsing Ogre3D mesh.xml");
			ioEx.initCause(ex);
			throw ioEx;
		} catch (ParserConfigurationException ex) {
			IOException ioEx = new IOException("Error while parsing Ogre3D mesh.xml");
			ioEx.initCause(ex);
			throw ioEx;
		}
	}
}
