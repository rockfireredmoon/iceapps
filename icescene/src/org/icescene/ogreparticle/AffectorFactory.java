package org.icescene.ogreparticle;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AffectorFactory {

    private static final Logger LOG = Logger.getLogger(AffectorFactory.class.getName());
    private static AffectorFactory instance;
    
    public static AffectorFactory get() {
        if (instance == null) {
            instance = new AffectorFactory() {
                @Override
                public OGREParticleAffector createAffector(String name, OGREParticleScript script) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    try {
                        Class<? extends OGREParticleAffector> clazz = (Class<? extends OGREParticleAffector>) Class.forName(AffectorFactory.class.getPackage().getName() + ".affectors." + name.replace(" ", "") + "Affector", true, cl == null ? AffectorFactory.class.getClassLoader() : cl);
                        return clazz.getConstructor(OGREParticleScript.class).newInstance(script);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to load affector.", e);
                    }
                    return null;
                }
            };
        }
        return instance;
    }

    /**
     * Set an alternative affector factory.
     *
     * @param factory affector factory
     */
    public static void set(AffectorFactory instance) {
        AffectorFactory.instance = instance;
    }
    
    public abstract OGREParticleAffector createAffector(String name, OGREParticleScript script);
}
