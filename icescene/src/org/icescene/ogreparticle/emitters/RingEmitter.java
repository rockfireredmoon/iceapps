package org.icescene.ogreparticle.emitters;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Logger;

import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.Property;

import com.jme3.math.Vector2f;
import com.jme3.scene.shape.Torus;

import emitter.Emitter;

public class RingEmitter extends Abstract3DEmitter {

    private static final Logger LOG = Logger.getLogger(RingEmitter.class.getName());
    private Vector2f innerSize = new Vector2f();

    public RingEmitter(OGREParticleScript group) {
        super(group);
    }

    @Property(label = "Inner Size", weight = 25, hint = Property.Hint.SCALE)
    public Vector2f getInnerSize() {
        return innerSize;
    }

    @Property
    public void setInnerSize(Vector2f innerSize) {
        this.innerSize = innerSize;
    }

    @Override
    public boolean parse(String[] args, int lineNo) throws ParseException {
        boolean ok = super.parse(args, lineNo);
        if (!ok) {
            if (args[0].equals("inner_height")) {
                if (args.length == 2) {
                    innerSize.y = (Float.parseFloat(args[1]));
                    ok = true;
                } else {
                    throw new ParseException("Expected single inner_height at line " + lineNo + ".", 0);
                }
            } else if (args[0].equals("inner_width")) {
                if (args.length == 2) {
                    innerSize.x = (Float.parseFloat(args[1]));
                    ok = true;
                } else {
                    throw new ParseException("Expected single inner_width at line " + lineNo + ".", 0);
                }
            }
        }
        return ok;
    }

    @Override
    protected void createEmitterShape(Emitter emitter) {
        emitter.setUseRandomEmissionPoint(true);
        Torus ring = new Torus(64, 2, ((1f - innerSize.x) * size.x) / 2f, size.y / 2f);
        LOG.info(String.format("    Ring emitter shape of %f x %f x - %f x %f", size.x, size.y, innerSize.x, innerSize.y));
        LOG.warning("TODO: Only ring width/inner_width is used");
        LOG.warning("TODO: Not convinced it's actually emitting in a ring shape");
        emitter.setShape(ring);
        emitter.setLocalScale(1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void writeEmitter(PrintWriter pw) {
        super.writeEmitter(pw);
        pw.println(String.format("\t\tinner_width %1.1f", innerSize.x));
        pw.println(String.format("\t\tinner_height %1.1f", innerSize.y));
    }
}
