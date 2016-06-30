package org.icescene.ogreparticle.affectors;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Logger;

import org.icescene.ogreparticle.AbstractOGREParticleAffector;
import org.icescene.ogreparticle.OGREParticleConfiguration;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.FloatRange;
import org.icescene.propertyediting.Property;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 * This affector defines a plane which deflects particles which collide with it. Mimics
 * http://www.ogre3d.org/docs/manual/manual_40.html#DeflectorPlane-Affector.
 */
public class DeflectorPlaneAffector extends AbstractOGREParticleAffector {

    private static final Logger LOG = Logger.getLogger(DeflectorPlaneAffector.class.getName());
    private boolean enabled = true;
    private Vector3f planePoint = new Vector3f(0, 0, 0);
    private Vector3f planeNormal = new Vector3f(0, 1, 0);
    private float bounce = 1f;

    public DeflectorPlaneAffector(OGREParticleScript script) {
        super(script);
    }

    @Override
    public void update(ParticleData p, float tpf) {
        float planeDistance = -planeNormal.dot(planePoint)
                / FastMath.sqrt(planeNormal.dot(planeNormal));
        Vector3f direction = p.velocity.mult(tpf);
        final Vector3f absPos = p.position.clone();
        absPos.addLocal(p.initialPosition);
        float pp = planeNormal.dot(absPos.add(direction));
        if (pp + planeDistance <= 0f) {
            float a = planeNormal.dot(absPos) + planeDistance;
            if (a > 0) {
                Vector3f directionPart = direction.mult(-a / direction.dot(planeNormal));
                absPos.set(absPos.add(directionPart).addLocal(
                        directionPart.subtract(direction).multLocal(bounce)));
                p.velocity.set(p.velocity.subtract(planeNormal.mult(p.velocity.dot(planeNormal)).mult(2f)).multLocal(bounce));
                absPos.subtractLocal(p.initialPosition);
                p.position.set(absPos);
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
        DeflectorPlaneAffector clone = new DeflectorPlaneAffector(script);
        clone.planeNormal = planeNormal.clone();
        clone.planePoint = planePoint.clone();
        clone.bounce = bounce;
        clone.setEnabled(enabled);
        return clone;
    }

    @Property(label = "Plane Point", weight = 10)
    @FloatRange(min = -Float.MAX_VALUE)
    public Vector3f getPlanePoint() {
        return planePoint;
    }

    @Property
    public void setPlanePoint(Vector3f planePoint) {
        this.planePoint = planePoint;
    }

    @Property(label = "Plane Normal", weight = 20)
    @FloatRange(min = -Float.MAX_VALUE)
    public Vector3f getPlaneNormal() {
        return planeNormal;
    }

    @Property
    public void setPlaneNormal(Vector3f planeNormal) {
        this.planeNormal = planeNormal;
    }

    @Property(label = "Bounce", weight = 30)
    public float getBounce() {
        return bounce;
    }

    @Property
    public void setBounce(float bounce) {
        this.bounce = bounce;
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
        return DeflectorPlaneAffector.class;
    }

    @Override
    public String toString() {
        return "DeflectionPlaneInfluencer{" + "enabled=" + enabled + ", planePoint=" + planePoint + ", planeNormal=" + planeNormal + ", bounce=" + bounce + '}';
    }

    @Override
    protected void writeAffector(PrintWriter pw) {
        pw.println(String.format("\t\tplane_point %s", OGREParticleConfiguration.formatForWrite(planePoint)));
        pw.println(String.format("\t\tplane_normal %s", OGREParticleConfiguration.formatForWrite(planeNormal)));
        pw.println(String.format("\t\tbounce %1.1f", bounce));
        pw.flush();
    }

    public boolean parse(String[] args, int lineNumber) throws ParseException {
        if (args[0].equals("plane_point")) {
            setPlanePoint(OGREParticleConfiguration.parseVector3f(args));
        } else if (args[0].equals("plane_normal")) {
            setPlaneNormal(OGREParticleConfiguration.parseVector3f(args));
        } else if (args[0].equals("bounce")) {
            if (args.length == 2) {
                setBounce(Float.parseFloat(args[1]));
            } else {
                throw new ParseException("Expected bounce value at line " + lineNumber + ".", 0);
            }
        } else {
            return false;
        }
        return true;
    }
}
