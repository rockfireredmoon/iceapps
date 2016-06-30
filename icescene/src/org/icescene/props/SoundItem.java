package org.icescene.props;

public class SoundItem {
    private final String name;
    private final String sound;
    private final float gain;

    public SoundItem(String name, String sound, float gain) {
    	if(name == null) {
    		throw new IllegalArgumentException("Name of sound " + sound + " may not be null");
    	}
        this.name = name;
        this.sound = sound;
        this.gain = gain;
    }
    
    public float getGain() {
        return gain;
    }    

    public String getName() {
        return name;
    }

    public String getSound() {
        return sound;
    }
    
}
