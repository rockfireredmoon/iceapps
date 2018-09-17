package org.icetests;

import java.util.prefs.Preferences;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.tools.AbstractToolArea;
import org.icescene.tools.ActionData;
import org.icescene.tools.DragContext;
import org.icescene.tools.HudType;
import org.icescene.tools.Tool;
import org.icescene.tools.ToolBox;
import org.icescene.tools.ToolBoxLayer;
import org.icescene.tools.ToolCategory;
import org.icescene.tools.ToolManager;
import org.icescene.tools.ToolPanel;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.core.BaseScreen;
import icetone.core.ZPriority;
import icetone.core.layout.mig.MigLayout;

public class TestTools extends IcesceneApp {

	static {
		AppInfo.context = TestTools.class;
	}

	public static void main(String[] args) {
		TestTools app = new TestTools();
		app.start();
	}

	public TestTools() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		// try {
		// Preferences parent = prefs.parent();
		// prefs.removeNode();
		// prefs.flush();
		// prefs = parent.node(prefs.name());
		// } catch (BackingStoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// A fake hud type
		HudType hudType = new HudType() {

			@Override
			public Preferences preferenceNode() {
				return SceneConfig.get();
			}

			@Override
			public String name() {
				return "TEST";
			}

			@Override
			public AbstractToolArea createToolArea(ToolManager toolManager, BaseScreen screen) {
				return null;
			}
		};

		DragContext dragContext = new DragContext();

		ToolManager toolManager = new ToolManager();

		// Build - Dialogs
		final ToolCategory buildDialogsCategory = new ToolCategory(hudType, "Test", "Test Tools", 1);
		toolManager.addCategory(buildDialogsCategory);
		buildDialogsCategory.addTool(new TestTool(1, "BuildIcons/Icon-32-Build-BuildMode.png"));
		buildDialogsCategory.addTool(new TestTool(2, "BuildIcons/Icon-32-Build-AddProp.png"));
		buildDialogsCategory.addTool(new TestTool(3, "BuildIcons/Icon-32-Build-AudioNode.png"));
		buildDialogsCategory.addTool(new TestTool(4, "BuildIcons/Icon-32-Build-CreatureTweak.png"));
		buildDialogsCategory.addTool(new TestTool(5, "BuildIcons/Icon-32-Build-Clutter.png"));

		ToolBox toolbox = new ToolBox("TestQuickbar1", "Quickbar (Ctrl hotkey)", 2, 8)
				.setDefaultVerticalPosition(BitmapFont.VAlign.Top);
		toolManager.addToolBox(hudType, toolbox.setDefaultHorizontalPosition(BitmapFont.Align.Left)
				.setDefaultVisible(true).setModifiers(ModifierKeysAppState.CTRL_MASK));

		ToolBoxLayer tbl = new ToolBoxLayer(screen, prefs, hudType, toolManager, dragContext);
		tbl.init();
		// getLayers().addChild(tbl);
		getLayers(ZPriority.NORMAL).addElement(tbl);

		Frame w = new Frame();
		w.setTitle("A Window");
		w.setResizable(true);
		w.setPosition(200, 200);
		w.getContentArea().setLayoutManager(new MigLayout(screen));
		w.getContentArea().addElement(new PushButton("Toolbar").onMouseReleased(evt -> {
			ToolPanel vis = tbl.getTools(toolbox);
			if (vis.isVisible()) {
				vis.hide();
			} else {
				vis.show();
			}
		}));
		w.sizeToContent();
		screen.addElement(w);
	}

	class TestTool extends Tool {

		public TestTool(int idx, String icon) {
			super(icon, "Test Tool " + idx, "Test Tool " + idx, 1);
			setDefaultToolBox("TestQuickbar1");
			setTrashable(true);
			setMayDrag(true);
		}

		@Override
		public void actionPerformed(ActionData data) {
		}
	}

}
