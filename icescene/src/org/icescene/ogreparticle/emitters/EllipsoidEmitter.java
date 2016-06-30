package org.icescene.ogreparticle.emitters;

import java.util.logging.Logger;

import org.icescene.ogreparticle.OGREParticleScript;

import com.jme3.scene.shape.Sphere;

import emitter.Emitter;

public class EllipsoidEmitter extends Abstract3DEmitter {

    private static final Logger LOG = Logger.getLogger(EllipsoidEmitter.class.getName());

    public EllipsoidEmitter(OGREParticleScript group) {
        super(group);
    }

    @Override
    protected void createEmitterShape(Emitter emitter) {
        LOG.info("    Tri Mesh");
        emitter.setUseRandomEmissionPoint(true);
        Sphere sphere = new Sphere(32, 32, 0.5f, false, false);
        LOG.info(String.format("    Ellipsoid emitter shape of %f x %f x %f", size.x, size.y, size.z));
        emitter.setShape(sphere);
        emitter.setLocalScale(size.x, size.y, size.z);
    }
}
