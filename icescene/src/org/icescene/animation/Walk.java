package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Walk extends AnimationSequence {

    public Walk() {
        super(BipedAnimationHandler.ANIM_WALK);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Walk_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Walk_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Walk_t").setLoopMode(LoopMode.Loop));
    }
}
