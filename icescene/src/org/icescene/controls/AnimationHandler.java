package org.icescene.controls;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.icescene.animation.AnimationOption;
import org.icescene.configuration.creatures.AbstractCreatureDefinition;
import org.icescene.entities.AbstractCreatureEntity;
import org.icescene.entities.EntityContext;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public abstract class AnimationHandler<D extends AbstractCreatureDefinition, C extends AbstractCreatureEntity<D>> extends
		AbstractControl implements AnimEventListener {
	private final static Logger LOG = Logger.getLogger(AnimationHandler.class.getName());

	public interface Listener {
		void animationStarted(AnimationOption name);

		void animationChanged(AnimationOption name);

		void animationStopped();
	}

	protected C entity;
	protected AnimationRequest active;
	protected EntityContext context;
	protected AnimControl animControl;
	protected boolean animating;
	private List<Listener> listeners = new LinkedList<Listener>();
	protected float speed = 1f;

	public void setSpeed(float speed) {
		if (this.speed != speed) {
			this.speed = speed;
			if (hasChannels())
				updateSpeed();
		}
	}

	protected AnimationHandler(C entity, EntityContext context) {
		this.entity = entity;
		this.context = context;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@Override
	public final void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		if (spatial == null && animControl != null) {
			animControl.removeListener(this);
			if (isAnimating()) {
				stop();
			}
			onCleanUp();
			animControl = null;
		} else if (spatial != null && animControl == null) {
			animControl = entity.getAnimControl();
			if (animControl == null) {
				LOG.warning(String.format("No animation control for %s", entity.getCreature().getDisplayName()));
			} else {
				animControl.addListener(this);
				onInit();
				LOG.info(String.format("Has %d animations in the control, and %d in the definition", animControl.getAnimationNames().size(), getAnimations().size()));
			}
		}
	}

	public C getEntity() {
		return entity;
	}

	public abstract void play(AnimationRequest request);

	@Override
	public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
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

	protected void onInit() {
	}

	protected void onCleanUp() {
	}

	public boolean isAnimating() {
		return animating && isEnabled();
	}

	public final void stop() {
		if (isAnimating()) {
			preStop();
			animControl.setEnabled(false);
			active = null;
			fireAnimationStopped();
		}
	}

	protected void preStop() {
	}

	protected void fireAnimationStarted(AnimationOption name) {
		for (Listener l : listeners) {
			l.animationStarted(name);
		}
	}

	protected void fireAnimationStopped() {
		for (Listener l : listeners) {
			l.animationStopped();
		}
	}

	protected void fireAnimationChanged(AnimationOption name) {
		for (Listener l : listeners) {
			l.animationChanged(name);
		}
	}

	public AnimationRequest getActive() {
		return active;
	}

	public abstract Map<String, AnimationOption> getAnimations();

	protected abstract boolean hasChannels();

	protected abstract void updateSpeed();

}
