package org.icescene.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class SynchronizingPhysicsControl extends AbstractControl {

    private final Spatial source;
    private RigidBodyControl rigidBody;

    public SynchronizingPhysicsControl(Spatial source) {
        this.source = source;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            rigidBody = spatial.getControl(RigidBodyControl.class);
            if (rigidBody == null) {
                throw new IllegalStateException("Expected " + getClass() + " to be attached to spatial that also has a " + RigidBodyControl.class);
            }
            updateFromSource();
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateFromSource();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void updateFromSource() {
        rigidBody.setPhysicsLocation(source.getWorldTranslation());
        rigidBody.getCollisionShape().setScale(source.getLocalScale());
        rigidBody.setPhysicsRotation(source.getLocalRotation());
    }
}
