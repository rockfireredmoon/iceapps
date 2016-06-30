package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Jog extends AnimationSequence {

    public Jog() {
        super(BipedAnimationHandler.ANIM_JOG);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jog_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jog_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jog_t").setLoopMode(LoopMode.Loop));
    }
}
