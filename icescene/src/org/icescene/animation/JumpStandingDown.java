package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class JumpStandingDown extends AnimationSequence {

    public JumpStandingDown() {
        super(BipedAnimationHandler.ANIM_JUMP_STANDING_DOWN);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jump_Standing_Down_h").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jump_Standing_Down_b").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jump_Standing_Down_t").setLoopMode(LoopMode.DontLoop));

        setSpeedOnAll(4f);
    }
}
