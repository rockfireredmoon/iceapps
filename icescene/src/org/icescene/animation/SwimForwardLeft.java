package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class SwimForwardLeft extends AnimationSequence {

    public SwimForwardLeft() {
        super(BipedAnimationHandler.ANIM_SWIM_FORWARD_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Swim_Forward_L_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Swim_Forward_L_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Swim_Forward_L_t").setLoopMode(LoopMode.Loop));
    }
}
