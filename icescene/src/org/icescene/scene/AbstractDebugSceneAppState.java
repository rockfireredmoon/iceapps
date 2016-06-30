package org.icescene.scene;

import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

import org.icelib.Color;
import org.icelib.Icelib;
import org.icescene.IcemoonAppState;
import org.icescene.SceneConfig;
import org.iceui.IceUI;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

public class AbstractDebugSceneAppState extends IcemoonAppState<IcemoonAppState> {

	private final static Logger LOG = Logger.getLogger(AbstractDebugSceneAppState.class.getName());
	protected final Node parentNode;
	protected Geometry gridGeom;
	protected Node arrowNode;

	public AbstractDebugSceneAppState(Preferences prefs, Node parentNode) {
		super(prefs);
		this.parentNode = parentNode;
		addPrefKeyPattern(SceneConfig.DEBUG + ".*");
	}

	@Override
	protected void handlePrefUpdateSceneThread(PreferenceChangeEvent evt) {
		super.handlePrefUpdateSceneThread(evt);
		if (evt.getKey().equals(SceneConfig.DEBUG_GRID)) {
			checkGrid();
		} else if (evt.getKey().equals(SceneConfig.DEBUG_VIEWPORT_COLOUR)) {
			checkViewportColour();
		}
	}

	@Override
	protected void postInitialize() {
		super.postInitialize();
		checkGrid();
		checkViewportColour();
	}

	protected Node attachCoordinateAxes(Vector3f pos) {

		Node node = new Node();
		Arrow arrow = new Arrow(Vector3f.UNIT_X);
		arrow.setLineWidth(4);
		putShape(node, arrow, ColorRGBA.Red);

		arrow = new Arrow(Vector3f.UNIT_Y);
		arrow.setLineWidth(4);
		putShape(node, arrow, ColorRGBA.Green);

		arrow = new Arrow(Vector3f.UNIT_Z);
		arrow.setLineWidth(4);
		putShape(node, arrow, ColorRGBA.Blue);

		node.setLocalTranslation(pos);

		return node;
	}

	protected Geometry putShape(Node node, Mesh shape, ColorRGBA color) {
		Geometry g = new Geometry("coordinate axis", shape);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.setColor("Color", color);
		g.setMaterial(mat);
		node.attachChild(g);
		return g;
	}

	protected void checkGrid() {
		boolean showGrid = prefs.getBoolean(SceneConfig.DEBUG_GRID, SceneConfig.DEBUG_GRID_DEFAULT);
		if (showGrid && gridGeom == null) {
			int g = 1920;
			int w = 4;
			Grid grid = new Grid(g, g, w);
			gridGeom = new Geometry("Grid", grid);
			Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Blue);
			gridGeom.setLocalTranslation((-g * w ) / 2, 0, ( -g * w ) / 2);
			gridGeom.setMaterial(mat);
			parentNode.attachChild(gridGeom);
			arrowNode = attachCoordinateAxes(new Vector3f(0, 0, 0));
			arrowNode.scale(4);
			parentNode.attachChild(arrowNode);
		} else if (!showGrid && gridGeom != null) {
			removeGrid();
		}
	}

	protected void removeGrid() {
		gridGeom.removeFromParent();
		gridGeom = null;
		arrowNode.removeFromParent();
	}

	private void checkViewportColour() {
		app.getViewPort().setBackgroundColor(IceUI.toRGBA(new Color(prefs.get(SceneConfig.DEBUG_VIEWPORT_COLOUR,
				Icelib.toHexString(IceUI.fromRGBA(SceneConfig.DEBUG_VIEWPORT_COLOUR_DEFAULT))))));
	}
}
