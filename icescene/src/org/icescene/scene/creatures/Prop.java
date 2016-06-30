package org.icescene.scene.creatures;

import java.util.Map;

import org.icelib.AbstractCreature;
import org.icelib.RGB;
import org.icescene.entities.AbstractSpawnEntity;
import org.icescene.entities.EntityContext;
import org.icescene.props.AbstractProp;

public class Prop extends AbstractSpawnEntity {
	protected final static String PATH = "Prop";

	public Prop(EntityContext context, AbstractCreature creature, AbstractProp prop,
			String entityInstanceId) {
		super(context, creature, PATH, entityInstanceId);
		this.bodyMesh = prop.getSpatial();
	}

	@Override
	protected Map<String, RGB> createColorMap() {
		return null;
	}

//	@Override
//	public Collection<AnimationSequence> getAnimationPresets() {
//		return Collections.emptyList();
//	}
}
