package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Walk34BackLeft extends AnimationSequence {

    public Walk34BackLeft() {
        super(BipedAnimationHandler.ANIM_WALK34_BACK_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Walk34_Back_L_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Walk34_Back_L_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Walk34_Back_L_t").setLoopMode(LoopMode.Loop));
    }
}
