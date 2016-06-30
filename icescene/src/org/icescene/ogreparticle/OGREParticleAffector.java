package org.icescene.ogreparticle;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import org.icescene.propertyediting.PropertyBean;

import emitter.influencers.ParticleInfluencer;

public interface OGREParticleAffector extends ParticleInfluencer, PropertyBean {
    OGREParticleScript getScript();

    boolean parse(String[] args, int lineNumber) throws ParseException;

    void write(OutputStream out, boolean b) throws IOException;
}
