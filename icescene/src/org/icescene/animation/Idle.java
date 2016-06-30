package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class Idle extends AnimationSequence {

    public Idle() {
        super(BipedAnimationHandler.ANIM_IDLE);
        addAnim(new Anim(SubGroup.MALE, AnimationSequence.Part.BOTTOM, "Idle_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.MALE, AnimationSequence.Part.HEAD, "Idle_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.MALE, AnimationSequence.Part.TOP, "Idle_t").setLoopMode(LoopMode.Loop));
        
        // Weird looking female animation
//        addAnim(new Anim(SubGroup.FEMALE, AnimationSequence.Part.HEAD, "Idle_Female_h").setLoopMode(LoopMode.Loop));
//        addAnim(new Anim(SubGroup.FEMALE, AnimationSequence.Part.BOTTOM, "Idle_Female_b").setLoopMode(LoopMode.Loop));
//        addAnim(new Anim(SubGroup.FEMALE, AnimationSequence.Part.TOP, "Idle_Female_t").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.FEMALE, AnimationSequence.Part.BOTTOM, "Idle_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.FEMALE, AnimationSequence.Part.HEAD, "Idle_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.FEMALE, AnimationSequence.Part.TOP, "Idle_t").setLoopMode(LoopMode.Loop));
    }
}
