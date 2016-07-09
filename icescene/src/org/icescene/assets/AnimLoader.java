package org.icescene.assets;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.icelib.OGRESerializerDataInputStream;

import com.jme3.animation.Animation;
import com.jme3.animation.BoneTrack;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AnimLoader implements AssetLoader {
	final static Logger LOG = Logger.getLogger(AnimLoader.class.getName());

	public final static int SKELETON_ANIMATION_BASEINFO = 0x4010;
	public final static int SKELETON_ANIMATION = 0x4000;
	public final static int SKELETON_ANIMATION_TRACK = 0x4100;
	public final static int SKELETON_ANIMATION_TRACK_KEYFRAME = 0x4110;

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		Loader l = new Loader();
		InputStream openStream = assetInfo.openStream();
		try {
			l.process(openStream);
		} finally {
			openStream.close();
		}
		return l.animations;
	}

	static class Loader {
		private AnimList animations = new AnimList();
		private List<BoneTrack> tracks = new ArrayList<>();
		private ArrayList<Float> times = new ArrayList<Float>();
		private ArrayList<Vector3f> translations = new ArrayList<Vector3f>();
		private ArrayList<Quaternion> rotations = new ArrayList<Quaternion>();
		private ArrayList<Vector3f> scales = new ArrayList<Vector3f>();

		Loader() {
		}

		@SuppressWarnings("resource")
		void process(InputStream in) throws IOException {
			OGRESerializerDataInputStream din = new OGRESerializerDataInputStream(new BufferedInputStream(in, 65536 * 10));

			try {
				String ofusionVer = din.readFileHeader();
				if (!ofusionVer.equals("[oFusion_Serializer_v1.0o]") && !ofusionVer.equals("[oFusion_Serializer_v1.0]")) {
					throw new IOException("Not an oFusion .anim file (" + ofusionVer + ")");
				}

				din.readShort(); // No bones

				String ogreVer = din.readFileHeader();
				if (!ogreVer.equals("[Serializer_v1.10]") && !ogreVer.equals("[Serializer_v1.80]")) {
					throw new IOException("Not an OGRE serialized file");
				}
				Chunk ct = new Chunk(din);
				while (true) {
					switch (ct.id) {
					case SKELETON_ANIMATION:
						readAnimation(din, ct);
						break;
					default:
						throw new IOException("Unknown chunk type " + ct.id);
					}
				}
				//
			} catch (EOFException eof) {
			} 
		}

		private void readAnimation(OGRESerializerDataInputStream din, Chunk ct) throws IOException {
			String name = din.readString();
			float animLength = din.readFloat();

			Animation anim = new Animation(name, animLength);
			animations.add(anim);
			tracks = new ArrayList<BoneTrack>();

			ct.read();
			switch (ct.id) {
			case SKELETON_ANIMATION_BASEINFO:
				String baseAnimName = din.readString();
				float baseKeyTime = din.readFloat();
				ct.read();
				throw new UnsupportedOperationException(String.format("Base info %s, %f is not supported.", baseAnimName,
						baseKeyTime));
			}

			try {
				while (ct.id == SKELETON_ANIMATION_TRACK) {
					readAnimationTrack(ct, din, anim);
				}
			} finally {
				anim.setTracks(tracks.toArray(new BoneTrack[tracks.size()]));
				tracks.clear();
			}
		}

		private void readAnimationTrack(Chunk ct, DataInput din, Animation anim) throws IOException {
			short boneHandle = din.readShort();
			BoneTrack track = new BoneTrack(boneHandle);
			ct.read();
			while (ct.id == SKELETON_ANIMATION_TRACK_KEYFRAME) {
				readKeyFrame(ct, din, track);
			}

			float[] timesArray = new float[times.size()];
			for (int i = 0; i < timesArray.length; i++) {
				timesArray[i] = times.get(i);
			}
			track.setKeyframes(timesArray, translations.toArray(new Vector3f[translations.size()]),
					rotations.toArray(new Quaternion[rotations.size()]), scales.toArray(new Vector3f[scales.size()]));
			translations.clear();
			rotations.clear();
			scales.clear();
			times.clear();
			tracks.add(track);

		}

		private void readKeyFrame(Chunk ct, DataInput din, BoneTrack track) throws IOException {
			float time = din.readFloat();
			times.add(time);

			float[] rot = new float[4];
			rot[0] = din.readFloat();
			rot[1] = din.readFloat();
			rot[2] = din.readFloat();
			rot[3] = din.readFloat();
			rotations.add(new Quaternion(rot[0], rot[1], rot[2], rot[3]));

			float[] trans = new float[3];
			trans[0] = din.readFloat();
			trans[1] = din.readFloat();
			trans[2] = din.readFloat();
			translations.add(new Vector3f(trans[0], trans[1], trans[2]));

			float[] scale = null;
			if (ct.len > 38) {
				scale = new float[3];
				scale[0] = din.readFloat();
				scale[1] = din.readFloat();
				scale[2] = din.readFloat();
				scales.add(new Vector3f(scale[0], scale[1], scale[2]));
			} else {
				scales.add(new Vector3f(1, 1, 1));
			}

			ct.read();
		}

	}

	static class Chunk {
		short id;
		int len;
		DataInput din;

		Chunk(DataInput din) throws IOException {
			this.din = din;
			read();
		}

		void read() throws IOException {
			id = din.readShort();
			len = din.readInt();
		}
	}
}
