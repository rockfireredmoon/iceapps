package org.icescene.ogreparticle.influencers;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;


/**
 * Very simple Influencer that alters the initial rotation by a random amount for
 * each particle. Attempt to mimic the 'angle' attribute. 
 * (http://www.ogre3d.org/docs/manual/manual_37.html#angle)
 */
public class AngleInfluencer implements ParticleInfluencer {
    private static final Logger LOG = Logger.getLogger(AngleInfluencer.class.getName());

    private boolean enabled = true;
    private float angle;
    
    public AngleInfluencer(float angle) {
        this.angle = angle;
    }
    
    @Override
    public void update(ParticleData p, float tpf) {
        
    }

    @Override
    public void initialize(ParticleData p) {
        Quaternion deviateQuat = new Quaternion(new float[] {
                (FastMath.rand.nextFloat() * angle * 2 ) - angle,
                (FastMath.rand.nextFloat() * angle * 2 ) - angle,
                (FastMath.rand.nextFloat() * angle * 2 ) - angle});
        p.velocity.set(deviateQuat.mult(p.velocity));
        
//        Quaternion dirQuat = new Quaternion();
//        dirQuat.lookAt(direction, upVector);
//        Quaternion deviateQuat = new Quaternion(new float[] {
//                (FastMath.rand.nextFloat() * angle * 2 ) - angle,
//                (FastMath.rand.nextFloat() * angle * 2 ) - angle,
//                (FastMath.rand.nextFloat() * angle * 2 ) - angle});
//        dirQuat.multLocal(deviateQuat);
//        float[] angles = new float[3];
//        dirQuat.toAngles(angles);
//        p.angles.set(angles[0],angles[1], angles[2]);
    }

    @Override
    public void reset(ParticleData p) {
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(angle, "angle", angle);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        // TODO
    }

    @Override
    public ParticleInfluencer clone() {
        try {
            AngleInfluencer clone = (AngleInfluencer) super.clone();
            clone.angle = angle;
            clone.setEnabled(enabled);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Class getInfluencerClass() {
        return AngleInfluencer.class;
    }
}
