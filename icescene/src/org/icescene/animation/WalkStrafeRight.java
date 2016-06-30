package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class WalkStrafeRight extends AnimationSequence {

    public WalkStrafeRight() {
        super(BipedAnimationHandler.ANIM_WALK_STRAFE_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Walk_Strafe_Right_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Walk_Strafe_Right_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Walk_Strafe_Right_t").setLoopMode(LoopMode.Loop));
    }
}
