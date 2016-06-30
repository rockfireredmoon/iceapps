package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class JogStrafeRight extends AnimationSequence {

    public JogStrafeRight() {
        super(BipedAnimationHandler.ANIM_JOG_STRAFE_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jog_Strafe_Right_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jog_Strafe_Right_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jog_Strafe_Right_t").setLoopMode(LoopMode.Loop));
    }
}
