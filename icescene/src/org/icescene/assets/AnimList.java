package org.icescene.assets;

import java.util.ArrayList;

import com.jme3.animation.Animation;
import com.jme3.asset.AssetKey;
import com.jme3.asset.CloneableSmartAsset;

public class AnimList extends ArrayList<Animation> implements CloneableSmartAsset, Cloneable {

	private AssetKey key;

	public AnimList() {
	}

	@Override
	public void setKey(AssetKey key) {
		this.key = key;
	}

	@Override
	public AssetKey getKey() {
		return key;
	}

	@Override
	public AnimList clone() {
		AnimList l = new AnimList();
		l.addAll(this);
		return l;
	}
}
