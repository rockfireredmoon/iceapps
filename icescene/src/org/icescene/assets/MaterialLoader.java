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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.assets.ExtendedMaterialListKey.Lighting;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.MaterialList;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.plugins.ogre.matext.MaterialExtensionLoader;
import com.jme3.scene.plugins.ogre.matext.MaterialExtensionSet;
import com.jme3.scene.plugins.ogre.matext.OgreMaterialKey;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.Type;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.util.PlaceholderAssets;
import com.jme3.util.blockparser.BlockLanguageParser;
import com.jme3.util.blockparser.Statement;

public class MaterialLoader implements AssetLoader {

	private static final Logger logger = Logger.getLogger(MaterialLoader.class.getName());

	private String folderName;
	private AssetManager assetManager;
	private ColorRGBA ambient, diffuse, specular, emissive;
	private Texture[] textures = new Texture[4];
	private String[] textureAlias = new String[4];
	private Vector2f[] scrollAnim = new Vector2f[4];
	private Vector2f[] scroll = new Vector2f[4];
	private Vector2f[] scale = new Vector2f[4];
	private String texName;
	private String matName;
	private float shinines;
	private boolean vcolor = false;
	private BlendMode blend = BlendMode.Off;
	private boolean twoSide = false;
	private Boolean depthWrite = null;
	private Boolean depthCheck = null;
	private boolean noLight = false;
	private boolean separateTexCoord = false;
	private int texUnit = 0;
	private int alphaRejection = -1;
	private boolean disabled;
	private float depthBias;
	private String name;
	private boolean flipTextureY;
	private int lod;
	private int pass;
	private boolean grass;
	private boolean pointSprite;
	private int priorityBias;
	private float slopeScaleBias;

	private ColorRGBA readColor(String content) {
		String[] split = content.split("\\s");

		ColorRGBA color = new ColorRGBA();
		color.r = Float.parseFloat(split[0]);
		color.g = Float.parseFloat(split[1]);
		color.b = Float.parseFloat(split[2]);
		if (split.length >= 4) {
			color.a = Float.parseFloat(split[3]);
		}
		return color;
	}

	private void readTextureImage(String content) {
		// texture image def
		String path = null;

		// find extension
		int extStart = content.lastIndexOf(".");
		for (int i = extStart; i < content.length(); i++) {
			char c = content.charAt(i);
			if (Character.isWhitespace(c)) {
				// extension ends here
				path = content.substring(0, i).trim();
				content = content.substring(i + 1).trim();
				break;
			}
		}
		if (path == null) {
			path = content.trim();
			content = "";
		}

		Scanner lnScan = new Scanner(content);
		try {
			String type = null;
			if (lnScan.hasNext()) {
				// more params
				type = lnScan.next();
				// if (!lnScan.hasNext("\n") && lnScan.hasNext()){
				// mips = lnScan.next();
				// if (lnScan.hasNext()){
				// even more params..
				// will have to ignore
				// }
				// }
			}

			boolean genMips = true;
			boolean cubic = false;
			if (type != null && type.equals("0"))
				genMips = false;

			if (type != null && type.equals("cubic")) {
				cubic = true;
			}

			TextureKey texKey = new TextureKey(folderName + path, false);
			texKey.setGenerateMips(genMips);
			if(cubic)
				texKey.setTextureTypeHint(Type.CubeMap);
			texKey.setFlipY(flipTextureY);

			try {
				Texture loadedTexture = assetManager.loadTexture(texKey);

				textures[texUnit].setImage(loadedTexture.getImage());
				textures[texUnit].setMinFilter(loadedTexture.getMinFilter());
				textures[texUnit].setKey(loadedTexture.getKey());

				// XXX: Is this really neccessary?
				textures[texUnit].setWrap(WrapMode.Repeat);
				if (texName != null) {
					textures[texUnit].setName(texName);
					texName = null;
				} else {
					textures[texUnit].setName(texKey.getName());
				}
			} catch (AssetNotFoundException ex) {
				logger.log(Level.WARNING, "Cannot locate {0} for material {1}", new Object[] { texKey, matName });
				textures[texUnit].setImage(PlaceholderAssets.getPlaceholderImage());
			}
		} finally {
			lnScan.close();
		}
	}

