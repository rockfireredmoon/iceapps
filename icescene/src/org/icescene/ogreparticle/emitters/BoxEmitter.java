package org.icescene.ogreparticle.emitters;

import java.util.logging.Logger;

import org.icescene.ogreparticle.OGREParticleScript;

import com.jme3.scene.shape.Box;

import emitter.Emitter;

public class BoxEmitter extends Abstract3DEmitter {

    private static final Logger LOG = Logger.getLogger(BoxEmitter.class.getName());

    public BoxEmitter(OGREParticleScript group) {
        super(group);
    }

    @Override
    protected void createEmitterShape(Emitter emitter) {
        LOG.info("    Tri Mesh");
        emitter.setUseRandomEmissionPoint(true);
        emitter.setShape(new Box(0.5f, 0.5f, 0.5f));
        emitter.setLocalScale(size);
        LOG.info(String.format("    Box emitter shape of %f x %f x %f", size.x, size.y, size.z));
    }
}
