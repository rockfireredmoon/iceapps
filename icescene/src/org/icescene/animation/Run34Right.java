package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Run34Right extends AnimationSequence {

    public Run34Right() {
        super(BipedAnimationHandler.ANIM_RUN34_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Run34_R_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Run34_R_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Run34_R_t").setLoopMode(LoopMode.Loop));
    }
}