	private void readTextureUnitStatement(Statement statement, AssetKey key) {
		String[] split = statement.getLine().split(" ", 2);
		String keyword = split[0];
		if (keyword.equals("texture")) {
			readTextureImage(split[1]);
		} else if (keyword.equals("texture_alias")) {
			textureAlias[texUnit] = split[1];
		} else if (keyword.equals("tex_address_mode")) {
			String mode = split[1];
			if (mode.equals("wrap")) {
				textures[texUnit].setWrap(WrapMode.Repeat);
			} else if (mode.equals("clamp")) {
				textures[texUnit].setWrap(WrapMode.Clamp);
			} else if (mode.equals("mirror")) {
				textures[texUnit].setWrap(WrapMode.MirroredRepeat);
			} else if (mode.equals("border")) {
				textures[texUnit].setWrap(WrapMode.BorderClamp);
			}
		} else if (keyword.equals("scroll_anim")) {
			String[] v = split[1].split("\\s+");
			scrollAnim[texUnit] = new Vector2f(Float.parseFloat(v[0]), Float.parseFloat(v[1]));
		} else if (keyword.equals("scroll")) {
			String[] v = split[1].split("\\s+");
			scroll[texUnit] = new Vector2f(Float.parseFloat(v[0]), Float.parseFloat(v[1]));
		} else if (keyword.equals("scale")) {
			String[] v = split[1].split("\\s+");
			scale[texUnit] = new Vector2f(Float.parseFloat(v[0]), Float.parseFloat(v[1]));
		} else if (keyword.equals("filtering")) {
			// ignored.. only anisotropy is considered
		} else if (keyword.equals("tex_coord_set")) {
			int texCoord = Integer.parseInt(split[1]);
			if (texCoord == 1) {
				separateTexCoord = true;
			}
		} else if (keyword.equals("max_anisotropy")) {
			int amount = Integer.parseInt(split[1]);
			textures[texUnit].setAnisotropicFilter(amount);
		} else {
			logger.log(Level.WARNING, "Unsupported texture_unit directive: {0} (line {1}, {2})", new Object[] { keyword, statement.getLineNumber(), key.getName() });
		}
	}

	private void readTextureUnit(Statement statement, AssetKey key) {
		String[] split = statement.getLine().split(" ", 2);
		// name is optional
		if (split.length == 2) {
			texName = split[1];
		} else {
			texName = null;
		}

		textures[texUnit] = new Texture2D();
		for (Statement texUnitStat : statement.getContents()) {
			readTextureUnitStatement(texUnitStat, key);
		}
		if (textures[texUnit] != null) {
			texUnit++;
		} else {
			// no image was loaded, ignore
			textures[texUnit] = null;
		}
	}

