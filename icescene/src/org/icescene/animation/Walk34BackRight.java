package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Walk34BackRight extends AnimationSequence {

    public Walk34BackRight() {
        super(BipedAnimationHandler.ANIM_WALK34_BACK_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Walk34_Back_R_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Walk34_Back_R_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Walk34_Back_R_t").setLoopMode(LoopMode.Loop));
    }
}
