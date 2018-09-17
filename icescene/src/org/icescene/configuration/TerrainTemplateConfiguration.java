package org.icescene.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.icelib.Icelib;
import org.icelib.PageLocation;
import org.icescene.SceneConstants;
import org.icescripting.ScriptEvalException;
import org.icescripting.Scripts;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.Vector2f;

/**
 * Loads and stores a Terrain template configuration file. Each template will
 * have one of these for the defaults, or any individual tile can have it's own
 * overrides.
 */
public class TerrainTemplateConfiguration extends AbstractPropertiesConfiguration<TerrainTemplateConfiguration> {
	final static Logger LOG = Logger.getLogger(TerrainTemplateConfiguration.class.getName());

	public final static String PAGE_SOURCE_HEIGHTMAP = "Heightmap";

	public enum LiquidPlane {
		NONE, WATER_PLANE, LAVA, TROPICAL, SHARD, SWAMP_WATER, TAR;

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}

		public String toWaterPlaneName() {
			return toString().replace(" ", "");
		}

		public String toMaterialName() {
			return toWaterPlaneName().replace("Plane", "");
		}

		public static LiquidPlane fromWaterPlaneName(String liquidPlaneName) {
			for (LiquidPlane p : values()) {
				if (p.toString().replace(" ", "").equals(liquidPlaneName))
					return p;
			}
			throw new IllegalArgumentException(String.format("Invalid liquid plane name %s.", liquidPlaneName));
		}
	}

	public static class LiquidPlaneConfiguration {

		private float elevation;
		private LiquidPlane material;
		private float defaultElevation;
		private LiquidPlane defaultMaterial;

		private LiquidPlaneConfiguration() {
		}

		public LiquidPlaneConfiguration(float elevation, LiquidPlane material) {
			this.elevation = this.defaultElevation = elevation;
			this.material = this.defaultMaterial = material;
		}

		public LiquidPlane getMaterial() {
			return material;
		}

		public float getElevation() {
			return elevation;
		}

		public void setElevation(float elevation) {
			this.elevation = elevation;
		}

		public void reset() {
			this.elevation = defaultElevation;
			this.material = defaultMaterial;
		}

		public void setMaterial(LiquidPlane material) {
			this.material = material;
		}

		@Override
		public LiquidPlaneConfiguration clone() {
			LiquidPlaneConfiguration cfg = new LiquidPlaneConfiguration();
			cfg.material = material;
			cfg.elevation = elevation;
			cfg.defaultElevation = defaultElevation;
			cfg.defaultMaterial = defaultMaterial;
			return cfg;
		}
	}

	private String customMaterialName;
	private String textureBaseFormat;
	private String textureCoverageFormat;
	private String textureSplatting0;
	private String textureSplatting1;
	private String textureSplatting2;
	private String textureSplatting3;
	private String perPageConfig;
	private String detailTexture;
	private int detailTile;
	private int asyncLoadRate;
	private int livePageMargin;
	private String pageSource;
	private int pageMaxX;
	private int pageMaxZ;
	private int heightmapRawSize = -1;
	private int heightmapRawBpp = -1;
	private int pageSize;
	private int tileSize;
	private int maxPixelError;
	private int pageWorldX;
	private int pageWorldZ;
	private int maxHeight;
	private int maxMipMapLevel;
	private boolean vertexNormals;
	private boolean vertexColors;
	private boolean useTriStrips;
	private boolean vertexProgramMorph;
	private float lodMorphStart;
	private String morphLODFactorParamName;
	private int morphLODFactorParamIndex = -1;
	private String heightmapImageFormat;
	// Settable
	private LiquidPlaneConfiguration liquidPlane;
	private String environment = "Default";

	private PageLocation page;
	private final static Map<String, TerrainTemplateConfiguration> cache = new HashMap<String, TerrainTemplateConfiguration>();

	public static TerrainTemplateConfiguration get(AssetManager assetManager, String resourceName) {
		TerrainTemplateConfiguration cfg = cache.get(resourceName);
		if (cfg == null) {
			cfg = new TerrainTemplateConfiguration(assetManager, resourceName);
			cache.put(resourceName, cfg);
			cfg.load();
		}
		return cfg;
	}

	public static void remove(TerrainTemplateConfiguration terrainTemplate) {
		cache.remove(terrainTemplate.getAssetPath());
	}

	public static TerrainTemplateConfiguration get(AssetManager assetManager, String resourceName,
			TerrainTemplateConfiguration base) {
		TerrainTemplateConfiguration cfg = cache.get(resourceName);
		if (cfg == null) {
			cfg = new TerrainTemplateConfiguration(assetManager, resourceName, base);
			cfg.load();
			cache.put(resourceName, cfg);
		}
		return cfg;
	}

	private TerrainTemplateConfiguration(AssetManager assetManager, String resourceName) {
		this(assetManager, resourceName, (TerrainTemplateConfiguration) null);
	}

	public TerrainTemplateConfiguration(AssetManager assetManager, String resourceName,
			TerrainTemplateConfiguration base) {
		super(assetManager, resourceName, base);

		// Set up either a new property sheet or copy the defaults
		if (base != null) {
			if (base.perPageConfig == null) {
				throw new IllegalArgumentException("Base template configuration must have a PerPageConfig set");
			}
			String baseName = Icelib.getBasename(Icelib.getBaseFilename(getAssetPath()));
			String[] coords = baseName.substring(baseName.lastIndexOf('_') + 2).split("y");
			page = new PageLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

		}

		// Look for environment 'script' for this tile/terrain
		String envConfig = Icelib.getBasename(resourceName) + ".nut";
		try {
			Bindings bindings = Scripts.get().createBindings();
			bindings.put("TerrainTemplate", this);
			Scripts.get().eval(envConfig, bindings);
		} catch (ScriptEvalException se) {
			LOG.log(Level.WARNING, "Failed to load terrain script.", se);
		} catch (AssetNotFoundException anfe) {
			LOG.log(Level.FINE, "Failed to load terrain script.", anfe);
		} catch (IllegalStateException ioe) {
			throw new RuntimeException(
					String.format("I/O error reading environment configuration script %s.", envConfig), ioe);
		}
	}

	public TerrainTemplateConfiguration getBase() {
		return base;
	}

	@Override
	public TerrainTemplateConfiguration clone() {
		TerrainTemplateConfiguration t = new TerrainTemplateConfiguration(assetManager, assetPath);

		t.customMaterialName = customMaterialName;
		t.textureBaseFormat = textureBaseFormat;
		t.textureCoverageFormat = textureCoverageFormat;
		t.textureSplatting0 = textureSplatting0;
		t.textureSplatting1 = textureSplatting1;
		t.textureSplatting2 = textureSplatting2;
		t.textureSplatting3 = textureSplatting3;
		t.perPageConfig = perPageConfig;
		t.detailTexture = detailTexture;
		t.detailTile = detailTile;
		t.asyncLoadRate = asyncLoadRate;
		t.livePageMargin = livePageMargin;
		t.pageSource = pageSource;
		t.pageMaxX = pageMaxX;
		t.pageMaxZ = pageMaxZ;
		t.heightmapRawSize = heightmapRawSize;
		t.heightmapRawBpp = heightmapRawBpp;
		t.pageSize = pageSize;
		t.tileSize = tileSize;
		t.maxPixelError = maxPixelError;
		t.pageWorldX = pageWorldX;
		t.pageWorldZ = pageWorldZ;
		t.maxHeight = maxHeight;
		t.maxMipMapLevel = maxMipMapLevel;
		t.vertexNormals = vertexNormals;
		t.vertexColors = vertexColors;
		t.useTriStrips = useTriStrips;
		t.vertexProgramMorph = vertexProgramMorph;
		t.lodMorphStart = lodMorphStart;
		t.morphLODFactorParamName = morphLODFactorParamName;
		t.morphLODFactorParamIndex = morphLODFactorParamIndex;
		t.heightmapImageFormat = heightmapImageFormat;
		t.liquidPlane = liquidPlane == null ? null : liquidPlane.clone();
		t.environment = environment;
		return t;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getTerrainTemplateGroup() {
		String d = FilenameUtils.getName(FilenameUtils.getPath(assetPath));
		return d.substring(d.indexOf('-') + 1);
	}

	public String getTerrainTemplateName() {
		return String.format("Terrain-%1$s#Terrain-%2$s.cfg", getTerrainTemplateGroup(), getBaseTemplateName());
	}

	public String getBaseTemplateName() {
		String base = FilenameUtils.getName(assetPath);
		return FilenameUtils.getBaseName(base.substring(base.indexOf('-') + 1));
	}

	public final void load() {
		customMaterialName = get("CustomMaterialName");
		textureBaseFormat = get("Texture.Base");
		textureCoverageFormat = get("Texture.Coverage");
		textureSplatting0 = get("Texture.Splatting0");
		textureSplatting1 = get("Texture.Splatting1");
		textureSplatting2 = get("Texture.Splatting2");
		textureSplatting3 = get("Texture.Splatting3");
		perPageConfig = get("PerPageConfig");
		detailTexture = get("DetailTexture");
		asyncLoadRate = getInt("AsyncLoadRate");
		livePageMargin = getInt("LivePageMargin");
		detailTile = getInt("DetailTile");
		pageSource = get("PageSource");
		heightmapImageFormat = get("Heightmap.image");
		pageMaxX = getInt("PageMaxX");
		pageMaxZ = getInt("PageMaxZ");
		heightmapRawSize = getInt("Heightmap.raw.size", -1);
		heightmapRawBpp = getInt("Heightmap.raw.bpp", -1);
		pageSize = getInt("PageSize");
		tileSize = getInt("TileSize");
		maxPixelError = getInt("MaxPixelError");
		pageWorldX = getInt("PageWorldX");
		pageWorldZ = getInt("PageWorldZ");
		maxHeight = getInt("MaxHeight");
		maxMipMapLevel = getInt("MaxMipMapLevel");
		vertexNormals = getBoolean("VertexNormals");
		vertexColors = getBoolean("VertexColors");
		useTriStrips = getBoolean("UseTriStrips");
		vertexProgramMorph = getBoolean("VertexProgramMorph");
		lodMorphStart = getFloat("LODMorphStart");
		morphLODFactorParamName = get("MorphLODFactorParamName");
		morphLODFactorParamIndex = getInt("MorphLODFactorParamIndex", -1);
		liquidPlane = null;
		if (getBackingObject().containsKey("WaterPlane.Material")) {
			String material = get("WaterPlane.Material");
			if (StringUtils.isNotBlank(material)) {
				liquidPlane = new LiquidPlaneConfiguration(getFloat("WaterPlane.Elevation"),
						LiquidPlane.fromWaterPlaneName(material));
			}
		}
	}

	public void setBaseTemplateName(String newBaseTemplateName) {
		// TODO move TerrainCOnstants.TEXTURE_PATH
		assetPath = String.format("%1$s/Terrain-%2$s/Terrain-%2$s.cfg", SceneConstants.TERRAIN_PATH,
				newBaseTemplateName);
		textureBaseFormat = newBaseTemplateName + "_Base_x%dy%d.jpg";
		textureCoverageFormat = newBaseTemplateName + "_Coverage_x%dy%d.png";
		heightmapImageFormat = newBaseTemplateName + "_Height_x%dy%d.png";
		perPageConfig = newBaseTemplateName + "_x%dy%d.cfg";
	}

	public void setLiquidPlaneConfiguration(LiquidPlaneConfiguration liquidPlane) {
		this.liquidPlane = liquidPlane;
	}

	public boolean isIn(PageLocation location) {
		return location.x >= 0 && location.y >= 0 && location.x < getPageMaxX() && location.y < getPageMaxZ();
	}

	/**
	 * Get how much to scale the coverage to make it fit one page.
	 *
	 * NOTE, this may need expanding if it turns out PF terrain can have
	 * different size of coverage images
	 *
	 * @return
	 */
	public float getCoverageScale() {
		return getPageWorldX() / 256;
	}

	/**
	 * Get how much to scale the terrain to make it fit one page.
	 *
	 * NOTE, this may need expanding if it turns out PF terrain can be odd sizes
	 * (I don't think it is)
	 *
	 * @return
	 */
	public float getPageScale() {
		return getPageWorldX() / (getPageSize() - 1);
	}

	/**
	 * Conver X and Z world coordinates to X and Y tile co-ordinates.
	 *
	 * @param worldCoords
	 *            world
	 * @return X and Y tile co-ordinates
	 */
	public PageLocation getTile(Vector2f worldCoords) {
		return new PageLocation(
				worldCoords.x < 0 ? (int) ((Math.abs(worldCoords.x) / (float) pageWorldX) + 1) * -1
						: (int) (worldCoords.x / (float) pageWorldX),
				worldCoords.y < 0 ? (int) ((Math.abs(worldCoords.y) / (float) pageWorldZ) + 1) * -1
						: (int) (worldCoords.y / (float) pageWorldZ));
	}

	public Vector2f getPositionWithinTile(PageLocation tile, Vector2f worldCoords) {
		return new Vector2f(worldCoords.x - (tile.x * pageWorldX), worldCoords.y - (tile.y * pageWorldZ));
	}

	public LiquidPlaneConfiguration getLiquidPlaneConfiguration() {
		return liquidPlane;
	}

	public void setWaterPlane(LiquidPlaneConfiguration waterPlane) {
		this.liquidPlane = waterPlane;
	}

	public String getCustomMaterialName() {
		return customMaterialName;
	}

	public String getTextureBaseFormat() {
		return textureBaseFormat;
	}

	public String getTextureTextureFormat() {
		return textureBaseFormat.replace("_Base_", "_Texture_");
	}

	public String getTextureCoverageFormat() {
		return textureCoverageFormat;
	}

	public String getTextureSplatting0() {
		return textureSplatting0;
	}

	public String getTextureSplatting1() {
		return textureSplatting1;
	}

	public String getTextureSplatting2() {
		return textureSplatting2;
	}

	public String getTextureSplatting3() {
		return textureSplatting3;
	}

	public void setTextureSplatting0(String textureSplatting0) {
		this.textureSplatting0 = textureSplatting0;
	}

	public void setTextureSplatting1(String textureSplatting1) {
		this.textureSplatting1 = textureSplatting1;
	}

	public void setTextureSplatting2(String textureSplatting2) {
		this.textureSplatting2 = textureSplatting2;
	}

	public void setTextureSplatting3(String textureSplatting3) {
		this.textureSplatting3 = textureSplatting3;
	}

	public String getPerPageConfig() {
		return perPageConfig;
	}

	public String getDetailTexture() {
		return detailTexture;
	}

	public int getAsyncLoadRate() {
		return asyncLoadRate;
	}

	public int getLivePageMargin() {
		return livePageMargin;
	}

	public int getDetailTile() {
		return detailTile;
	}

	public String getPageSource() {
		return pageSource;
	}

	public int getPageMaxX() {
		return pageMaxX;
	}

	public int getPageMaxZ() {
		return pageMaxZ;
	}

	public int getHeightmapRawSize() {
		return heightmapRawSize;
	}

	public int getHeightmapRawBpp() {
		return heightmapRawBpp;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTileSize() {
		return tileSize;
	}

	public int getMaxPixelError() {
		return maxPixelError;
	}

	public int getPageWorldX() {
		return pageWorldX;
	}

	public int getPageWorldZ() {
		return pageWorldZ;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public int getMaxMipMapLevel() {
		return maxMipMapLevel;
	}

	public boolean isVertexNormals() {
		return vertexNormals;
	}

	public boolean isVertexColors() {
		return vertexColors;
	}

	public boolean isUseTriStrips() {
		return useTriStrips;
	}

	public boolean isVertexProgramMorph() {
		return vertexProgramMorph;
	}

	public float getLodMorphStart() {
		return lodMorphStart;
	}

	public String getMorphLODFactorParamName() {
		return morphLODFactorParamName;
	}

	public int getMorphLODFactorParamIndex() {
		return morphLODFactorParamIndex;
	}

	public String getHeightmapImageFormat() {
		return heightmapImageFormat;
	}

	@Override
	protected void fill(boolean partial) {
		put("Texture.Splatting0", textureSplatting0);
		put("Texture.Splatting1", textureSplatting1);
		put("Texture.Splatting2", textureSplatting2);
		put("Texture.Splatting3", textureSplatting3);

		if (base == null) {

			// Only for default
			put("CustomMaterialName", customMaterialName);
			put("Texture.Base", textureBaseFormat);
			put("Texture.Coverage", textureCoverageFormat);
			put("PerPageConfig", perPageConfig);
			put("DetailTexture", detailTexture);
			put("AsyncLoadRate", asyncLoadRate);
			put("LivePageMargin", livePageMargin);
			put("DetailTile", detailTile);
			put("PageSource", pageSource);
			put("Heightmap.image", heightmapImageFormat);
			put("PageMaxX", pageMaxX);
			put("PageMaxZ", pageMaxZ);
			if (heightmapRawSize > -1) {
				put("Heightmap.raw.size", heightmapRawSize);
			} else
				getBackingObject().remove("Heightmap.raw.size");
			if (heightmapRawBpp > -1) {
				put("Heightmap.raw.bpp", heightmapRawBpp);
			} else
				getBackingObject().remove("Heightmap.raw.bpp");
			put("PageSize", pageSize);
			put("TileSize", tileSize);
			put("MaxPixelError", maxPixelError);
			put("PageWorldX", pageWorldX);
			put("PageWorldZ", pageWorldZ);
			put("MaxHeight", maxHeight);
			put("MaxMipMapLevel", maxMipMapLevel);
			put("VertexNormals", vertexNormals ? "yes" : "no");
			put("VertexColors", vertexColors ? "yes" : "no");
			put("UseTriStrips", useTriStrips ? "yes" : "no");
			put("VertexProgramMorph", vertexProgramMorph ? "yes" : "no");
			put("LODMorphStart", lodMorphStart);
			put("MorphLODFactorParamName", morphLODFactorParamName);
			if (morphLODFactorParamIndex > -1)
				put("MorphLODFactorParamIndex", morphLODFactorParamIndex);
			else
				getBackingObject().remove("MorphLODFactorParamIndex");
		}

		if (base != null) {
			// Only for per page config
			if (liquidPlane != null) {
				put("WaterPlane.Material", liquidPlane.getMaterial().toString());
				put("WaterPlane.Elevation", liquidPlane.getElevation());
			} else {
				getBackingObject().remove("WaterPlane.Material");
				getBackingObject().remove("WaterPlane.Elevation");
			}
		}
	}

	public static String toAssetPath(String terrain) {
		return String.format("%s/Terrain-%1$s/Terrain-%1$s.cfg", SceneConstants.TERRAIN_PATH, terrain);
	}

	public PageLocation getPage() {
		return page;
	}

}
