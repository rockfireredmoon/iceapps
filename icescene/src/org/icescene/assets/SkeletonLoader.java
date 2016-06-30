/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icescene.assets;

import icemoon.iceloader.ServerAssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FilenameUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.plugins.ogre.AnimData;
import com.jme3.util.xml.SAXUtil;

public class SkeletonLoader extends DefaultHandler implements AssetLoader {

	class SkeletonList {
		String path;
		List<String> skeletonFiles = new ArrayList<String>();
		List<Animation> animations;

		SkeletonList(String path) {
			this.path = path;
		}

		public boolean containsMeshFile(String name) {
			return skeletonFiles.contains(name);
		}

		public Collection<? extends Animation> getAnimations() {
			if (animations == null) {
				animations = new ArrayList<>();

				String dir = FilenameUtils.getPath(path);
				String base = FilenameUtils.getBaseName(FilenameUtils.getBaseName(path));

				for (String animFile : ((ServerAssetManager) assetManager).getAssetNamesMatching(String.format("%s%s_.*\\.anim",
						dir, base))) {
					List<Animation> anim = (List<Animation>) assetManager.loadAsset(new AnimKey(animFile));
					if (logger.isLoggable(Level.FINE)) {
						logger.fine(String.format("Loaded %d animations from file %s", anim.size(), animFile));
						for (Animation a : anim) {
							logger.fine(String.format("   %s (length %f) with %d tracks", a.getName(), a.getLength(),
									a.getTracks().length));
						}
					}
					animations.addAll(anim);
				}
			}
			return animations;
		}
	}

	private static final Logger logger = Logger.getLogger(SkeletonLoader.class.getName());
	private AssetManager assetManager;
	private Stack<String> elementStack = new Stack<String>();
	private HashMap<Integer, Bone> indexToBone = new HashMap<Integer, Bone>();
	private HashMap<String, Bone> nameToBone = new HashMap<String, Bone>();
	private BoneTrack track;
	private ArrayList<BoneTrack> tracks = new ArrayList<BoneTrack>();
	private Animation animation;
	private ArrayList<Animation> animations;
	private Bone bone;
	private Skeleton skeleton;
	private ArrayList<Float> times = new ArrayList<Float>();
	private ArrayList<Vector3f> translations = new ArrayList<Vector3f>();
	private ArrayList<Quaternion> rotations = new ArrayList<Quaternion>();
	private ArrayList<Vector3f> scales = new ArrayList<Vector3f>();
	private float time = -1;
	private Vector3f position;
	private Quaternion rotation;
	private Vector3f scale;
	private float angle;
	private Vector3f axis;

	private static Map<String, List<SkeletonList>> skeletonList = null;

	public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException {
		if (qName.equals("position") || qName.equals("translate")) {
			position = SAXUtil.parseVector3(attribs);
		} else if (qName.equals("rotation") || qName.equals("rotate")) {
			angle = SAXUtil.parseFloat(attribs.getValue("angle"));
		} else if (qName.equals("axis")) {
			assert elementStack.peek().equals("rotation") || elementStack.peek().equals("rotate");
			axis = SAXUtil.parseVector3(attribs);
		} else if (qName.equals("scale")) {
			scale = SAXUtil.parseVector3(attribs);
		} else if (qName.equals("keyframe")) {
			assert elementStack.peek().equals("keyframes");
			time = SAXUtil.parseFloat(attribs.getValue("time"));
		} else if (qName.equals("keyframes")) {
			assert elementStack.peek().equals("track");
		} else if (qName.equals("track")) {
			assert elementStack.peek().equals("tracks");
			String boneName = SAXUtil.parseString(attribs.getValue("bone"));
			Bone bone = nameToBone.get(boneName);
			int index = skeleton.getBoneIndex(bone);
			track = new BoneTrack(index);
		} else if (qName.equals("boneparent")) {
			assert elementStack.peek().equals("bonehierarchy");
			String boneName = attribs.getValue("bone");
			String parentName = attribs.getValue("parent");
			Bone bone = nameToBone.get(boneName);
			Bone parent = nameToBone.get(parentName);
			parent.addChild(bone);
		} else if (qName.equals("bone")) {
			assert elementStack.peek().equals("bones");

			// insert bone into indexed map
			bone = new Bone(attribs.getValue("name"));
			int id = SAXUtil.parseInt(attribs.getValue("id"));
			indexToBone.put(id, bone);
			nameToBone.put(bone.getName(), bone);
		} else if (qName.equals("tracks")) {
			assert elementStack.peek().equals("animation");
			tracks.clear();
		} else if (qName.equals("animation")) {
			assert elementStack.peek().equals("animations");
			String name = SAXUtil.parseString(attribs.getValue("name"));
			float length = SAXUtil.parseFloat(attribs.getValue("length"));
			animation = new Animation(name, length);
		} else if (qName.equals("bonehierarchy")) {
			assert elementStack.peek().equals("skeleton");
		} else if (qName.equals("animations")) {
			assert elementStack.peek().equals("skeleton");
			animations = new ArrayList<Animation>();
		} else if (qName.equals("bones")) {
			assert elementStack.peek().equals("skeleton");
		} else if (qName.equals("skeleton")) {
			assert elementStack.size() == 0;
		}
		elementStack.add(qName);
	}

