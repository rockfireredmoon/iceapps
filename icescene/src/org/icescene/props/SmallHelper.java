package org.icescene.props;

import java.util.logging.Logger;

import org.icescene.entities.EntityContext;
import org.icescene.materials.WireframeWidget;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * The manipulator sceneryItem.
 */
public class SmallHelper extends AbstractProp {

	static final Logger LOG = Logger.getLogger(SmallHelper.class.getName());

	public SmallHelper(EntityContext app) {
		super("SmallHelper", app);
		spatial.setShadowMode(RenderQueue.ShadowMode.Off);
		spatial.setQueueBucket(RenderQueue.Bucket.Translucent);
	}

	@Override
	public void onConfigureProp() {
		Box box = new com.jme3.scene.shape.Box(16, 16, 16);
		Geometry geom = new Geometry("HelperGeom", box);
		geom.setMaterial(new WireframeWidget(context.getAssetManager(), ColorRGBA.Green));
		spatial.attachChild(geom);
		propSpatials.add(geom);

	}

	@Override
	public AbstractProp clone() {
		throw new UnsupportedOperationException();
	}
}