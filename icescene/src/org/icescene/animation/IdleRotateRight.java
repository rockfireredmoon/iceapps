package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class IdleRotateRight extends AnimationSequence {

    public IdleRotateRight() {
        super(BipedAnimationHandler.ANIM_IDLE_ROTATE_RIGHT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Idle_Rotate_Right_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Idle_Rotate_Right_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Idle_Rotate_Right_t").setLoopMode(LoopMode.Loop));
    }
}
