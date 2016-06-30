package org.icescene.animation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.icelib.Appearance;
import org.icescene.configuration.creatures.SoundOption;
import org.icescene.controls.BipedAnimationHandler;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.FilterBuilder;

import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetNotFoundException;

public class AnimationSequence implements Comparable<AnimationSequence> {

	public enum Part {

		BOTTOM, TOP, HEAD, ALL
	}

	public enum SubGroup {

		MALE, FEMALE, NONE;

		public static SubGroup fromGender(Appearance.Gender gender) {
			switch (gender) {
			case FEMALE:
				return SubGroup.FEMALE;
			case MALE:
				return SubGroup.MALE;
			default:
				return SubGroup.NONE;
			}
		}
	}

	public static Collection<AnimationSequence> list() {
		Reflections reflections = new Reflections(ClasspathHelper.forPackage(AnimationSequence.class.getPackage().getName()),
				new SubTypesScanner(), new FilterBuilder().includePackage(AnimationSequence.class));
		Set<Class<? extends AnimationSequence>> anims = reflections.getSubTypesOf(AnimationSequence.class);
		List<AnimationSequence> l = new ArrayList<AnimationSequence>();
		for (Class<? extends AnimationSequence> c : anims) {
			try {
				l.add(c.newInstance());
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		Collections.sort(l);
		return l;

	}

	public static AnimationSequence get(String name) {
		try {
			return (AnimationSequence) Class.forName(AnimationSequence.class.getPackage().getName() + "." + name, true,
					AnimationSequence.class.getClassLoader()).newInstance();
		} catch (Exception e) {
			throw new AssetNotFoundException(String.format("No such animation sequence as %s.", name));
		}
	}

	public static class Anim {

		private final Part part;
		private String animName;
		private LoopMode loopMode = LoopMode.DontLoop;
		private float speed = 1f;
		private final SubGroup subGroup;
		private float blendTime = BipedAnimationHandler.ANIM_DEFAULT_BLEND_TIME;
		private AnimationSequence sequence;

		public Anim(SubGroup subGroup, Part part, String animName) {
			this.part = part;
			this.subGroup = subGroup;
			this.animName = animName;
		}

		@Override
		public Anim clone() {
			Anim a = new Anim(subGroup, part, animName);
			a.loopMode = loopMode;
			a.speed = speed;
			a.sequence = sequence;
			return a;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 53 * hash + (this.part != null ? this.part.hashCode() : 0);
			hash = 53 * hash + (this.animName != null ? this.animName.hashCode() : 0);
			hash = 53 * hash + (this.subGroup != null ? this.subGroup.hashCode() : 0);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Anim other = (Anim) obj;
			if (this.part != other.part) {
				return false;
			}
			if ((this.animName == null) ? (other.animName != null) : !this.animName.equals(other.animName)) {
				return false;
			}
			if (this.subGroup != other.subGroup) {
				return false;
			}
			return true;
		}

		public AnimationSequence getSequence() {
			return sequence;
		}

		public float getBlendTime() {
			return blendTime;
		}

		public void setBlendTime(float blendTime) {
			this.blendTime = blendTime;
		}

		public Part getPart() {
			return part;
		}

		public String getAnimName() {
			return animName;
		}

		public void setAnimName(String animName) {
			this.animName = animName;
		}

		public SubGroup getSubGroup() {
			return subGroup;
		}

		public Anim setLoopMode(LoopMode loopMode) {
			this.loopMode = loopMode;
			return this;
		}

		public LoopMode getLoopMode() {
			return loopMode;
		}

		public float getSpeed() {
			return speed;
		}

		public Anim setSpeed(float speed) {
			this.speed = speed;
			return this;
		}

		@Override
		public String toString() {
			return "Anim{" + "part=" + part + ", animName=" + animName + ", loopMode=" + loopMode + ", speed=" + speed
					+ ", subGroup=" + subGroup + '}';
		}
	}

	private final Map<SubGroup, List<Anim>> anims = new EnumMap<SubGroup, List<Anim>>(SubGroup.class);
	private String name;
	private Collection<SoundOption> sounds = Collections.emptyList();

	public AnimationSequence(String name) {
		this.name = name;
	}

	@Override
	public AnimationSequence clone() {
		AnimationSequence s = new AnimationSequence(name);
		for (Map.Entry<SubGroup, List<Anim>> en : anims.entrySet()) {
			List<Anim> l = new ArrayList<Anim>();
			for (Anim a : en.getValue()) {
				final Anim newA = a.clone();
				newA.sequence = s;
				l.add(newA);
			}
			s.anims.put(en.getKey(), l);
		}
		return s;
	}

	public void populate(AnimationSequence seq) {
		name = seq.name;
		anims.clear();
		anims.putAll(seq.anims);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AnimationSequence other = (AnimationSequence) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(AnimationSequence o) {
		return getName().compareTo(o.getName());
	}

	public String getName() {
		return name;
	}

	public Collection<SoundOption> getSounds() {
		return sounds;
	}

	public void setSounds(Collection<SoundOption> sounds) {
		this.sounds = sounds;
	}

	public Map<SubGroup, List<Anim>> getAnims() {
		return anims;
	}

	public void removeAnim(Anim anim) {
		List<Anim> l = anims.get(anim.getSubGroup());
		if (l != null) {
			l.remove(anim);
		}
	}

	public AnimationSequence addAnim(Anim anim) {
		anim.sequence = this;
		List<Anim> l = anims.get(anim.getSubGroup());
		if (l == null) {
			l = new ArrayList<Anim>();
			anims.put(anim.getSubGroup(), l);
		}
		l.add(anim);
		return this;
	}

	public List<Anim> anims(SubGroup subGroup) {
		if (subGroup == null) {
			List<Anim> l = new ArrayList<Anim>();
			for (List<Anim> al : anims.values()) {
				l.addAll(al);
			}
			return l;
		} else {
			List<Anim> l = anims.get(subGroup);
			if (l == null) {
				l = anims.get(SubGroup.NONE);
			}
			return l;
		}
	}

	@Override
	public String toString() {
		return "AnimationSequence{" + "anims=" + anims + ", name=" + name + '}';
	}

	public boolean hasAnimForPart(SubGroup subGroup, Part part) {
		for (Anim a : anims(subGroup)) {
			if (a.getPart().equals(part)) {
				return true;
			}
		}
		return false;
	}

	public final void setBlendTimeOnAll(float f) {
		for (Anim a : anims(null)) {
			a.setBlendTime(f);
		}
	}

	public final void setSpeedOnAll(float f) {
		for (Anim a : anims(null)) {
			a.setSpeed(f);
		}
	}
}
