package org.icescene.audio;

import java.util.Collection;
import java.util.prefs.Preferences;

import org.iceui.controls.SoundFieldControl;
import org.iceui.controls.chooser.SoundSourceDialog;
import org.iceui.controls.chooser.SoundSourceDialog.Source;

import icetone.core.ElementManager;

public abstract class AudioField extends SoundFieldControl {
    private final AudioQueue queue;


    public AudioField(ElementManager screen, Type type, String initial,  Collection<String> imageResources, Preferences pref, AudioQueue queue) {
        super(screen, type, initial, imageResources, pref);
        this.queue = queue;
    }

    public AudioField(ElementManager screen, Type type, String UID, String initial, Collection<String> imageResources, Preferences pref, AudioQueue queue) {
        super(screen, type, UID, initial, true, imageResources, pref);
        this.queue = queue;
    }


    public AudioField(ElementManager screen, Type type, String initial, boolean showHex, boolean showChooserButton, Collection<String> imageResources, Preferences pref, AudioQueue queue) {
        super(screen, type, initial, showHex, showChooserButton, imageResources, pref);
        this.queue = queue;
    }

    public AudioField(ElementManager screen, Type type, String UID, String initial, boolean showHex, boolean showChooserButton, Collection<String> imageResources, Preferences pref, AudioQueue queue) {
        super(screen, type, UID, initial, showHex, showChooserButton, imageResources, pref);
        this.queue = queue;
    }

    @Override
    protected void playURL(Source source, String url) {
        AudioAppState as = app.getStateManager().getState(AudioAppState.class);
        as.stopAudio(false, queue);
        final QueuedAudio queuedAudio = new QueuedAudio(this, url, 0, false, queue, 1f);
		queuedAudio.setStream(Source.STREAM_URL.equals(source));
		queuedAudio.setStreamCache(false);
        as.queue(queuedAudio);
        ((SoundSourceDialog)chooser).setStopAvailable(true);
    }
	
	protected boolean isAudioPlaying() {
        AudioAppState as = app.getStateManager().getState(AudioAppState.class);
        return as.isAnyPlaying(queue); 
	}

    @Override
    protected void stopAudio() {
        AudioAppState as = app.getStateManager().getState(AudioAppState.class);
        as.stopAudio(false, queue);
        ((SoundSourceDialog)chooser).setStopAvailable(false);
    }
}
