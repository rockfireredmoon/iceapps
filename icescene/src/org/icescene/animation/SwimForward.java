package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class SwimForward extends AnimationSequence {

    public SwimForward() {
        super(BipedAnimationHandler.ANIM_SWIM_FORWARD);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Swim_Forward_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Swim_Forward_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Swim_Forward_t").setLoopMode(LoopMode.Loop));
    }
}
