package org.icescene.ogreparticle.affectors;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import org.icescene.ogreparticle.AbstractOGREParticleAffector;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.FloatRange;
import org.icescene.propertyediting.Property;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Very simple Influencer that scales the particle by a certain amount each second. An
 * attempt to mimic "OGRE Scaler Affector"
 * (http://www.ogre3d.org/docs/manual/manual_40.html#Scaler-Affector)
 */
public class ScalerAffector extends AbstractOGREParticleAffector {

    private boolean initialized = false;
    private boolean enabled = true;
    private float rate = 1f;

    public ScalerAffector(OGREParticleScript script) {
        super(script);
    }

    @Override
    public void update(ParticleData p, float tpf) {
        if (enabled) {
        // TODO this division by 2f was found by trial and error. I'm not sure why
        // it is required, but it makes particles look right size when compared
        // in OgreParticleLab. See also InitialSizeInfluence
            float ds = ( rate * tpf ) / 2f;
            p.size.set(p.size.x + ds, p.size.y + ds, p.size.z);
        }
    }

    @Override
    public void initialize(ParticleData p) {
    }

    @Override
    public void reset(ParticleData p) {
    }

    @Property(label = "Rate", weight = 10)
    @FloatRange(min = -Float.MAX_VALUE)
    public float getRate() {
        return rate;
    }

    @Property
    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(rate, "rate", 0);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        rate = ic.readFloat("rate", 0f);
    }

    @Override
    public ParticleInfluencer clone() {
        ScalerAffector clone = new ScalerAffector(script);
        clone.setRate(rate);
        clone.setEnabled(enabled);
        return clone;
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
        return ScalerAffector.class;
    }

    @Override
    protected void writeAffector(PrintWriter pw) {
        pw.println(String.format("\t\trate %1.1f", rate));
        pw.flush();
    }

    public boolean parse(String[] args, int lineNumber) throws ParseException {
        if (args[0].equals("rate")) {
            if (args.length == 2) {
                setRate(Float.parseFloat(args[1]));
            } else {
                throw new ParseException("Expected single rate for scaler at line " + lineNumber + ".", 0);
            }
        } else {
            return false;
        }
        return true;
    }
}
