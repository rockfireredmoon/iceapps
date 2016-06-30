package org.icescene.configuration.creatures;

import java.util.EnumMap;
import java.util.Map;

import org.icelib.beans.MappedMap;

public class Details extends MappedMap<DetailType, DetailVariants> {

	public Details() {
		super(new EnumMap<DetailType, DetailVariants>(DetailType.class), DetailType.class, DetailVariants.class);
	}

	@Override
	public DetailVariants put(DetailType k, DetailVariants v) {
		// TODO Auto-generated method stub
		return super.put(k, v);
	}

	@Override
	public void putAll(Map<? extends DetailType, ? extends DetailVariants> m) {
		// TODO Auto-generated method stub
		super.putAll(m);
	}

}