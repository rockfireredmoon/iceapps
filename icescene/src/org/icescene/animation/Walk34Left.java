package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Walk34Left extends AnimationSequence {

    public Walk34Left() {
        super(BipedAnimationHandler.ANIM_WALK34_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Walk34_L_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Walk34_L_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Walk34_L_t").setLoopMode(LoopMode.Loop));
    }
}
