package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Jog34Right extends AnimationSequence {

    public Jog34Right() {
        super(BipedAnimationHandler.ANIM_JOG34_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jog34_R_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jog34_R_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jog34_R_t").setLoopMode(LoopMode.Loop));
    }
}
