package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class SwimRight extends AnimationSequence {

    public SwimRight() {
        super(BipedAnimationHandler.ANIM_SWIM_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Swim_R_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Swim_R_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Swim_R_t").setLoopMode(LoopMode.Loop));
    }
}
