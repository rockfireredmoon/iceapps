package org.icescene.controls;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.icelib.Icelib;
import org.icescene.SceneConstants;
import org.icescene.animation.AnimationOption;
import org.icescene.configuration.creatures.CreatureDefinition;
import org.icescene.entities.EntityContext;
import org.icescene.scene.creatures.Biped;

import com.jme3.animation.AnimChannel;

public class BipedAnimationHandler extends AbstractCreatureAnimationHandler<CreatureDefinition, Biped> {

	private final static Logger LOG = Logger.getLogger(BipedAnimationHandler.class.getName());
	private AnimChannel topChannel;
	private AnimChannel bottomChannel;
	private AnimChannel handChannel;
	/**
	 * Animation name for float walk
	 */
	public static String ANIM_FLOAT_WALK = "FloatWalk";
	/**
	 * Speed factor for all animations. Ordinary would be 1.0f. Adjusting this
	 * directly
	 * instead of GLOBAL_SPEED_FACTOR would leave animations not matching
	 * physical
	 * movement speed.
	 */
	public static float ANIM_GLOBAL_SPEED = 1.6F;
	/**
	 * Animation name for idle
	 */
	public static String ANIM_IDLE = "Idle";
	/**
	 * Animation name for idle rotate right
	 */
	public static String ANIM_IDLE_ROTATE_RIGHT = "IdleRotateRight";
	/**
	 * Default amount of time it takes to fade from one animation to next
	 */
	public static float ANIM_DEFAULT_BLEND_TIME = 0.25F;
	/**
	 * Animation name for idle rotate left
	 */
	public static String ANIM_IDLE_ROTATE_LEFT = "IdleRotateLeft";
	/**
	 * Animation name for interact
	 */
	public static String ANIM_INTERACT = "Interact";
	/**
	 * Animation name for jog strafe right
	 */
	public static String ANIM_JOG_STRAFE_RIGHT = "Jog_Strafe_Right";
	//
	// These "34" .. i think this means 3/4 turn?
	//
	/**
	 * Animation name for jog34 left (not sure what this is yet)
	 */
	public static String ANIM_JOG34_LEFT = "Jog34_L";
	/**
	 * Animation name for jog
	 */
	public static String ANIM_JOG = "Jog";
	/**
	 * Animation name for jog strafe left
	 */
	public static String ANIM_JOG_STRAFE_LEFT = "Jog_Strafe_Left";
	/**
	 * Animation name for jog backward
	 */
	public static String ANIM_JOG_BACKWARD = "Jog_Backward";
	/**
	 * Animation name for jog34 right (not sure what this is yet)
	 */
	public static String ANIM_JOG34_RIGHT = "Jog34_R";
	/**
	 * Animation name for jog34 back (not sure what this is yet)
	 */
	public static String ANIM_JOG34_BACK_LEFT = "Jog34_Back_L";
	/**
	 * Animation name for jog34 back (not sure what this is yet)
	 */
	public static String ANIM_JOG34_BACK_RIGHT = "Jog34_Back_R";
	/**
	 * Animation name for jumping when standing (while rising)
	 */
	public static String ANIM_JUMP_STANDING_UP = "JumpStandingUp";
	/**
	 * Animation name for jumping when standing (while falling)
	 */
	public static String ANIM_JUMP_STANDING_DOWN = "JumpStandingDown";
	/**
	 * Animation name for jumping when standing (when at peak)
	 */
	public static String ANIM_JUMP_STANDING_TOP = "JumpStandingTop";
	/**
	 * Animation name for running
	 */
	public static String ANIM_RUN = "Run";
	/**
	 * Animation name for run34 left (not sure what this is yet)
	 */
	public static String ANIM_RUN34_LEFT = "Run34_L";
	/**
	 * Animation name for run34 right (not sure what this is yet)
	 */
	public static String ANIM_RUN34_RIGHT = "Run34_R";
	/**
	 * Animation name for swim right
	 */
	public static String ANIM_SWIM_RIGHT = "SwimRight";
	/**
	 * Animation name for swim forward left
	 */
	public static String ANIM_SWIM_FORWARD_LEFT = "SwimForwardLeft";
	/**
	 * Animation name for jumping when standing (when recovering)
	 */
	public static String ANIM_JUMP_STANDING_RECOVER = "JumpStandingRecover";
	/**
	 * Animation name for swim back
	 */
	public static String ANIM_SWIM_BACK = "SwimBack";
	/**
	 * Animation name for swim forward
	 */
	public static String ANIM_SWIM_FORWARD = "SwimForward";
	/**
	 * Animation name for swim forward left
	 */
	public static String ANIM_SWIM_FORWARD_RIGHT = "SwimForwardRight";
	/**
	 * Animation name for swim left
	 */
	public static String ANIM_SWIM_LEFT = "SwimLeft";
	/**
	 * Animation name for swim tread
	 */
	public static String ANIM_SWIM_TREAD = "SwimTread";
	/**
	 * Animation name for walk forward
	 */
	public static String ANIM_WALK = "Walk";
	/**
	 * Animation name for walk34 back (not sure what this is yet)
	 */
	public static String ANIM_WALK34_BACK_LEFT = "Walk34_Back_L";
	/**
	 * Animation name for walk34 back (not sure what this is yet)
	 */
	public static String ANIM_WALK34_BACK_RIGHT = "Walk34_Back_R";
	/**
	 * Animation name for walk34 left (not sure what this is yet)
	 */
	public static String ANIM_WALK34_LEFT = "Walk34_L";
	/**
	 * Animation name for walk34 right (not sure what this is yet)
	 */
	public static String ANIM_WALK34_RIGHT = "Walk34_R";
	/**
	 * Animation name for walk backward
	 */
	public static String ANIM_WALK_BACKWARD = "Walk_Backward";
	/**
	 * Animation name for walk strafe left
	 */
	public static String ANIM_WALK_STRAFE_LEFT = "Walk_Strafe_Left";
	/**
	 * Animation name for walk strafe right
	 */
	public static String ANIM_WALK_STRAFE_RIGHT = "Walk_Strafe_Right";

