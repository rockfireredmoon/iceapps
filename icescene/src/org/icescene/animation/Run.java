package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Run extends AnimationSequence {

    public Run() {
        super(BipedAnimationHandler.ANIM_RUN);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Run_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Run_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Run_t").setLoopMode(LoopMode.Loop));
    }
}
