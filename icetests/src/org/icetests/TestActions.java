package org.icetests;

import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.controls.Rotator;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.tools.AbstractToolArea;
import org.icescene.tools.ActionBarsAppState;
import org.icescene.tools.ActionData;
import org.icescene.tools.DragContext;
import org.icescene.tools.HudType;
import org.icescene.tools.Tool;
import org.icescene.tools.ToolBox;
import org.icescene.tools.ToolCategory;
import org.icescene.tools.ToolManager;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.core.ElementManager;

public class TestActions extends IcesceneApp {
	
	static {
		AppInfo.context = TestActions.class;
	}

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestActions.class, "Icetest");
	}

	public TestActions(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
		// setStylePath("icetone/style/def/style_map.gui.xml");
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseCustomCursors(true);
		screen.setUseUIAudio(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);
		flyCam.setEnabled(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
		geom.addControl(new Rotator());

		ToolManager toolManager = new ToolManager();
		TestHudType testHud = new TestHudType();

		// Build - Dialogs
		final ToolCategory testCategory = new ToolCategory(testHud, "Tests", "Test tools", 1);
		testCategory.setShowInToolBox(false);
		toolManager.addCategory(testCategory);
		testCategory.addTool(new TestTool1());

		toolManager.addToolBox(testHud, new ToolBox("Tests", "Tests", 1, 8).setDefaultVerticalPosition(BitmapFont.VAlign.Top)
				.setDefaultHorizontalPosition(BitmapFont.Align.Left).setStyle(ToolBox.Style.Tools).setDefaultVisible(true)
				.setModifiers(ModifierKeysAppState.CTRL_MASK).setConfigurable(false));

		toolManager.addToolBox(testHud, new ToolBox("Tests2", "Tests2", 1, 8).setDefaultVerticalPosition(BitmapFont.VAlign.Top)
				.setDefaultHorizontalPosition(BitmapFont.Align.Right).setStyle(ToolBox.Style.Tools).setDefaultVisible(true)
				.setModifiers(ModifierKeysAppState.ALT_MASK).setConfigurable(false));

		DragContext dragContext = new DragContext();

		ActionBarsAppState actions = new ActionBarsAppState(prefs, testHud, dragContext, toolManager);
		stateManager.attach(actions);

	}

	class TestTool1 extends Tool {

		public TestTool1() {
			super("BuildIcons/Icon-32-Build-BuildMode.png", "Test 1", "Test Tool 1", 1);
			setDefaultToolBox("Tests");
			setTrashable(false);
			setMayDrag(true);
		}

		@Override
		public void actionPerformed(ActionData data) {
		}
	}

	class TestHudType implements HudType {

		@Override
		public Preferences preferenceNode() {
			return SceneConfig.get();
		}

		@Override
		public String name() {
			return "test";
		}

		@Override
		public AbstractToolArea createToolArea(ToolManager toolManager, ElementManager screen) {
			return null;
		}

	}
}
