 package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class JumpStandingRecover extends AnimationSequence {

    public JumpStandingRecover() {
        super(BipedAnimationHandler.ANIM_JUMP_STANDING_RECOVER);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jump_Standing_Recover_h").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jump_Standing_Recover_b").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jump_Standing_Recover_t").setLoopMode(LoopMode.DontLoop));
        setSpeedOnAll(5f);
    }
}