	private void readPassStatement(Statement statement, AssetKey key) {
		// read until newline
		String[] split = statement.getLine().split(" ", 2);
		String keyword = split[0];
		if (keyword.equals("diffuse")) {
			if (split[1].equals("vertexcolour")) {
				// use vertex colors
				diffuse = ColorRGBA.White;
				vcolor = true;
			} else {
				diffuse = readColor(split[1]);
			}
		} else if (keyword.equals("ambient")) {
			if (split[1].equals("vertexcolour")) {
				// use vertex colors
				ambient = ColorRGBA.White;
			} else {
				ambient = readColor(split[1]);
			}
		} else if (keyword.equals("emissive")) {
			emissive = readColor(split[1]);
		} else if (keyword.equals("specular")) {
			String[] subsplit = split[1].split("\\s");
			specular = new ColorRGBA();
			specular.r = Float.parseFloat(subsplit[0]);
			specular.g = Float.parseFloat(subsplit[1]);
			specular.b = Float.parseFloat(subsplit[2]);
			float unknown = Float.parseFloat(subsplit[3]);
			if (subsplit.length >= 5) {
				// using 5 float values
				specular.a = unknown;
				shinines = Float.parseFloat(subsplit[4]);
			} else {
				// using 4 float values
				specular.a = 1f;
				shinines = unknown;
			}
		} else if (keyword.equals("texture_unit")) {
			readTextureUnit(statement, key);
		} else if (keyword.equals("scene_blend")) {
			String mode = split[1];
			if (pass < 1) {
				if (mode.equals("alpha_blend")) {
					blend = BlendMode.Alpha;
				} else if (mode.equals("add")) {
					blend = BlendMode.Additive;
				} else if (mode.equals("colour_blend")) {
					blend = BlendMode.Color;
				} else if (mode.equals("modulate")) {
					blend = BlendMode.Modulate;
				} else {
					logger.log(Level.WARNING, "Unsupported blend function: {0} (line {1}, {2})", new Object[] { mode, statement.getLineNumber(), key.getName() });
				}
			} else {
				logger.log(Level.WARNING, "Ignoring blend mode in secondary pass: {0} (line {1}, {2})", new Object[] { mode, statement.getLineNumber(), key.getName() });
			}
		} else if (keyword.equals("alpha_rejection")) {
			String op = split[1];
			if (op.startsWith("greater_equal ")) {
				split = split[1].split("\\s+");
				alphaRejection = Integer.parseInt(split[1]);
			} else {
				logger.log(Level.WARNING, "Unsupported alpha_rejection function: {0} (line {1}, {2})", new Object[] { split[1], statement.getLineNumber(), key.getName() });
			}
		} else if (keyword.equals("cull_hardware")) {
			String mode = split[1];
			if (mode.equals("none")) {
				twoSide = true;
			}
		} else if (keyword.equals("vertex_program_ref")) {
			if (split[1].equals("VP_Grass")) {
				grass = true;
			} else {
				logger.log(Level.WARNING, "Unsupported pass directive: {0} (line {1}, {2})", new Object[] { split[1], statement.getLineNumber(), key.getName() });
			}

			// vertex_program_ref VP_Grass
			// {
			// // ...
			// }
			//
			// fragment_program_ref FP_Grass
			// {
			// // ...
			// }
		} else if (keyword.equals("depth_bias")) {
			/*
			 * When polygons are coplanar, you can get problems with 'depth
			 * fighting' where the pixels from the two polys compete for the
			 * same screen pixel. This is particularly a problem for decals
			 * (polys attached to another surface to represent details such as
			 * bulletholes etc.). A way to combat this problem is to use a depth
			 * bias to adjust the depth buffer value used for the decal such
			 * that it is slightly higher than the true value, ensuring that the
			 * decal appears on top. There are two aspects to the biasing, a
			 * constant bias value and a slope-relative biasing value, which
			 * varies according to the maximum depth slope relative to the
			 * camera, ie:
			 *
			 * finalBias = maxSlope * slopeScaleBias + constantBias
			 * 
			 * Note that slope scale bias, whilst more accurate, may be ignored
			 * by old hardware.
			 */
			String[] vals = split[1].split("\\s+");
			depthBias = Float.parseFloat(vals[0]);
			if (vals.length > 1)
				slopeScaleBias = Float.parseFloat(vals[1]);
			else
				slopeScaleBias = 0.0f;
		} else if (keyword.equals("depth_write")) {
			depthWrite = !split[1].equalsIgnoreCase("off");
		} else if (keyword.equals("depth_check")) {
			depthCheck = !split[1].equalsIgnoreCase("off");
		} else if (keyword.equals("cull_software")) {
			// ignore
		} else if (keyword.equals("point_sprites")) {
			pointSprite = split[1].equalsIgnoreCase("on");
			// ignore
		} else if (keyword.equals("lighting")) {
			String isOn = split[1];
			if (isOn.equals("on")) {
				noLight = false;
			} else if (isOn.equals("off")) {
				noLight = true;
			}
		} else {
			logger.log(Level.WARNING, "Unsupported pass directive: {0} (line {1}, {2})", new Object[] { keyword, statement.getLineNumber(), key.getName() });
		}
	}
	
	private void readPass(Statement statement, AssetKey key) {
		String name;
		String[] split = statement.getLine().split(" ", 2);
		if (split.length == 1) {
			// no name
			name = null;
		} else {
			name = split[1];
		}

		for (Statement passStat : statement.getContents()) {
			readPassStatement(passStat, key);
		}
		pass++;
	}

