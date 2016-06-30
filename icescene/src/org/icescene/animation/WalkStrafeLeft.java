package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class WalkStrafeLeft extends AnimationSequence {

    public WalkStrafeLeft() {
        super(BipedAnimationHandler.ANIM_WALK_STRAFE_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Walk_Strafe_Left_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Walk_Strafe_Left_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Walk_Strafe_Left_t").setLoopMode(LoopMode.Loop));
    }
}
