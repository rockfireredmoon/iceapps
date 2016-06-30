package org.icescene.ogreparticle.influencers;

import java.io.IOException;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector2f;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;


/**
 * Very simple Influencer that sets the start size to a fixed value. An attempt to 
 * mimic 'particle_width' and 'particle_height'.
 * (http://www.ogre3d.org/docs/manual/manual_37.html#colour)
 */
public class InitialSizeInfluencer implements ParticleInfluencer {

    private boolean enabled = true;
    private Vector2f size;
    
    public InitialSizeInfluencer() {
        this(new Vector2f(1, 1));
    }

    public InitialSizeInfluencer(Vector2f size) {
        this.size = size;
    }
    
    @Override
    public void update(ParticleData p, float tpf) {
    }

    @Override
    public void initialize(ParticleData p) {
        
        // TODO this division by 2f was found by trial and error. I'm not sure why
        // it is required, but it makes particles look right size when compared
        // in OgreParticleLab. See also ScalerAffector
        
        p.size.set(size.x / 2f, size.y / 2f, p.size.z);
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
            InitialSizeInfluencer clone = (InitialSizeInfluencer) super.clone();
            clone.size = size.clone();
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
        return InitialSizeInfluencer.class;
    }
}
