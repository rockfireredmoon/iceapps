package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class SwimForwardRight extends AnimationSequence {

    public SwimForwardRight() {
        super(BipedAnimationHandler.ANIM_SWIM_FORWARD_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Swim_Forward_R_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Swim_Forward_R_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Swim_Forward_R_t").setLoopMode(LoopMode.Loop));
    }
}
