package org.icescene.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class Rotator extends AbstractControl {

    private float speed;

    public Rotator() {
        this(1);

    }

    public Rotator(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.rotate(0, tpf * speed, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
