package org.icescene.props;

import org.icescene.SceneConfig;
import org.icescene.assets.ExtendedMaterialListKey;
import org.icescene.entities.EntityContext;

public class ClutterProp extends XMLProp {

	public ClutterProp(final String name, final EntityContext app) {
		super(name, app);
	}

	@Override
	protected void configureMeshMaterialKey(ExtendedMaterialListKey mk) {
		super.configureMeshMaterialKey(mk);
		mk.setLighting(context.getPreferences().getBoolean(SceneConfig.TERRAIN_LIT, SceneConfig.TERRAIN_LIT_DEFAULT) ? ExtendedMaterialListKey.Lighting.LIT
				: ExtendedMaterialListKey.Lighting.UNLIT);
	}

	@Override
	public AbstractProp clone() {
		ClutterProp p = new ClutterProp(spatial.getName(), context);
		p.sceneryItem = sceneryItem;
		p.spatial = spatial.clone(false);
		return p;
	}

}
