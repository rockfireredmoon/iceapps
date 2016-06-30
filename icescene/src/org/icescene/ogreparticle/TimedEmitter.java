package org.icescene.ogreparticle;

import com.jme3.math.FastMath;

import emitter.Emitter;

/**
 * Extension to {@link Emitter} that allows a duration (or random duration
 * range)
 * to be set for all particles. Once this time is reached, no more particles
 * will
 * be emitted, but current ones will continue till they naturally die. A repeat
 * time
 * (or random repeat range) can be set. Once expired, the emitter will wait for
 * this
 * duration, then start emitting again. This is to mimic one of the features of
 * the OGRE
 * particle system.
 * 
 * @author rockire
 */
public class TimedEmitter extends Emitter {
	private long expire = Long.MAX_VALUE;
	private long repeatAt = Long.MAX_VALUE;
	private boolean emit = true;
	private float durationMin;
	private float durationMax;
	private float repeatDelayMin;
	private float repeatDelayMax;
	private boolean durationSet;
	private float timescale = 1f;

	public void setDurationMin(float seconds) {
		durationMin = seconds;
		durationSet = true;
	}

	public void setDurationMax(float seconds) {
		durationMax = seconds;
		durationSet = true;
	}

	public void setDuration(float seconds) {
		durationMin = durationMax = seconds;
		durationSet = true;
	}

	public void setRepeatDelayMin(float seconds) {
		repeatDelayMin = seconds;
	}

	public void setRepeatDelayMax(float seconds) {
		repeatDelayMax = seconds;
	}

	public void setRepeat(float seconds) {
		repeatDelayMin = repeatDelayMax = seconds;
	}

	@Override
	public void emitNextParticle() {
		long now = System.currentTimeMillis();
		if (!emit && now > repeatAt) {
			// Now it's time to repeat
			repeatAt = Long.MAX_VALUE;
			calcNewExpire(now);
			emit = true;
		} else if (emit && now > expire) {
			// Expired, stop emitting
			if (repeatDelayMin > 0 || repeatDelayMax > 0) {
				// Repeat
				calcNewRepeat(now);
			}
			emit = false;
		} else if (expire == Long.MAX_VALUE && durationSet) {
			// Calculate first expire
			calcNewExpire(now);
		}
		if (emit) {
			super.emitNextParticle();
		}
	}

	private void calcNewExpire(long now) {
		if (timescale == 0)
			expire = Long.MAX_VALUE;
		else {
			float delayMs = (durationMin + ((durationMax - durationMin) * FastMath.rand.nextFloat())) * 1000f / timescale;
			expire = (long) delayMs + now;
		}
	}

	private void calcNewRepeat(long now) {
		if (timescale == 0)
			repeatAt = Long.MAX_VALUE;
		else {
			float delayMs = (repeatDelayMin + ((repeatDelayMax - repeatDelayMin) * FastMath.rand.nextFloat())) * 1000f / timescale;
			repeatAt = (long) delayMs + now;
		}
	}

	@Override
	public void update(float tpf) {
		super.update(tpf * timescale);
	}

	public void setTimeScale(float ts) {
		timescale = ts;
	}

}
