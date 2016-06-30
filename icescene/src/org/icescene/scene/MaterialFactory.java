package org.icescene.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.configuration.MeshConfiguration;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;

public class MaterialFactory {

    private final static Logger LOG = Logger.getLogger(MaterialFactory.class.getName());
    private static MaterialFactory instance;
    private final static List<Package> packages = new ArrayList<Package>();

    static {
//        packages.add(DefaultLit.class.getPackage());
    }

    public static void addMaterialPackage(Package materialPackage) {
        packages.add(materialPackage);
    }

    public static MaterialFactory getManager() {
        if (instance == null) {
            instance = new MaterialFactory();
        }
        return instance;
    }

    public Material getMaterial(MeshConfiguration meshConfig, AssetManager assetManager) {
        return getMaterial(null, meshConfig, assetManager);
    }

    public Material getMaterial(String materialName, AssetManager assetManager) {
        return getMaterial(materialName, null, assetManager);
    }

    public Material getMaterial(String materialName, MeshConfiguration meshConfig, AssetManager assetManager) {
        // Try jmonkey material definition first
        String actualName = materialName == null ? meshConfig.getMaterial() : materialName;
        String materialPath = String.format("Materials/%s.j3m", actualName);
        try {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(String.format("Loading %s", materialPath));
            }
            final Material material = assetManager.loadMaterial(materialPath);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(String.format("Loaded %s", materialPath));
            }
            return material;
        } catch (AssetNotFoundException anfe) {
            try {
                actualName = actualName.replace("/", "");
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine(String.format("Could not find %s, looking for java class (%s)", materialPath, actualName));
                }
                for (Package p : packages) {
                    try {
                        Class<? extends Material> materialClass = (Class<? extends Material>) Class.forName(p.getName() + "." + actualName);
                        final Material material = materialClass.getConstructor(MeshConfiguration.class, AssetManager.class).newInstance(meshConfig, assetManager);
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine(String.format("Loaded %s", materialClass));
                        }
                        return material;
                    } catch (ClassNotFoundException cnfe) {
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine(String.format("Not found in %s", p.getName()));
                        }
                    }
                }
                throw new AssetNotFoundException("Failed to load material for " + actualName + ".");

            } catch (Exception e) {
                throw new AssetNotFoundException("Failed to load material for " + actualName + ".", e);
            }
        }
    }
}
