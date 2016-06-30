package org.icescene.ogreparticle.affectors;

import static org.icescene.ogreparticle.OGREParticleConfiguration.parseVector3f;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import org.icescene.ogreparticle.AbstractOGREParticleAffector;
import org.icescene.ogreparticle.OGREParticleConfiguration;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.FloatRange;
import org.icescene.propertyediting.Property;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * Attempt to mimic Linear Force Affector
 * (http://www.ogre3d.org/docs/manual/manual_40.html#Linear-Force-Affector)
 */
public class LinearForceAffector extends AbstractOGREParticleAffector {

    public enum Application {

        AVERAGE, ADD
    }
    private boolean enabled = true;
    private Vector3f force = new Vector3f(0, -100f, 0);
    private Application application = Application.ADD;

    public LinearForceAffector(OGREParticleScript script) {
        super(script);
    }

    public LinearForceAffector(OGREParticleScript script, Vector3f force, Application application) {
        super(script);
        this.force = force;
        this.application = application;
    }

    @Override
    public void update(ParticleData p, float tpf) {
        if (enabled) {
            switch (application) {
                case ADD:
                    p.velocity.addLocal(force.mult(tpf));
                    break;
                case AVERAGE:
                    p.velocity.set(p.velocity.add(force).divideLocal(2f));
                    break;
            }
        }
    }

    @Override
    public void initialize(ParticleData p) {
    }

    @Override
    public void reset(ParticleData p) {
    }

    @Property(label = "Force", weight = 10)
    @FloatRange(min = -Float.MAX_VALUE)
    public Vector3f getForce() {
        return force;
    }

    @Property
    public void setForce(Vector3f force) {
        this.force = force;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        force.write(ex);
        oc.write(application.name(), "application", Application.ADD.name());
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        application = Application.valueOf(ic.readString("application", Application.ADD.name()));
        force.read(im);
    }

    @Override
    public ParticleInfluencer clone() {
        LinearForceAffector clone = new LinearForceAffector(script);
        clone.setApplication(application);
        clone.setForce(force.clone());
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
        return LinearForceAffector.class;
    }

    @Override
    protected void writeAffector(PrintWriter pw) {
        pw.println(String.format("\t\tforce_vector %s", OGREParticleConfiguration.formatForWrite(force)));
        pw.println(String.format("\t\tadd %s", application.name().toLowerCase()));
        pw.flush();
    }

    public boolean parse(String[] args, int lineNumber) throws ParseException {
        if (args[0].equals("force_vector")) {
            setForce(parseVector3f(args));
        } else if (args[0].equals("force_application")) {
            if (args.length == 2) {
                if (args[1].equals("add")) {
                    // this is "traditional" apparently
                    setApplication(LinearForceAffector.Application.ADD);
                } else if (args[1].equals("average")) {
                    setApplication(LinearForceAffector.Application.AVERAGE);
                } else {
                    throw new ParseException("Expected value of average or add at line " + lineNumber, 0);
                }
            } else {
                throw new ParseException("Expected either add or average for force_application at line " + lineNumber + ".", 0);
            }
        } else {
            return false;
        }
        return true;
    }
}
