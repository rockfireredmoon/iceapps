package org.icescene.ogreparticle.emitters;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Logger;

import org.icescene.ogreparticle.AbstractOGREParticleEmitter;
import org.icescene.ogreparticle.OGREParticleScript;

import emitter.Emitter;

public class PointEmitter extends AbstractOGREParticleEmitter {

    private static final Logger LOG = Logger.getLogger(PointEmitter.class.getName());

    public PointEmitter(OGREParticleScript group) {
        super(group);
    }

    @Override
    protected void createEmitterShape(Emitter emitter) {
        // http://www.ogre3d.org/docs/manual/manual_35.html#particle_005fpoint_005frendering
        LOG.info("    Point Mesh");
        emitter.setShapeSimpleEmitter();
        emitter.setUseRandomEmissionPoint(false);
        emitter.setLocalScale(1.0f, 1.0f, 1.0f);
        LOG.info("    Point emitter shape");
    }

    public boolean parse(String[] args, int lineNumber) throws ParseException {
        return false;
    }

    @Override
    protected void writeEmitter(PrintWriter pw) {
    }
}
