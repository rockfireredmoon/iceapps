package org.icescene.ogreparticle.emitters;

import java.util.logging.Logger;

import org.icescene.ogreparticle.OGREParticleScript;

import com.jme3.scene.shape.Cylinder;

import emitter.Emitter;
import emitter.particle.ParticleDataTriMesh;

public class CylinderEmitter extends Abstract3DEmitter {

    private static final Logger LOG = Logger.getLogger(CylinderEmitter.class.getName());

    public CylinderEmitter(OGREParticleScript group) {
        super(group);
    }

    @Override
    protected void createEmitterShape(Emitter emitter) {
        LOG.info("    Tri Mesh");
        emitter.setParticleType(ParticleDataTriMesh.class);
        Cylinder boxTestES = new Cylinder(8, 16, 0.5f, 0.5f, 0.5f, true, false);
        emitter.setShape(boxTestES);
        emitter.setUseRandomEmissionPoint(true);
        emitter.setLocalScale(size.x, size.y, Math.max(0.1f, size.z));
        LOG.info(String.format("    Cyclinder emitter shape of %f x %f x %f", size.x, size.y, size.z));
    }
}