	public static List<String> DEFAULT_BIPED_LOOPS = Arrays.asList(BipedAnimationHandler.ANIM_IDLE);

	public BipedAnimationHandler(Biped entity, EntityContext context) {
		super(DEFAULT_BIPED_LOOPS, entity, context);
	}

	@Override
	public void play(AnimationRequest request) {

		String name = request.getName().getKey();

		AnimationOption opt = getAnimations().get(name);
		CreatureDefinition definition = entity.getDefinition();

		if (opt == null) {
			LOG.warning(String.format("No animation def %s in %s", name, definition.getKey()));
			for (String k : getAnimations().keySet())
				LOG.warning(String.format(">>> %s", k));
		} else {

			LOG.info(String.format("Playing %s (category %s)", name, opt.getCategory()));
			active = request;

			boolean wasNew = false;
			if (!animControl.isEnabled()) {
				animControl.setEnabled(true);
				wasNew = true;
			}

			if (!hasChannels()) {
				handChannel = animControl.createChannel();
				bottomChannel = animControl.createChannel();
				topChannel = animControl.createChannel();
				wasNew = true;
			}

			try {
				handChannel.setAnim(name + "_h", opt.getBlend() / SceneConstants.GLOBAL_SPEED_FACTOR);
				bottomChannel.setAnim(name + "_b", opt.getBlend() / SceneConstants.GLOBAL_SPEED_FACTOR);
				topChannel.setAnim(name + "_t", opt.getBlend() / SceneConstants.GLOBAL_SPEED_FACTOR);
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
			configureLoops(handChannel);

			if (wasNew) {
				fireAnimationStarted(opt);
			} else {
				fireAnimationChanged(opt);
			}

			postPlay();
		}
	}

	@Override
	protected boolean isDefaultLoop(String name) {
		AnimationOption opt = getAnimations().get(name);
		return opt.getCategory().equals("Movement") || super.isDefaultLoop(name);
	}

	protected void updateSpeed() {
		updateSpeed(topChannel);
		updateSpeed(bottomChannel);
		updateSpeed(handChannel);
	}

	@Override
	protected boolean hasChannels() {
		return topChannel != null;
	}

}
