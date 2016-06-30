package org.icescene.ogreparticle.affectors;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import org.icescene.ogreparticle.AbstractOGREParticleAffector;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.Property;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Randomly change particle direction. Mimics
 * http://www.ogre3d.org/docs/manual/manual_40.html#DirectionRandomiser-Affector.
 */
public class DirectionRandomiserAffector extends AbstractOGREParticleAffector {

    private boolean enabled = true;
    private boolean keepVelocity;
    private float randomness;
    private float scope;

    public DirectionRandomiserAffector(OGREParticleScript script) {
        super(script);
    }

    @Property(label = "Keep Velocity", weight = 10)
    public boolean isKeepVelocity() {
        return keepVelocity;
    }

    @Property
    public void setKeepVelocity(boolean keepVelocity) {
        this.keepVelocity = keepVelocity;
    }

    @Property(label = "Randomness", weight = 20)
    public float getRandomness() {
        return randomness;
    }

    @Property
    public void setRandomness(float randomness) {
        this.randomness = randomness;
    }

    @Property(label = "Scope", weight = 30)
    public float getScope() {
        return scope;
    }

    @Property
    public void setScope(float scope) {
        this.scope = scope;
    }

    @Override
    public void update(ParticleData p, float tpf) {
        float length = 0;
        if (scope > FastMath.rand.nextFloat()) {
            if (!p.velocity.equals(Vector3f.ZERO)) {
                if (keepVelocity) {
                    length = p.velocity.length();
                }
                p.velocity.addLocal(((FastMath.rand.nextFloat() * randomness * 2) - randomness) * tpf,
                        ((FastMath.rand.nextFloat() * randomness * 2) - randomness) * tpf,
                        ((FastMath.rand.nextFloat() * randomness * 2) - randomness) * tpf);
                if (keepVelocity) {
                    p.velocity.multLocal(length / p.velocity.length());
                }
            }
        }
    }

    @Override
    public void initialize(ParticleData p) {
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
        DirectionRandomiserAffector clone = new DirectionRandomiserAffector(script);
        clone.randomness = randomness;
        clone.keepVelocity = keepVelocity;
        clone.scope = scope;
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
        return DirectionRandomiserAffector.class;
    }

    @Override
    public String toString() {
        return "DirectionRandomiserInfluencer{" + "enabled=" + enabled + ", keepVelocity=" + keepVelocity + ", randomness=" + randomness + ", scope=" + scope + '}';
    }

    @Override
    protected void writeAffector(PrintWriter pw) {
        pw.println(String.format("\t\tscope %1.1f", scope));
        pw.println(String.format("\t\trandomness %1.1f", randomness));
        pw.println(String.format("\t\tkeep_veloicty %s", keepVelocity));
        pw.flush();
    }

    public boolean parse(String[] args, int lineNumber) throws ParseException {
        if (args[0].equals("randomness")) {
            if (args.length == 2) {
                setRandomness(Float.parseFloat(args[1]));
            } else {
                throw new ParseException("Expected float value after randomness at line " + lineNumber + ".", 0);
            }
        } else if (args[0].equals("scope")) {
            if (args.length == 2) {
                setScope(Float.parseFloat(args[1]));
            } else {
                throw new ParseException("Expected float value after scope at line " + lineNumber + ".", 0);
            }
        } else if (args[0].equals("keep_velocity")) {
            if (args.length == 2) {
                setKeepVelocity(Boolean.parseBoolean(args[1]));
            } else {
                throw new ParseException("Expected boolean value after keep_velocity at line " + lineNumber + ".", 0);
            }
        } else {
            return false;
        }
        return true;
    }
}
