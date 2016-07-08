package org.icescene.controls;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.icelib.Icelib;
import org.icescene.Alarm.AlarmTask;
import org.icescene.SceneConstants;
import org.icescene.animation.AnimationOption;
import org.icescene.audio.AudioAppState;
import org.icescene.audio.AudioQueue;
import org.icescene.configuration.creatures.AbstractCreatureDefinition;
import org.icescene.configuration.creatures.SoundOption;
import org.icescene.entities.AbstractCreatureEntity;
import org.icescene.entities.EntityContext;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.math.FastMath;

/**
 * Handles animation for the 'Horde', basically this is the standard handler for
 * all creatures that are not Bipeds.
 */
public abstract class AbstractCreatureAnimationHandler<T extends AbstractCreatureDefinition, K extends AbstractCreatureEntity<T>>
		extends AnimationHandler<T, K> {

	private final static Logger LOG = Logger.getLogger(AbstractCreatureAnimationHandler.class.getName());

	private AlarmTask ambientChange;
	private AnimationOption idle;
	private List<String> defaultLoops;

	public AbstractCreatureAnimationHandler(List<String> defaultLoops, K entity, EntityContext context) {
		super(entity, context);
		this.defaultLoops = defaultLoops;
	}

	protected void updateSpeed(AnimChannel ch) {
		ch.setSpeed(SceneConstants.GLOBAL_SPEED_FACTOR * BipedAnimationHandler.ANIM_GLOBAL_SPEED
				* Icelib.getSpeedFactor(entity.getCreature().getSpeed()) * speed);
	}

	protected boolean isDefaultLoop(String name) {
		return defaultLoops.contains(name);
	}

	public Map<String, AnimationOption> getAnimations() {
		T definition = entity.getDefinition();
		if (definition == null) {
			LOG.warning(String.format("No model definition for %s", entity));
			return Collections.emptyMap();
		}
		return definition.getAnimations();
	}

	@Override
	public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
		if (channel.getLoopMode().equals(LoopMode.DontLoop)) {
			if (hasChannels() && !isTerminal(animName)) {
				playIdle();
			} else {
				animating = false;
				active = null;
			}
		}
	}

	public void playIdle() {
		// TODO set Combat_Idle if in combat
		if (idle != null)
			play(new AnimationRequest(idle));
	}

	@Override
	protected void onCleanUp() {
		cancelAmbientChange();
	}

	@Override
	protected void onInit() {
		super.onInit();
		Map<String, AnimationOption> anims = getAnimations();
		idle = anims.get("Idle");
		playIdle();
	}

	protected void postPlay() {
		if (isIdle(active.getName().getKey())) {
			// Trigger ambient changes when idling
			scheduleAmbientChange();
		} else if (!isIdleStateAnimation(active.getName().getKey())) {
			// All other animations should not trigger ambient animation
			// changes
			cancelAmbientChange();
		}

		AudioAppState aas = context.getStateManager().getState(AudioAppState.class);
		if (aas != null) {
			Map<String, SoundOption> sounds = active.getName().getSounds();
			for (SoundOption so : sounds.values()) {
				try {
					aas.queue(AudioQueue.COMBAT, this, so.getName(), so.getTime(), 1.0f);
				}
				catch(IllegalStateException ise) {
					LOG.warning(String.format("%s is already queued to play", so.getName()));
				}
			}
		}
	}

	protected void configureLoops(AnimChannel ch) {
		// NOTE: HAVE to set loop mode after setting animation
		if (active.getLoop() != null) {
			if (active.getLoop()) {
				ch.setLoopMode(LoopMode.Loop);
			} else {
				ch.setLoopMode(LoopMode.DontLoop);
			}
		} else {
			if (isDefaultLoop(active.getName().getKey())) {
				ch.setLoopMode(LoopMode.Loop);
			} else {
				ch.setLoopMode(LoopMode.DontLoop);
			}
		}
	}

	protected boolean isTerminal(String name) {
		return name.equals("Death");
	}

	protected boolean isIdle(String name) {
		return name.equals("Idle") || name.equals("Combat_Idle");
	}

	protected boolean isIdleStateAnimation(String name) {
		return isIdle(name) || entity.getDefinition().getAmbient().contains(name);
	}

	protected void cancelAmbientChange() {
		if (ambientChange != null) {
			// Icelib.removeMe("Cancelled ambient change");
			ambientChange.cancel();
			ambientChange = null;
		}
	}

	protected void scheduleAmbientChange() {
		final List<String> ambient = entity.getDefinition().getAmbient();
		if (ambient == null || ambient.isEmpty())
			return;
		cancelAmbientChange();
		float time = 3 + (FastMath.rand.nextFloat() * 30f);
		LOG.info(String.format("Scheduling ambient change for %3.1f seconds", time));
		ambientChange = context.getAlarm().timed(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ambientChange = null;
				String ambientName = ambient.get((int) (Math.random() * (double) ambient.size()));
				AnimationOption anim = getAnimations().get(ambientName);
				if (anim == null) {
					LOG.warning(String.format("No animation name %s", ambientName));
				} else {
					play(new AnimationRequest(anim));
				}
				return null;
			}
		}, time);
	}

	@Override
	protected void preStop() {
		cancelAmbientChange();
	}
}