	private void readTechnique(Statement statement, AssetKey key) {
		String[] split = statement.getLine().split(" ", 2);
		pass = 0;
		if (split.length == 1) {
			// no name
			name = null;
		} else {
			name = split[1];
		}

		for (Statement techStat : statement.getContents()) {
			if (techStat.getLine().startsWith("lod_index")) {
				split = techStat.getLine().split(" ", 2);
				lod = Integer.parseInt(split[1]);
			} else if (techStat.getLine().startsWith("priority_bias")) {
				split = techStat.getLine().split(" ", 2);
				priorityBias = Integer.parseInt(split[1]);
			} else if (techStat.getLine().startsWith("disabled")) {
				split = techStat.getLine().split(" ", 2);
				disabled = split[1].equals("true");
			} else if (techStat.getLine().startsWith("pass")) {
				readPass(techStat, key);
			} else {
				logger.log(Level.WARNING, "Unsupported technique directive: {0} (line {1}, {2})", new Object[] { techStat.getLine(), techStat.getLineNumber(), key.getName() });
			}
		}

		texUnit = 0;
	}

	private void readMaterialStatement(Statement statement, AssetKey key) {
		if (statement.getLine().startsWith("technique")) {
			readTechnique(statement, key);
		} else if (statement.getLine().startsWith("receive_shadows")) {
			String isOn = statement.getLine().split("\\s")[1];
			if (isOn != null && isOn.equals("true")) {
			}
		}
	}

	private void readMaterial(Statement statement, AssetKey key) {
		for (Statement materialStat : statement.getContents()) {
			readMaterialStatement(materialStat, key);
		}
	}

