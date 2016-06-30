package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Jog34BackRight extends AnimationSequence {

    public Jog34BackRight() {
        super(BipedAnimationHandler.ANIM_JOG34_BACK_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jog34_Back_R_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jog34_Back_R_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jog34_Back_R_t").setLoopMode(LoopMode.Loop));
    }
}
