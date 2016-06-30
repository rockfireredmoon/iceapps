package org.icescene.configuration.creatures;

import java.util.List;
import java.util.Map;

import org.icelib.AttachmentPoint;
import org.icelib.beans.MappedList;
import org.icelib.beans.MappedMap;
import org.icescene.ServiceRef;
import org.icescene.animation.AnimationDefs;
import org.icescene.animation.AnimationOption;
import org.icescene.configuration.attachments.AttachmentPlace;

import com.jme3.math.Vector3f;

public class CreatureDefinition extends AbstractCreatureDefinition {

	private static final long serialVersionUID = 1L;
	private Map<AttachmentPoint, AttachmentPlace> attachmentPoints = new MappedMap<AttachmentPoint, AttachmentPlace>(
			AttachmentPoint.class, AttachmentPlace.class);
	private Details details = new Details();
	private boolean helmetHead;
	private List<Head> heads = new MappedList<Head>(Head.class);
	private float sizeMultiplier = 1.0f;
	private Posture posture = Posture.NONE;
	private Vector3f headScale = new Vector3f(1, 1, 1);
	private float minSelectableSize;
	private float maxSelectableSize;
	
	@ServiceRef
	protected static AnimationDefs animationDefs;

	public CreatureDefinition(CreatureKey key) {
		super(key);
	}

	public float getMinSelectableSize() {
		return minSelectableSize;
	}

	public void setMinSelectableSize(float minSelectableSize) {
		this.minSelectableSize = minSelectableSize;
	}

	public float getMaxSelectableSize() {
		return maxSelectableSize;
	}

	public void setMaxSelectableSize(float maxSelectableSize) {
		this.maxSelectableSize = maxSelectableSize;
	}

	public Vector3f getHeadScale() {
		return headScale;
	}

	public void setHeadScale(Vector3f headScale) {
		this.headScale = headScale;
	}

	public boolean isHelmetHead() {
		return helmetHead;
	}

	public List<Head> getHeads() {
		return heads;
	}

	public Details getDetails() {
		return details;
	}

	public Map<AttachmentPoint, AttachmentPlace> getAttachmentPoints() {
		return attachmentPoints;
	}

	public float getSizeMultiplier() {
		return sizeMultiplier;
	}

	public void setHelmetHead(boolean helmetHead) {
		this.helmetHead = helmetHead;
	}

	public void setSizeMultiplier(float sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}

	public void setPosture(Posture posture) {
		this.posture = posture;
	}

	public Posture getPosture() {
		return posture;
	}

	@Override
	public Map<String, AnimationOption> getAnimations() {
		return animationDefs.get("Biped").getAnimations();
	}

}