package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class SwimBack extends AnimationSequence {

    public SwimBack() {
        super(BipedAnimationHandler.ANIM_SWIM_BACK);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Swim_Back_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Swim_Back_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Swim_Back_t").setLoopMode(LoopMode.Loop));
    }
}
