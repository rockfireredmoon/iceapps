package org.icescene.audio;


public class QueuedAudio {

	private String path;
	private final float interval;
	private boolean loop;
	private final AudioQueue queue;
	private float gain;
	private final Object owner;
	private boolean stream = true;
	private boolean streamCache = true;
	private float refDistance;
	private float maxDistance;
	private boolean positional;
	private AudioQueueHandler queueHandler;

	public QueuedAudio(Object owner, String path, float interval, boolean loop, AudioQueue queue, float gain) {
		this.path = path;
		this.owner = owner;
		this.interval = interval;
		this.loop = loop;
		this.queue = queue;
		this.gain = gain;
	}

	public boolean isStreamCache() {
		return streamCache;
	}

	public void setStreamCache(boolean streamCache) {
		this.streamCache = streamCache;
	}

	public boolean isStream() {
		return stream;
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}

	public float getActualGain() {
		return queue.getBaseGain() * getGain();
	}

	public Object getOwner() {
		return owner;
	}

	public String getPath() {
		return path;
	}

	public float getInterval() {
		return interval;
	}

	public boolean isLoop() {
		return loop;
	}

	public AudioQueue getQueue() {
		return queue;
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public float getRefDistance() {
		return refDistance;
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public void setRefDistance(float refDistance) {
		this.refDistance = refDistance;
	}

	public void setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;

	}

	public boolean isPositional() {
		return positional;
	}

	public void setPositional(boolean positional) {
		this.positional = positional;
	}
	
	public boolean isPlaying() {
		return queueHandler != null;
	}
	
	public void stop() {
		if(queueHandler != null) {
			queueHandler.stopAudio(true);
		}
	}

	@Override
	public String toString() {
		return "QueuedAudio [path=" + path + ", interval=" + interval + ", loop=" + loop + ", queue=" + queue + ", gain=" + gain
				+ ", owner=" + owner + ", stream=" + stream + ", streamCache=" + streamCache + ", refDistance=" + refDistance
				+ ", maxDistance=" + maxDistance + ", positional=" + positional + "]";
	}
	
	void init(AudioQueueHandler queueHandler) {
		this.queueHandler = queueHandler;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueuedAudio other = (QueuedAudio) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (queue != other.queue)
			return false;
		return true;
	}
}
