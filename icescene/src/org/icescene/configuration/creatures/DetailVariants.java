package org.icescene.configuration.creatures;

import org.icelib.beans.MappedMap;

public class DetailVariants extends MappedMap<String, DetailElements> {

	public DetailVariants() {
		super(String.class, DetailElements.class);
	}

}