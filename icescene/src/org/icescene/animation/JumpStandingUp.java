package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class JumpStandingUp extends AnimationSequence {

    public JumpStandingUp() {
        super(BipedAnimationHandler.ANIM_JUMP_STANDING_UP);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jump_Standing_Up_h").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jump_Standing_Up_b").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jump_Standing_Up_t").setLoopMode(LoopMode.DontLoop));
        setSpeedOnAll(4f);
    }
}
