package org.icescene.io;

import org.icelib.beans.MappedMap;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.Service;

@Service
public class KeyMaps extends MappedMap<String, KeyMapping> implements IcesceneService {
	private static final long serialVersionUID = 1L;
	
	public KeyMaps() {
		super(String.class, KeyMapping.class);
	}

	@Override
	public void init(IcesceneApp app) {
	}

	@Override
	protected Object doPut(Object k, Object v) {
		// TODO Auto-generated method stub
		return super.doPut(k, v);
	}

//	@Override
//	public KeyMapping put(String k, KeyMapping v) {
//		// TODO Auto-generated method stub
//		return super.put(k, v);
//	}
//
//	@Override
//	public void putAll(Map<? extends String, ? extends KeyMapping> m) {
//		// TODO Auto-generated method stub
//		super.putAll(m);
//	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public Object put(String key, Object value) {
//		if (value instanceof Map) {
//			KeyMapping km = new KeyMapping();
//			km.setMapping(key);
//			return super.put(key, new ObjectMapper<>(km).map((Map<String, Object>) value));
//		} else if (value instanceof KeyMapping) {
//			return super.put(key, value);
//		} else {
//			throw new IllegalArgumentException("KeyMaps only accepts " + Map.class + " and " + KeyMapping.class + " as value.");
//		}
//	}

}