	private Material compileMaterial(ExtendedMaterialListKey key, String name) {
		Material mat = key.createMaterial(assetManager, !noLight);
		ExtendedMaterialKey matKey = new ExtendedMaterialKey(key.getName() + "/" + name);
		mat.setKey(matKey);
		matKey.setListKey(key);
		boolean actualNoLight = noLight;
		if (key.getLighting() != Lighting.DEFAULT) {
			actualNoLight = key.getLighting() == Lighting.UNLIT;
		}
		matKey.setLit(!actualNoLight);
		try {
			matKey.getLOD(lod).setDisabled(disabled);
			if (blend != BlendMode.Off) {
				RenderState rs = mat.getAdditionalRenderState();
				rs.setAlphaTest(true);
				rs.setAlphaFallOff(0.01f);
				rs.setBlendMode(blend);

				if (twoSide) {
					rs.setFaceCullMode(RenderState.FaceCullMode.Off);
				}

				if (depthWrite != null) {
					rs.setDepthWrite(depthWrite);
				}
				if (depthCheck != null) {
					rs.setDepthTest(depthCheck);
				}
				if (depthBias != 0 || slopeScaleBias != 0)
					rs.setPolyOffset(depthBias, slopeScaleBias);

				rs.setPointSprite(pointSprite);
				// rs.setDepthWrite(false);
				mat.setTransparent(true);
				if (!actualNoLight) {
					mat.setBoolean("UseAlpha", true);
				}
			} else {
				RenderState rs = mat.getAdditionalRenderState();
				if (twoSide) {
					rs.setFaceCullMode(RenderState.FaceCullMode.Off);
				}
				if (depthWrite != null) {
					rs.setDepthWrite(depthWrite);
				}
				if (depthCheck != null) {
					rs.setDepthTest(depthCheck);
				}
				if (depthBias != 0 || slopeScaleBias != 0)
					rs.setPolyOffset(depthBias, slopeScaleBias);
				rs.setPointSprite(pointSprite);
			}

			if (alphaRejection > -1) {
				// mat.getAdditionalRenderState().setBlendMode(BlendMode.Off);
				// mat.getAdditionalRenderState().setAlphaTest(true);
				float as = (float) alphaRejection / 255f;
				mat.setFloat("AlphaDiscardLimit", as);
				mat.setTransparent(true);
			}

			if (scrollAnim.length > 0) {
				// TODO fix
				// mat.setFloat("GlobalAnimSpeed",
				// SceneConstants.SCROLL_SHADER_SPEED);
			}
			for (int i = 0; i < scrollAnim.length; i++) {
				if (scrollAnim[i] != null) {
					mat.setFloat("ScrollAnim" + (i + 1) + "X", scrollAnim[i].x);
					mat.setFloat("ScrollAnim" + (i + 1) + "Y", scrollAnim[i].y);
				}
			}
			for (int i = 0; i < scale.length; i++) {
				if (scale[i] != null) {
					// TODO no x/y scaling
					mat.setFloat("Scale" + (i + 1), scale[i].x);
				}
			}
			for (int i = 0; i < scroll.length; i++) {
				if (scroll[i] != null) {
					mat.setFloat("Scroll" + (i + 1) + "X", scroll[i].x);
					mat.setFloat("Scroll" + (i + 1) + "Y", scroll[i].y);
				}
			}

			List<String> defaultAliases = getDefaultAliases("Diffuse", "Illuminated");

			if (!actualNoLight) {
				if (shinines > 0f) {
					mat.setFloat("Shininess", shinines);
				} else {
					mat.setFloat("Shininess", 1f); // set shininess to some
													// value
													// anyway..
				}

				if (vcolor)
					mat.setBoolean("UseVertexColor", true);

				fillDefaultAliases(defaultAliases);
				setAllTextures("DiffuseMap", "IllumMap", key, mat);
				mat.setBoolean("UseMaterialColors", true);
				if (diffuse != null) {
					mat.setColor("Diffuse", diffuse);
				} else {
					mat.setColor("Diffuse", ColorRGBA.White);
				}
				if (ambient != null) {
					mat.setColor("Ambient", ambient);
				} else {
					mat.setColor("Ambient", ColorRGBA.DarkGray);
				}
				if (specular != null) {
					mat.setColor("Specular", specular);
				} else {
					mat.setColor("Specular", ColorRGBA.Black);
				}

				if (emissive != null) {
					mat.setColor("GlowColor", emissive);
				}
			} else {
				if (vcolor) {
					mat.setBoolean("VertexColor", true);
				}
				fillDefaultAliases(defaultAliases);
				if (separateTexCoord) {
					mat.setBoolean("SeparateTexCoord", true);
				}
				setAllTextures("ColorMap", "IllumMap", key, mat);
				if (diffuse != null) {
					mat.setColor("Color", diffuse);
				}

				if (emissive != null) {
					mat.setColor("GlowColor", emissive);
				}
			}

			if (grass) {
				mat.setBoolean("Swaying", true);
				mat.setVector3("SwayData", new Vector3f(1.5f, 1f, 10f));
				mat.setVector2("Wind", new Vector2f(1f, 5f));
				// mat.setTexture("AlphaNoiseMap",
				// assetManager.loadTexture("Textures/noise.png"));

				// mat.setBoolean("Invert", true);

				// mat.setFloat("FadeEnd", 500);
//				 mat.setBoolean("FadeEnabled", true);
				// mat.setFloat("FadeRange", 250);
				// mat.setBoolean("UseAlpha", true);
				// mat.setFloat("AlphaDiscardLimit", 0.70588f);
			}

			actualNoLight = false;
			lod = 0;
			priorityBias = 0;
			Arrays.fill(textures, null);
			Arrays.fill(textureAlias, null);
			Arrays.fill(scrollAnim, null);
			Arrays.fill(scroll, null);
			Arrays.fill(scale, null);
			diffuse = null;
			specular = null;
			depthWrite = null;
			depthCheck = null;
			depthBias = 0;
			slopeScaleBias = 0;
			pointSprite = false;
			shinines = 0f;
			emissive = null;
			vcolor = false;
			grass = false;
			disabled = false;
			blend = BlendMode.Off;
			alphaRejection = -1;
			texUnit = 0;
			separateTexCoord = false;
			flipTextureY = false;
			return mat;
		} catch (IllegalArgumentException iae) {
			throw new RuntimeException(String.format("Failed to load material %s (%s)", key, matKey), iae);
		}
	}

