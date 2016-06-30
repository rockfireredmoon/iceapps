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
import com.jme3.math.FastMath;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Influencer that mimics OGRE's Rotator Affector
 * (http://www.ogre3d.org/docs/manual/manual_40.html#Rotator-Affector).
 */
public class RotatorAffector extends AbstractOGREParticleAffector {

    private boolean enabled = true;
    private float speedRangeStart;
    private float speedRangeEnd;
    private float rangeStart;
    private float rangeEnd;

    public RotatorAffector(OGREParticleScript script) {
        super(script);
    }

    @Override
    public void update(ParticleData p, float tpf) {
        if (enabled) {
            p.angles.set(0, 0, p.angles.z + (tpf * p.rotationSpeed.z));
        }
    }

    @Override
    public void initialize(ParticleData p) {
        // Pick a random rotation speed and start in the ranges given
        float rotSpeed = speedRangeStart + ((speedRangeEnd - speedRangeStart) * FastMath.rand.nextFloat());
        float rotAngle = rangeStart + ((rangeEnd - rangeStart) * FastMath.rand.nextFloat());

        p.angles.set(0, 0, rotAngle);
        p.rotationSpeed.set(0, 0, rotSpeed);
    }

    @Override
    public void reset(ParticleData p) {
    }

    @Property(label = "Speed Start", weight = 10)
    @FloatRange(min = -Float.MAX_VALUE)
    public float getSpeedRangeStart() {
        return speedRangeStart;
    }

    @Property(label = "Speed End", weight = 20)
    @FloatRange(min = -Float.MAX_VALUE)
    public float getSpeedRangeEnd() {
        return speedRangeEnd;
    }

    @Property(label = "Range Start", weight = 20, hint = Property.Hint.ANGLE)
    @FloatRange(min = -Float.MAX_VALUE)
    public float getRangeStart() {
        return rangeStart;
    }

    @Property(label = "Range End", weight = 20, hint = Property.Hint.ANGLE)
    @FloatRange(min = -Float.MAX_VALUE)
    public float getRangeEnd() {
        return rangeEnd;
    }

    @Property
    public void setSpeedRangeStart(float speedRangeStart) {
        this.speedRangeStart = speedRangeStart;
    }

    @Property
    public void setSpeedRangeEnd(float speedRangeEnd) {
        this.speedRangeEnd = speedRangeEnd;
    }

    @Property
    public void setRangeStart(float rangeStart) {
        this.rangeStart = rangeStart;
    }

    @Property
    public void setRangeEnd(float rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(speedRangeStart, "speedRangeStart", 0);
        oc.write(speedRangeEnd, "speedRangeEnd", 0);
        oc.write(rangeStart, "rangeStart", 0);
        oc.write(rangeEnd, "rangeEnd", 0);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        speedRangeStart = ic.readFloat("speedRangeStart", 0f);
        speedRangeEnd = ic.readFloat("speedRangeEnd", 0f);
        rangeStart = ic.readFloat("rangeStart", 0f);
        rangeEnd = ic.readFloat("rangeEnd", 0f);
    }

    @Override
    public ParticleInfluencer clone() {
        RotatorAffector clone = new RotatorAffector(script);
        clone.setSpeedRangeStart(speedRangeStart);
        clone.setSpeedRangeEnd(speedRangeEnd);
        clone.setRangeStart(rangeStart);
        clone.setRangeEnd(rangeEnd);
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
        return RotatorAffector.class;
    }

    @Override
    protected void writeAffector(PrintWriter pw) {
        pw.println(String.format("\t\trotation_speed_range_start %1.1f", speedRangeStart));
        pw.println(String.format("\t\trotation_speed_range_end %1.1f", speedRangeEnd));
        pw.println(String.format("\t\trotation_range_start %1.1f", rangeStart));
        pw.println(String.format("\t\trotation_range_end %1.1f", rangeEnd));
        pw.flush();
    }

    public boolean parse(String[] args, int lineNumber) throws ParseException {
        if (args[0].equals("rotation_speed_range_start")) {
            if (args.length == 2) {
                setSpeedRangeStart(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
            } else {
                throw new ParseException("Expected single rotation_speed_range_start for rotator at line " + lineNumber + ".", 0);
            }
        } else if (args[0].equals("rotation_speed_range_end")) {
            if (args.length == 2) {
                setSpeedRangeEnd(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
            } else {
                throw new ParseException("Expected single rotation_speed_range_end for rotator at line " + lineNumber + ".", 0);
            }
        } else if (args[0].equals("rotation_range_start")) {
            if (args.length == 2) {
                setRangeStart(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
            } else {
                throw new ParseException("Expected single rotation_range_start for rotator at line " + lineNumber + ".", 0);
            }
        } else if (args[0].equals("rotation_range_end")) {
            if (args.length == 2) {
                setRangeEnd(Float.parseFloat(args[1]) * FastMath.DEG_TO_RAD);
            } else {
                throw new ParseException("Expected single rotation_range_end for rotator at line " + lineNumber + ".", 0);
            }
        } else {
            return false;
        }
        return true;
    }
}
