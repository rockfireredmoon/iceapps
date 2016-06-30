package org.icescene.audio;

import java.io.IOException;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;

public class QueueData implements Savable {

	QueuedAudio qa;

	QueueData(QueuedAudio qa) {
		this.qa = qa;
	}

	public void write(JmeExporter ex) throws IOException {
	}

	public void read(JmeImporter im) throws IOException {
	}
}