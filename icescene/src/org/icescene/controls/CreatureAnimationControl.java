package org.icescene.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.AbstractCreature;
import org.icelib.Icelib;
import org.icescene.SceneConstants;
import org.icescene.animation.AnimationSequence;
import org.icescene.audio.AudioAppState;
import org.icescene.audio.AudioQueue;
import org.icescene.configuration.creatures.SoundOption;
import org.icescene.entities.AbstractSpawnEntity;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class CreatureAnimationControl extends AbstractControl implements AnimEventListener {

//	public class AnimationRequest {
//
//		private String name;
//		private boolean loop;
//		private float speed;
//
//		public AnimationRequest(String name) {
//
//		}
//
//		public AnimationRequest(String name, boolean loop) {
//
//		}
//
//		public AnimationRequest(String name, boolean loop, float speed) {
//			this.name = name;
//			this.loop = loop;
//			this.speed = speed;
//		}
//	}

	public interface Listener {

		void sequenceStarted(CreatureAnimationControl control, AnimationSequence seq);

		void sequenceStopped(CreatureAnimationControl control, AnimationSequence seq);
	}

	private final static Logger LOG = Logger.getLogger(CreatureAnimationControl.class.getName());
	private AbstractCreature player;
	private AnimationSequence.SubGroup animationSubGroup;
	private AnimationSequence defaultAnim;
	private AnimControl animControl;
	protected Map<AnimationSequence.Part, AnimChannel> channels = new EnumMap<AnimationSequence.Part, AnimChannel>(
			AnimationSequence.Part.class);
	protected Map<AnimChannel, AnimationSequence> sequences = new HashMap<AnimChannel, AnimationSequence>();
	protected Map<AnimationSequence, List<String>> anims = new HashMap<AnimationSequence, List<String>>();
	private List<Listener> listeners = new ArrayList<Listener>();
	protected AbstractSpawnEntity entity;
	protected AppStateManager stateManager;

	public CreatureAnimationControl(AbstractSpawnEntity entity, AppStateManager stateManager) {
		this.entity = entity;
		this.stateManager = stateManager;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);

		// We can get animation control once attached to scene
		if (spatial == null && animControl != null) {

			animControl.removeListener(this);
			animControl = null;
		} else if (spatial != null && animControl == null) {

			player = getEntity().getCreature();
			animationSubGroup = player.getAppearance().getGender() == null ? null : AnimationSequence.SubGroup.fromGender(player
					.getAppearance().getGender());
			animControl = entity.getAnimControl();
			if (animControl == null) {
				LOG.warning(String.format("No animation control for %s", player.getDisplayName()));
			} else {
				animControl.addListener(this);
				LOG.info(String.format("Has %d animations", animControl.getAnimationNames().size()));
			}
		}
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
		super.render(rm, vp);
	}

	public AbstractSpawnEntity getEntity() {
		return entity;
	}

	public Collection<String> getAnims() {
		List<String> p = new ArrayList<String>();
		return (Collection<String>) (animControl == null ? Collections.emptyList() : animControl.getAnimationNames());
	}

	public void playSequence(String animName) {
		playSequence(animName, true);
	}

	public void playSequence(String animName, boolean blend) {

		// Stop current animation

		if (animControl != null) {
			// Get the animation sequence
			playSequence(AnimationSequence.get(animName), blend);

		}
	}

	public void setDefaultAnim(AnimationSequence anim) {
		this.defaultAnim = anim;
		LOG.info(String.format("Default animation is now %s", anim.getName()));
		if (defaultAnim != null && animControl != null) {
			playSequence(defaultAnim);
		}
	}

	public AnimationSequence getDefaultAnim() {
		return defaultAnim;
	}

	public CreatureAnimationControl playDefaultAnim() {
		if (defaultAnim != null) {
			playSequence(defaultAnim);
		}
		return this;
	}

	protected AnimationSequence.SubGroup getAnimationSubGroup() {
		return animationSubGroup;
	}

	protected void setAnimationSubGroup(AnimationSequence.SubGroup animationSubGroup) {
		this.animationSubGroup = animationSubGroup;
	}

	@Override
	public final void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
		AnimationSequence sequence = sequences.get(channel);
		List<String> remaining = anims.get(sequence);
		if (remaining != null) {
			if (channel.getLoopMode().equals(LoopMode.DontLoop)) {
				remaining.remove(animName);
				LOG.info(String.format("%s from animation sequence has completed, removing from list in sequence %s", animName,
						sequence.getName()));
			}
			if (remaining.isEmpty()) {
				LOG.info(String.format("%s sequence has completed.", sequence.getName()));

				// Clear up all the maps
				anims.remove(sequence);
				for (Iterator<Map.Entry<AnimChannel, AnimationSequence>> en = sequences.entrySet().iterator(); en.hasNext();) {
					Map.Entry<AnimChannel, AnimationSequence> e = en.next();
					if (e.getValue().equals(sequence)) {
						en.remove();
						for (Iterator<Map.Entry<AnimationSequence.Part, AnimChannel>> cen = channels.entrySet().iterator(); cen
								.hasNext();) {
							Map.Entry<AnimationSequence.Part, AnimChannel> ce = cen.next();
							if (ce.getValue().equals(e.getKey())) {
								cen.remove();
							}
						}
					}
				}
				// protected Map<AnimationSequence.Part, AnimChannel> channels =
				// new EnumMap<AnimationSequence.Part,
				// AnimChannel>(AnimationSequence.Part.class);
				// protected Map<AnimChannel, AnimationSequence> sequences = new
				// HashMap<AnimChannel, AnimationSequence>();

				for (int i = listeners.size() - 1; i >= 0; i--) {
					listeners.get(i).sequenceStopped(this, sequence);
				}
				onSequenceDone(sequence);
			}

			if (anims.isEmpty()) {
				animControl.clearChannels();
			}
		}
	}

	public void onSequenceDone(AnimationSequence sequence) {
	}

	@Override
	public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
	}

	@Override
	protected void controlUpdate(float tpf) {
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
	}

	public void playAnimation(String animation, boolean loop, float speed) {
		AnimationSequence seq = new AnimationSequence(animation);
		final AnimationSequence.Anim anim = new AnimationSequence.Anim(AnimationSequence.SubGroup.NONE, AnimationSequence.Part.ALL,
				animation);
		anim.setLoopMode(loop ? LoopMode.Loop : LoopMode.DontLoop);
		anim.setSpeed(speed);
		seq.addAnim(anim);
		playSequence(seq);
	}

	public void stopSequence() {
		final Set<Map.Entry<AnimationSequence, List<String>>> entrySet = new HashSet<Map.Entry<AnimationSequence, List<String>>>(
				anims.entrySet());
		anims.clear();
		channels.clear();
		sequences.clear();
		animControl.clearChannels();
		for (Map.Entry<AnimationSequence, List<String>> en : entrySet) {
			for (int i = listeners.size() - 1; i >= 0; i--) {
				listeners.get(i).sequenceStopped(this, en.getKey());
			}
			onSequenceDone(en.getKey());
		}
	}

	public boolean isAnyActive() {
		return !anims.isEmpty();
	}

	public void playSequence(AnimationSequence sequence) {
		playSequence(sequence, true);
	}

	public void playSequence(AnimationSequence sequence, boolean forceLoop, float speed) {
		playSequence(sequence, true, forceLoop, speed);
	}

	public void playSequence(AnimationSequence sequence, boolean blend) {
		playSequence(sequence, blend, false, 1f);
	}

	public void playSequence(AnimationSequence sequence, boolean blend, boolean forceLoop, float speed) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Play animation %s", sequence.getName()));
		}
		if (animControl == null) {
			LOG.warning("Cannot play animation sequence, spatial has no animation control.");
			return;
		}

		// Make sure there is a channel for each part that needs to be animated
		List<AnimationSequence.Anim> sequenceAnims = sequence.anims(getAnimationSubGroup());
		for (AnimationSequence.Anim a : sequenceAnims) {
			AnimChannel ch = channels.get(a.getPart());
			if (ch == null) {
				ch = animControl.createChannel();
				channels.put(a.getPart(), ch);
			}
			sequences.put(ch, sequence);
		}

		// Remove any channels we don't need any more (not sure if this is
		// required, but keeps it tidy :)
		for (Iterator<Map.Entry<AnimationSequence.Part, AnimChannel>> it = channels.entrySet().iterator(); it.hasNext();) {
			Map.Entry<AnimationSequence.Part, AnimChannel> en = it.next();
			if (!sequence.hasAnimForPart(getAnimationSubGroup(), en.getKey())) {
				it.remove();
			}
		}

		List<String> remainingAnims = new ArrayList<String>();

		// Set the animation for each channel
		for (AnimationSequence.Anim a : sequenceAnims) {
			float animSpeed = SceneConstants.GLOBAL_SPEED_FACTOR * BipedAnimationHandler.ANIM_GLOBAL_SPEED * Icelib.getSpeedFactor(player.getSpeed())
					* a.getSpeed() * speed;
			AnimChannel ch = channels.get(a.getPart());
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Playing animation %s (current speed %4.2f, setting to %4.2f)", a, ch.getSpeed(), animSpeed));
			}
			if (ch.getAnimationName() == null || !ch.getAnimationName().equals(a.getAnimName())) {
				try {
					remainingAnims.add(a.getAnimName());
					ch.setAnim(a.getAnimName(), blend ? a.getBlendTime() / SceneConstants.GLOBAL_SPEED_FACTOR : 0.00001f);
					ch.setLoopMode(forceLoop ? LoopMode.Loop : a.getLoopMode());
					ch.setSpeed(animSpeed);
				} catch (IllegalArgumentException iae) {
					LOG.warning(String.format("Animation %s doesn't exist for sequence %s.", a.getAnimName(), sequence.getName()));
				}
			}
		}

		if (!remainingAnims.isEmpty()) {
			anims.put(sequence, remainingAnims);
		}

		AudioAppState aas = stateManager.getState(AudioAppState.class);
		if (aas != null) {
			for (SoundOption so : sequence.getSounds()) {
				aas.queue(AudioQueue.COMBAT, this, so.getName(), so.getTime(), 1.0f);
			}
		}

		//
		if (!sequenceAnims.isEmpty()) {
			for (int i = listeners.size() - 1; i >= 0; i--) {
				listeners.get(i).sequenceStarted(this, sequence);
			}
		}
	}
}
