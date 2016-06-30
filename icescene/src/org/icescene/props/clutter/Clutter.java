package org.icescene.props.clutter;

import org.icescene.entities.EntityContext;
import org.icescene.props.AbstractProp;
import org.icescene.props.AbstractXMLProp;

import com.jme3.renderer.queue.RenderQueue;

public class Clutter extends AbstractXMLProp {

	public Clutter(String name, EntityContext app) {
		super(name, app);
		// TODO make tuneable
		spatial.setShadowMode(RenderQueue.ShadowMode.Off);
		spatial.setQueueBucket(RenderQueue.Bucket.Transparent);
	}

	@Override
	public AbstractProp clone() {
		Clutter p = new Clutter(spatial.getName(), context);
		p.spatial = spatial.clone(false);
		return p;
	}
}
