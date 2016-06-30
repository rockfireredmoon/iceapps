package org.icescene.animation;

import org.icelib.beans.MappedMap;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.Service;

@Service
public class AnimationDefs extends MappedMap<String, AnimationDef> implements IcesceneService {

	private static final long serialVersionUID = 1L;

	public AnimationDefs() {
		super(String.class, AnimationDef.class);
	}

	@Override
	public void init(IcesceneApp app) {
	}
}
