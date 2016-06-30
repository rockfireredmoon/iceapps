package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class IdleRotateLeft extends AnimationSequence {

    public IdleRotateLeft() {
        super(BipedAnimationHandler.ANIM_IDLE_ROTATE_LEFT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Idle_Rotate_Left_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Idle_Rotate_Left_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Idle_Rotate_Left_t").setLoopMode(LoopMode.Loop));
    }
}
