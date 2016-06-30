package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class SwimTread extends AnimationSequence {

    public SwimTread() {
        super(BipedAnimationHandler.ANIM_SWIM_TREAD);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Swim_Tread_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Swim_Tread_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Swim_Tread_t").setLoopMode(LoopMode.Loop));
    }
}