	public void endElement(String uri, String name, String qName) {
		if (qName.equals("translate") || qName.equals("position") || qName.equals("scale")) {
		} else if (qName.equals("axis")) {
		} else if (qName.equals("rotate") || qName.equals("rotation")) {
			rotation = new Quaternion();
			axis.normalizeLocal();
			rotation.fromAngleNormalAxis(angle, axis);
			angle = 0;
			axis = null;
		} else if (qName.equals("bone")) {
			bone.setBindTransforms(position, rotation, scale);
			bone = null;
			position = null;
			rotation = null;
			scale = null;
		} else if (qName.equals("bonehierarchy")) {
			Bone[] bones = new Bone[indexToBone.size()];
			// find bones without a parent and attach them to the skeleton
			// also assign the bones to the bonelist
			for (Map.Entry<Integer, Bone> entry : indexToBone.entrySet()) {
				Bone bone = entry.getValue();
				bones[entry.getKey()] = bone;
			}
			indexToBone.clear();
			skeleton = new Skeleton(bones);
		} else if (qName.equals("animation")) {
			animations.add(animation);
			animation = null;
		} else if (qName.equals("track")) {
			if (track != null) { // if track has keyframes
				tracks.add(track);
				track = null;
			}
		} else if (qName.equals("tracks")) {
			BoneTrack[] trackList = tracks.toArray(new BoneTrack[tracks.size()]);
			animation.setTracks(trackList);
			tracks.clear();
		} else if (qName.equals("keyframe")) {
			assert time >= 0;
			assert position != null;
			assert rotation != null;

			times.add(time);
			translations.add(position);
			rotations.add(rotation);
			if (scale != null) {
				scales.add(scale);
			} else {
				scales.add(new Vector3f(1, 1, 1));
			}

			time = -1;
			position = null;
			rotation = null;
			scale = null;
		} else if (qName.equals("keyframes")) {
			if (times.size() > 0) {
				float[] timesArray = new float[times.size()];
				for (int i = 0; i < timesArray.length; i++) {
					timesArray[i] = times.get(i);
				}

				Vector3f[] transArray = translations.toArray(new Vector3f[translations.size()]);
				Quaternion[] rotArray = rotations.toArray(new Quaternion[rotations.size()]);
				Vector3f[] scalesArray = scales.toArray(new Vector3f[scales.size()]);

				track.setKeyframes(timesArray, transArray, rotArray, scalesArray);
				// track.setKeyframes(timesArray, transArray, rotArray);
			} else {
				track = null;
			}

			times.clear();
			translations.clear();
			rotations.clear();
			scales.clear();
		} else if (qName.equals("skeleton")) {
			nameToBone.clear();
		}
		assert elementStack.peek().equals(qName);
		elementStack.pop();
	}

	/**
	 * Reset the SkeletonLoader in case an error occured while parsing XML. This
	 * allows future use of the loader even after an error.
	 */
	private void fullReset() {
		elementStack.clear();
		indexToBone.clear();
		nameToBone.clear();
		track = null;
		tracks.clear();
		animation = null;
		if (animations != null) {
			animations.clear();
		}

		bone = null;
		skeleton = null;
		times.clear();
		rotations.clear();
		translations.clear();
		time = -1;
		position = null;
		rotation = null;
		scale = null;
		angle = 0;
		axis = null;
	}

