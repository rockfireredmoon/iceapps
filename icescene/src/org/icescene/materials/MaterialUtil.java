package org.icescene.materials;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.shader.VarType;

public class MaterialUtil {

	public static void setColor(Geometry geom, ColorRGBA col) {
		setColor(geom.getMaterial(), col);
	}

	public static void setColor(Material mat, ColorRGBA col) {
		if (mat.getMaterialDef().getMaterialParam("Color") == null) {
			mat.setParam("UseMaterialColors", VarType.Boolean, true);
			mat.setParam("Diffuse", VarType.Vector4, col);
		} else
			mat.setParam("Color", VarType.Vector4, col);
	}
}
