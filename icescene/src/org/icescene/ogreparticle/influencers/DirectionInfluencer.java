package org.icescene.ogreparticle.influencers;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;


/**
 * Set initial direction
 */
public class DirectionInfluencer implements ParticleInfluencer {
    private static final Logger LOG = Logger.getLogger(DirectionInfluencer.class.getName());

    private boolean enabled = true;
    private Vector3f direction;
    private float velocityMin;
    private float velocityMax;
    
    public DirectionInfluencer(Vector3f direction, float velocityMin, float velocityMax) {
        this.direction = direction;
        this.velocityMax = velocityMax;
        this.velocityMin = velocityMin;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getVelocityMin() {
        return velocityMin;
    }

    public void setVelocityMin(float velocityMin) {
        this.velocityMin = velocityMin;
    }

    public float getVelocityMax() {
        return velocityMax;
    }

    public void setVelocityMax(float velocityMax) {
        this.velocityMax = velocityMax;
    }
    
    @Override
    public void update(ParticleData p, float tpf) {
        
    }

    @Override
    public void initialize(ParticleData p) {
        p.velocity.set(direction.mult(velocityMin + ( ( velocityMax - velocityMin) * FastMath.rand.nextFloat())));
    }

    @Override
    public void reset(ParticleData p) {
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        // TODO
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        // TODO
    }

    @Override
    public ParticleInfluencer clone() {
        try {
            DirectionInfluencer clone = (DirectionInfluencer) super.clone();
            clone.direction = direction.clone();
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
        return DirectionInfluencer.class;
    }
}