	private void setAllTextures(String diffuseMatName, String glowMatName, ExtendedMaterialListKey key, Material mat) {

		/*
		 * Set the actual textures into the material if the material specifies
		 * an image. If the texture doesn't specify it, then add the alias and
		 * the material parameter name to use to the 'texture alias' map, which
		 * the MeshLoader then uses to populate the remaining textures when it
		 * reads them.
		 */

		// Do any specific DiffuseMap or LightMap first

		int diffuseMapIndex = 1;
		for (int i = 0; i < textureAlias.length; i++) {
			if (textureAlias[i] != null) {
				String matKey = null;
				if (textureAlias[i].equals("Diffuse")) {
					matKey = diffuseMatName;
					diffuseMapIndex++;
				} else if (textureAlias[i].equals("Illuminated")) {
					matKey = glowMatName;
				}
				if (matKey != null) {
					setMatTex(key, mat, i, matKey);
					textureAlias[i] = null;
				}
			}
		}

		// Everything else is diffuse map

		for (int i = 0; i < textureAlias.length; i++) {
			if (textureAlias[i] != null) {
				String matKey = diffuseMapIndex == 1 ? diffuseMatName : diffuseMatName + diffuseMapIndex;
				setMatTex(key, mat, i, matKey);
				diffuseMapIndex++;
			}
		}
	}

	private void setMatTex(ExtendedMaterialListKey key, Material mat, int i, String matKey) {
		// Do we have a texture already?
		if (textures[i] != null && textures[i].getImage() != null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine(String.format("Setting material texture %s for key %s (%d) on %s", textures[i].getKey(),
						key.toString(), i, matKey));
			mat.setTexture(matKey, textures[i]);
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine(
						String.format("Deferring setting material texture %s (%d) on %s. ", key.toString(), i, matKey));
		}

		// Store the alias in the map for the mesh to populate the rest
		// of the actual textures
		key.addAlias((ExtendedMaterialKey) mat.getKey(), textureAlias[i], matKey);
	}

	protected void fillDefaultAliases(List<String> defaultAliases) {
		// Give any texture units that don't have an alias one.
		int defaultDiffuseIdx = 2;
		for (int i = 0; i < textureAlias.length; i++) {
			if (textureAlias[i] == null && textures[i] != null) {
				if (defaultAliases.isEmpty()) {
					textureAlias[i] = "Diffuse" + defaultDiffuseIdx++;
				} else {
					textureAlias[i] = defaultAliases.remove(0);
				}
			}
		}
	}

	private List<String> getDefaultAliases(String... aliases) {
		// Maintain a list of default alias names, removing any that are
		// explicitly defined
		List<String> defaultAliases = new ArrayList<>(Arrays.asList(aliases));

		for (int i = 0; i < textureAlias.length; i++) {
			if (textureAlias[i] != null) {
				defaultAliases.remove(textureAlias[i]);
			}
		}
		return defaultAliases;
	}

	private MaterialList load(AssetManager assetManager, AssetKey key, InputStream in) throws IOException {
		folderName = key.getFolder();

		noLight = false;
		this.assetManager = assetManager;

		MaterialList list = null;
		List<Statement> statements = BlockLanguageParser.parse(in);

		for (Statement statement : statements) {
			if (statement.getLine().startsWith("import")) {
				MaterialExtensionSet matExts = null;
				if (key instanceof OgreMaterialKey) {
					matExts = ((OgreMaterialKey) key).getMaterialExtensionSet();
				}

				if (matExts == null) {
					throw new IOException("Must specify MaterialExtensionSet when loading\n"
							+ "Ogre3D materials with extended materials");
				}

				list = new MaterialExtensionLoader().load(assetManager, key, matExts, statements);
				break;
			} else if (statement.getLine().startsWith("material")) {
				if (list == null) {
					list = new MaterialList();
				}
				String[] split = statement.getLine().split(" ", 2);
				matName = split[1].trim();
				flipTextureY = ((ExtendedMaterialListKey) key).isFlipTextureY();
				;
				readMaterial(statement, key);
				Material mat = compileMaterial((ExtendedMaterialListKey) key, matName);
				list.put(matName, mat);
			}
		}

		return list;
	}

	public Object load(AssetInfo info) throws IOException {
		InputStream in = null;
		try {
			in = info.openStream();
			return load(info.getManager(), info.getKey(), in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

}
