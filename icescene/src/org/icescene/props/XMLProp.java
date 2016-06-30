package org.icescene.props;

import org.icescene.entities.EntityContext;

import com.jme3.renderer.queue.RenderQueue;

public class XMLProp extends AbstractXMLProp {

	public XMLProp(final String name, final EntityContext app) {
		super(name, app);
		spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
	}

	@Override
	public AbstractProp clone() {
		XMLProp p = new XMLProp(spatial.getName(), context);
		p.sceneryItem = sceneryItem;
		p.spatial = spatial.clone(false);
		return p;
	}

}
