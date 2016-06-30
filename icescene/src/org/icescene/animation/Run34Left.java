package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Run34Left extends AnimationSequence {

    public Run34Left() {
        super(BipedAnimationHandler.ANIM_RUN34_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Run34_L_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Run34_L_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Run34_L_t").setLoopMode(LoopMode.Loop));
    }
}
