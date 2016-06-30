package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class JogStrafeLeft extends AnimationSequence {

    public JogStrafeLeft() {
        super(BipedAnimationHandler.ANIM_JOG_STRAFE_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jog_Strafe_Left_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jog_Strafe_Left_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jog_Strafe_Left_t").setLoopMode(LoopMode.Loop));
    }
}
