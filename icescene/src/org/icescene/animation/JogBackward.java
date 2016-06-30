package org.icescene.animation;

import org.icescene.controls.BipedAnimationHandler;

import com.jme3.animation.LoopMode;

public class JogBackward extends AnimationSequence {

    public JogBackward() {
        super(BipedAnimationHandler.ANIM_JOG_BACKWARD);
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.HEAD, "Jog_Backward_h").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.BOTTOM, "Jog_Backward_b").setLoopMode(LoopMode.Loop));
        addAnim(new Anim(SubGroup.NONE, AnimationSequence.Part.TOP, "Jog_Backward_t").setLoopMode(LoopMode.Loop));
        setSpeedOnAll(1.15f);
    }
}
