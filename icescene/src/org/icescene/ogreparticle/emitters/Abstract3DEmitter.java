package org.icescene.ogreparticle.emitters;

import java.io.PrintWriter;
import java.text.ParseException;

import org.icescene.ogreparticle.AbstractOGREParticleEmitter;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.FloatRange;
import org.icescene.propertyediting.Property;

import com.jme3.math.Vector3f;

public abstract class Abstract3DEmitter extends AbstractOGREParticleEmitter {

    protected Vector3f size = new Vector3f(1, 1, 1);

    public Abstract3DEmitter(OGREParticleScript group) {
        super(group);
    }

    @Property(label = "Size", weight = 15, hint = Property.Hint.SCALE)
    @FloatRange(incr = 0.1f, precision = 3)
    public Vector3f getSize() {
        return size;
    }

    @Property
    public void setSize(Vector3f size) {
        this.size = size;
    }

    public boolean parse(String[] args, int lineNo) throws ParseException {
        if (args[0].equals("height")) {
            if (args.length == 2) {
                size.x = (Float.parseFloat(args[1]));
                return true;
            } else {
                throw new ParseException("Expected single height at line " + lineNo + ".", 0);
            }
        } else if (args[0].equals("width")) {
            if (args.length == 2) {
                size.y = (Float.parseFloat(args[1]));
                return true;
            } else {
                throw new ParseException("Expected single width at line " + lineNo + ".", 0);
            }
        } else if (args[0].equals("depth")) {
            if (args.length == 2) {
                size.z = (Float.parseFloat(args[1]));
                return true;
            } else {
                throw new ParseException("Expected single width at line " + lineNo + ".", 0);
            }
        }
        return false;
    }

    @Override
    protected void writeEmitter(PrintWriter pw) {
        pw.println(String.format("\t\twidth %1.1f", size.x));
        pw.println(String.format("\t\theight %1.1f", size.y));
        pw.println(String.format("\t\tdepth %1.1f", size.z));
    }
}
