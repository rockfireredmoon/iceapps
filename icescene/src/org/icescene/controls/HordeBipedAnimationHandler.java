package org.icescene.controls;

import java.util.logging.Logger;

import org.icelib.Icelib;
import org.icescene.SceneConstants;
import org.icescene.animation.AnimationOption;
import org.icescene.configuration.creatures.ModelDefinition;
import org.icescene.entities.EntityContext;
import org.icescene.scene.creatures.CreatureEntity;

import com.jme3.animation.AnimChannel;

public class HordeBipedAnimationHandler extends AbstractCreatureAnimationHandler<ModelDefinition, CreatureEntity> {

	private final static Logger LOG = Logger.getLogger(HordeAnimationHandler.class.getName());

	private AnimChannel topChannel;
	private AnimChannel bottomChannel;

	public HordeBipedAnimationHandler(CreatureEntity entity, EntityContext context) {
		super(BipedAnimationHandler.DEFAULT_BIPED_LOOPS, entity, context);
	}

	@Override
	public void play(AnimationRequest request) {

		String name = request.getName().getKey();

		AnimationOption opt = getAnimations().get(name);
		ModelDefinition definition = entity.getDefinition();

		if (opt == null) {
			LOG.warning(String.format("No animation def %s in %s", name, definition.getKey()));
			for (String k : getAnimations().keySet())
				LOG.warning(String.format(">>> %s", k));
		} else {

			LOG.info(String.format("Playing %s", name));
			active = request;

			boolean wasNew = false;
			if (!animControl.isEnabled()) {
				animControl.setEnabled(true);
				wasNew = true;
			}

			if (!hasChannels()) {
				topChannel = animControl.createChannel();
				bottomChannel = animControl.createChannel();
				wasNew = true;
			}

			try {
				topChannel.setAnim(name + "_t", opt.getBlend() / SceneConstants.GLOBAL_SPEED_FACTOR);
				bottomChannel.setAnim(name + "_b", opt.getBlend() / SceneConstants.GLOBAL_SPEED_FACTOR);
			} catch (IllegalArgumentException iae) {
				LOG.warning(String.format("No animation %s in %s", name, definition.getKey()));
				for (String s : Icelib.sort(animControl.getAnimationNames())) {
					LOG.warning(String.format(">>> %s", s));
				}
			}

			animating = true;
			updateSpeed();

			configureLoops(topChannel);
			configureLoops(bottomChannel);

			if (wasNew) {
				fireAnimationStarted(opt);
			} else {
				fireAnimationChanged(opt);
			}

			postPlay();
		}
	}

	protected void updateSpeed() {
		updateSpeed(topChannel);
		updateSpeed(bottomChannel);
	}

	@Override
	protected boolean hasChannels() {
		return topChannel != null;
	}

}
