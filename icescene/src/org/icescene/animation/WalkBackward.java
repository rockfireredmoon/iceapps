package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class WalkBackward extends AnimationSequence {

    public WalkBackward() {
        super(BipedAnimationHandler.ANIM_WALK_BACKWARD);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Walk_Backward_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Walk_Backward_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Walk_Backward_t").setLoopMode(LoopMode.Loop));
        setSpeedOnAll(2f);
    }
}
