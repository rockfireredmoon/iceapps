package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class SwimLeft extends AnimationSequence {

    public SwimLeft() {
        super(BipedAnimationHandler.ANIM_SWIM_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Swim_L_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Swim_L_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Swim_L_t").setLoopMode(LoopMode.Loop));
    }
}
