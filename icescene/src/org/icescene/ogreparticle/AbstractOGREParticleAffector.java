package org.icescene.ogreparticle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import emitter.influencers.ParticleInfluencer;

public abstract class AbstractOGREParticleAffector implements OGREParticleAffector {
    
    protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected final OGREParticleScript script;
    
    public AbstractOGREParticleAffector(OGREParticleScript script) {
        this.script = script;
    }
    
    public OGREParticleScript getScript() {
        return script;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public abstract ParticleInfluencer clone();
    
    public String getName() {
        String cn = getClass().getSimpleName();
        if(cn.endsWith("Affector")) {
            cn = cn.substring(0, cn.length() -  8);
        }
        return cn;
    }
    
    public void write(OutputStream fos, boolean b) throws IOException {
        PrintWriter pw = new PrintWriter(fos);
        pw.println(String.format("\taffector %s", getName()));
        pw.println("\t{");
        writeAffector(pw);
        pw.println("\t}");
        pw.flush();
    }
    
    protected abstract void writeAffector(PrintWriter pw);
}
