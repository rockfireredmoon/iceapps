package org.icescene.ogreparticle;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import org.icescene.propertyediting.PropertyBean;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import emitter.Emitter;

public interface OGREParticleEmitter extends PropertyBean {
    
    OGREParticleScript getScript();

    void setAngle(float angle);

    void setDirection(Vector3f direction);

    void setVelocity(float velocity);

    void setVelocityMin(float velocityMin);

    void setVelocityMax(float velocityMax);

    void setTimeToLive(float timeToLive);

    void setDuration(float duration);

    void setDurationMax(float durationMax);

    void setDurationMin(float durationMin);

    void setRepeatDelayMax(float repeatDelayMax);

    void setRepeatDelayMin(float repeatDelayMin);

    void setRepeatDelay(float repeatDelay);

    void setParticlesPerSec(float particlesPerSec);

    void setLocalTranslation(Vector3f localTranslation);

    void setLowLife(float lowLife);

    void setHighLife(float highLife);
    
    void setColour(ColorRGBA colour);
    
    void setStartColour(ColorRGBA startColour);
    
    void setEndColour(ColorRGBA startColour);

    Emitter createEmitter(AssetManager assetManager);

    boolean parse(String[] args, int lineNumber) throws ParseException;

    void write(OutputStream fos, boolean b) throws IOException;
}
