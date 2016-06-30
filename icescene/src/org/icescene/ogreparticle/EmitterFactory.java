package org.icescene.ogreparticle;

import java.util.logging.Logger;

import org.icescene.ogreparticle.emitters.BoxEmitter;
import org.icescene.ogreparticle.emitters.CylinderEmitter;
import org.icescene.ogreparticle.emitters.EllipsoidEmitter;
import org.icescene.ogreparticle.emitters.HollowEllipsoidEmitter;
import org.icescene.ogreparticle.emitters.PointEmitter;
import org.icescene.ogreparticle.emitters.RingEmitter;

public abstract class EmitterFactory {
    private static final Logger LOG = Logger.getLogger(EmitterFactory.class.getName());

    private static EmitterFactory instance;

    public static EmitterFactory get() {
        if (instance == null) {
            instance = new EmitterFactory() {
                @Override
                public OGREParticleEmitter createEmitter(String shape, OGREParticleScript script) {
                    // Emitter shape
                    if (shape.equalsIgnoreCase("box")) {
                        return new BoxEmitter( script);
                    } else if (shape.equalsIgnoreCase("cylinder")) {
                        return new CylinderEmitter( script);
                    } else if (shape.equalsIgnoreCase("point")) {
                        return new PointEmitter( script);
                    } else if (shape.equalsIgnoreCase("ring")) {
                        return new RingEmitter( script);
                    } else if (shape.equalsIgnoreCase("ellipsoid")) {
                        return new EllipsoidEmitter( script);
                    } else if (shape.replace(" ", "").equalsIgnoreCase("hollowellipsoid")) {
                        return new HollowEllipsoidEmitter( script);
                    } else {
                        LOG.warning(String.format("TODO: unknown shape %s", shape));
                        return new PointEmitter( script);
                    }
                }
            };
        }
        return instance;
    }

    /**
     * Set an alternative emitter factory.
     *
     * @param factory emitter factory
     */
    public static void set(EmitterFactory instance) {
        EmitterFactory.instance = instance;
    }

    public abstract OGREParticleEmitter createEmitter(String shape, OGREParticleScript script);
}
