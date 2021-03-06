package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Jog34BackLeft extends AnimationSequence {

    public Jog34BackLeft() {
        super(BipedAnimationHandler.ANIM_JOG34_BACK_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jog34_Back_L_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jog34_Back_L_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jog34_Back_L_t").setLoopMode(LoopMode.Loop));
    }
}
