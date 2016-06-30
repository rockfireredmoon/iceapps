package org.icescene.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;

public class XAudioNode extends AudioNode {

	public XAudioNode() {
	}

	public XAudioNode(AudioData audioData, AudioKey audioKey) {
		super(audioData, audioKey);
	}

	public XAudioNode(AssetManager assetManager, String name, boolean stream, boolean streamCache) {
		super(assetManager, name, stream, streamCache);
	}

	public XAudioNode(AssetManager assetManager, String name, boolean stream) {
		super(assetManager, name, stream);
	}

	public XAudioNode(AssetManager assetManager, String name) {
		super(assetManager, name);
	}

	public boolean isLoop() {
		return loop;
	}

	public AudioKey getAudioKey() {
		return audioKey;
	}
}