	public Object load(InputStream in) throws IOException {
		return doLoad(in, null, null);
	}

	protected Object doLoad(InputStream in, String dir, String name) throws IOException {
		try {

			// Added by larynx 25.06.2011
			// Android needs the namespace aware flag set to true
			// Kirill 30.06.2011
			// Now, hack is applied for both desktop and android to avoid
			// checking with JmeSystem.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XMLReader xr = factory.newSAXParser().getXMLReader();

			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			InputStreamReader r = new InputStreamReader(in);
			xr.parse(new InputSource(r));
			if (animations == null) {
				animations = new ArrayList<Animation>();
			}

			/*
			 * If we know the name and path of this skeleton file, look for
			 * skeletonlist files in the same folder. If the name of this
			 * skeleton is in the list, then all the .anim files that have the
			 * same prefix are loaded.
			 */
			if (name != null && dir != null) {

				if (skeletonList == null) {
					// Load the skeletonlist files for this asset folder
					skeletonList = new HashMap<String, List<SkeletonList>>();
					Set<String> skelListPaths = ((ServerAssetManager) assetManager).getAssetNamesMatching(".*\\.skeletonlist");
					List<SkeletonList> skelFiles;
					for (String skelListPath : skelListPaths) {
						String skelDir = FilenameUtils.getPath(skelListPath);
						skelFiles = skeletonList.get(skelDir);
						if (skelFiles == null) {
							skelFiles = new ArrayList<SkeletonLoader.SkeletonList>();
							skeletonList.put(skelDir, skelFiles);
						}
						logger.info(String.format("Loading skeleton list file %s", skelListPath));
						SkeletonList l = new SkeletonList(skelListPath);
						AssetInfo info = assetManager.locateAsset(new AssetKey<String>(skelListPath));
						BufferedReader reader = new BufferedReader(new InputStreamReader(info.openStream()));
						try {
							String line = null;
							while ((line = reader.readLine()) != null) {
								line = line.trim();
								if (!line.startsWith("#")) {
									logger.info(String.format("%s has animations loaded from skeleton list file %s", line,
											skelListPath));
									l.skeletonFiles.add(line);
								}
							}
						} finally {
							reader.close();
						}
						skelFiles.add(l);

					}
				}

				String basename = FilenameUtils.getName(name);
				if (basename.endsWith(".xml")) {
					basename = FilenameUtils.getBaseName(basename);
				}

				/*
				 * If there are no skeleton list files in the directory, just
				 * look for all animations in that directory
				 */
				if (!skeletonList.containsKey(dir)) {
					SkeletonList l = new SkeletonList(name);
					l.skeletonFiles.add(basename);
					skeletonList.put(dir, Arrays.asList(l));
				}

				/*
				 * Does this asset path exist in any of the skeleton lists. If
				 * so, load all of the animations that have the same prefix
				 * (from the same folder as the list file)
				 */
				for (Map.Entry<String, List<SkeletonList>> en : skeletonList.entrySet()) {
					for (SkeletonList l : en.getValue()) {
						if (l.containsMeshFile(basename)) {
							animations.addAll(l.getAnimations());
						}
					}
				}

			}

			AnimData data = new AnimData(skeleton, animations);
			skeleton = null;
			animations = null;
			return data;
		} catch (SAXException ex) {
			IOException ioEx = new IOException("Error while parsing Ogre3D dotScene");
			ioEx.initCause(ex);
			fullReset();
			throw ioEx;
		} catch (ParserConfigurationException ex) {
			IOException ioEx = new IOException("Error while parsing Ogre3D dotScene");
			ioEx.initCause(ex);
			fullReset();
			throw ioEx;
		}

	}

	public Object load(AssetInfo info) throws IOException {
		assetManager = info.getManager();
		InputStream in = null;
		try {
			String dir = info.getKey().getFolder();
			in = info.openStream();
			return doLoad(in, dir, info.getKey().getName());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}
