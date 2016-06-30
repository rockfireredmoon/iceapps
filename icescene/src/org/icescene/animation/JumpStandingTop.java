package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class JumpStandingTop extends AnimationSequence {

    public JumpStandingTop() {
        super(BipedAnimationHandler.ANIM_JUMP_STANDING_TOP);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jump_Standing_Top_h").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jump_Standing_Top_b").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jump_Standing_Top_t").setLoopMode(LoopMode.DontLoop));
        
        setSpeedOnAll(4f);
    }
}
