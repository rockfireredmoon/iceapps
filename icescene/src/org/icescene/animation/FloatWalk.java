package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class FloatWalk extends AnimationSequence {

    public FloatWalk() {
        super(BipedAnimationHandler.ANIM_FLOAT_WALK);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Float_Walk_h").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Float_Walk_b").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Float_Walk_t").setLoopMode(LoopMode.DontLoop));
    }
}
