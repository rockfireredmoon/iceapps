package org.icescene.configuration.creatures;

import icemoon.iceloader.ServerAssetManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.icelib.Icelib;

import com.jme3.asset.AssetManager;

public class CreatureKey implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String type;
	private final String name;

	public CreatureKey(String fullName) {
		int typeIdx = fullName.indexOf('-');
		if (typeIdx == -1) {
			throw new IllegalArgumentException(String.format("Invalid creature key %s", fullName));
		}
		type = fullName.substring(0, typeIdx);
		name = fullName.substring(typeIdx + 1);
	}

	public CreatureKey(String type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreatureKey other = (CreatureKey) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String getText() {
		return StringUtils.isBlank(name) ? (type == null ? "" : type) : Icelib.toEnglish(name);
	}

	public String getFullName() {
		return StringUtils.isBlank(type) ? (name == null ? "" : name) : (type + "-" + name);
	}

	public String getPath() {
		// TODO correct dir
		return String.format("%1$s/%1$s-%2$s/%1$s-%2$s", type, name);
	}

	public static CreatureKey fromPath(String path) {
		return new CreatureKey(FilenameUtils.getBaseName(path));
	}

	public static Collection<? extends CreatureKey> getAll(String type, AssetManager assetManager) {
		List<CreatureKey> l = new ArrayList<>();
		for (String path : ((ServerAssetManager) assetManager).getAssetNamesMatching(String.format("%s/.*\\.js", type))) {
			l.add(CreatureKey.fromPath(path));
		}
		return l;
	}

	@Override
	public String toString() {
		return "CreatureKey [type=" + type + ", name=" + name + "]";
	}
}