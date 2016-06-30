package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Interact extends AnimationSequence {

    public Interact() {
        super(BipedAnimationHandler.ANIM_INTERACT);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Interact_h").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Interact_b").setLoopMode(LoopMode.DontLoop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Interact_t").setLoopMode(LoopMode.DontLoop));
    }
}
