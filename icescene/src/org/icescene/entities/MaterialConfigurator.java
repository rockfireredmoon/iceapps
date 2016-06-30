package org.icescene.entities;

import java.util.ArrayList;

import org.icescene.NodeVisitor;
import org.icescene.NodeVisitor.Visit;

import com.jme3.material.MatParam;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture2D;

public abstract class MaterialConfigurator implements Visit {

	public MaterialConfigurator(Spatial root) {
		if (root instanceof Node) {
			new NodeVisitor((Node) root).visit(this);
		} else {
			visit(root);
		}
	}

	@Override
	public void visit(Spatial node) {

		if (node instanceof Geometry) {
			Geometry geom = (Geometry) node;
			configureGeometry(geom);

			/*
			 * Remove any texture parameters that have a null image (these
			 * may have been prepared by the material loader, but not used
			 * by the mesh or anything that hooks into DynamicMaterialList.
			 * If we let them go through to be rendered, there will be
			 * errors.
			 */
			for (MatParam p : new ArrayList<MatParam>(geom.getMaterial().getParams())) {
				if (p.getVarType().equals(VarType.Texture2D) && p.getValue() != null
						&& ((Texture2D) p.getValue()).getImage() == null) {
					geom.getMaterial().clearParam(p.getName());
				}
			}
		}

	}

	protected abstract void configureGeometry(Geometry geom);

}
