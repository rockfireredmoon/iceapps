package org.icescene.controls;

import java.util.Arrays;
import java.util.logging.Logger;

import org.icelib.Icelib;
import org.icescene.SceneConstants;
import org.icescene.animation.AnimationOption;
import org.icescene.configuration.creatures.ModelDefinition;
import org.icescene.entities.EntityContext;
import org.icescene.scene.creatures.CreatureEntity;

import com.jme3.animation.AnimChannel;

/**
 * Handles animation for the 'Horde', basically this is the standard handler for
 * all creatures that are not Bipeds.
 */
public class HordeAnimationHandler extends AbstractCreatureAnimationHandler<ModelDefinition, CreatureEntity> {

	private final static Logger LOG = Logger.getLogger(HordeAnimationHandler.class.getName());

	private AnimChannel activeChannel;

	public HordeAnimationHandler(CreatureEntity entity, EntityContext context) {
		super(Arrays.asList(BipedAnimationHandler.ANIM_IDLE), entity, context);
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

			active = request;

			boolean wasNew = false;
			if (!animControl.isEnabled()) {
				animControl.setEnabled(true);
				wasNew = true;
			}

			if (!hasChannels()) {
				activeChannel = animControl.createChannel();
				wasNew = true;
			}

			try {
				activeChannel.setAnim(name, opt.getBlend() / SceneConstants.GLOBAL_SPEED_FACTOR);
			} catch (IllegalArgumentException iae) {
				LOG.warning(String.format("No animation %s in %s", name, definition.getKey()));
				for (String s : Icelib.sort(animControl.getAnimationNames())) {
					LOG.warning(String.format(">>> %s", s));
				}
			}

			animating = true;
			updateSpeed();
			configureLoops(activeChannel);

			if (wasNew) {
				fireAnimationStarted(opt);
			} else {
				fireAnimationChanged(opt);
			}

			postPlay();
		}
	}

	protected void updateSpeed() {
		updateSpeed(activeChannel);
	}

	@Override
	protected boolean hasChannels() {
		return activeChannel != null;
	}

}
