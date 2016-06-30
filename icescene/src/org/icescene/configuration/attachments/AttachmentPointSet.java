package org.icescene.configuration.attachments;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import org.icelib.AttachmentPoint;
import org.icelib.beans.MappedMap;
import org.icelib.beans.ObjectMapper;

public class AttachmentPointSet implements Serializable {

	private static final long serialVersionUID = 1L;

	public static AttachmentPointSet create(Map<String, Object> map) throws Exception {
		return new ObjectMapper<AttachmentPointSet>(new AttachmentPointSet()).map(map);
	}

	String key;

	Map<AttachmentPoint, AttachmentPlace> places = new MappedMap<AttachmentPoint, AttachmentPlace>(
			new EnumMap<AttachmentPoint, AttachmentPlace>(AttachmentPoint.class), AttachmentPoint.class, AttachmentPlace.class);

	public AttachmentPointSet() {
	}

	public AttachmentPointSet(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Map<AttachmentPoint, AttachmentPlace> getPlaces() {
		return places;
	}

	// public void setPlaces(Map<AttachmentPoint, AttachmentPlace> places) {
	// for(Map.Entry en : places.entrySet()) {
	// if(en.getValue() instanceof )
	// }
	// this.places = new MappedMap<AttachmentPoint, AttachmentPlace>(places,
	// AttachmentPoint.class, AttachmentPlace.class);
	// }

	@Override
	public AttachmentPointSet clone() {
		AttachmentPointSet set = new AttachmentPointSet();
		set.places.putAll(places);
		return set;
	}

	@Override
	public String toString() {
		return "AttachmentPointSet{" + "key=" + key + ", places=" + places + '}';
	}
}