package org.icetests;

import java.util.logging.Logger;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.FancyWindow;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.extras.SplitPanel;
import icetone.controls.lists.Table;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.controls.windows.TabControl;
import icetone.core.Element;
import icetone.core.layout.FillLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

public class TestSplit extends IcesceneApp {

	static {
		AppInfo.context = TestSplit.class;
	}

	private static final Logger LOG = Logger.getLogger(TestSplit.class.getName());

	public static void main(String[] args) {
		TestSplit app = new TestSplit();
		app.start();
	}

	public TestSplit() {
		setUseUI(true);
		// setStylePath("icetone/gui/style/def/style_map.gui.xml");

	}

	@Override
	public void onSimpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Upper tabs
		TabControl upperTabs = new TabControl(screen) {
			@Override
			public void onTabSelect(int index) {
			}
		};
		upperTabs.addTab("Tab 1");
		Label tabContent1 = new Label(screen, new Vector2f(150, 24));
		tabContent1.setText("Tab content 1");
		Label tabContent2 = new Label(screen, new Vector2f(150, 24));
		tabContent2.setText("Tab content 2");
		upperTabs.addTabChild(0, tabContent1);
		upperTabs.addTab("Tab 2");
		upperTabs.addTabChild(1, tabContent2);

		// Vertical split
		SplitPanel verticalSplit = new SplitPanel(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null,
				Element.Orientation.VERTICAL);
		verticalSplit.setTextPadding(0);
		verticalSplit.setMinDimensions(new Vector2f(50, 50));
		verticalSplit.setLeftOrTop(upperTabs);

		// Lower table
		Table lowerTable = new Table(screen) {
			@Override
			public void onChange() {
			}
		};
		lowerTable.addColumn("Col1");
		lowerTable.addColumn("Col2");
		lowerTable.setColumnResizeMode(Table.ColumnResizeMode.AUTO_FIRST);
		verticalSplit.setRightOrBottom(lowerTable);

		// Left panel
		Element leftPanel = new Element(screen, Vector4f.ZERO, "Interface/bgx.jpg");
		leftPanel.setIsResizable(false);
		leftPanel.setIsMovable(false);

		// Horizontal split
		SplitPanel horizontalSplit = new SplitPanel(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, "Interface/bgw.jpg",
				Element.Orientation.HORIZONTAL);
		horizontalSplit.setTextPadding(0);
		horizontalSplit.setDefaultDividerLocationRatio(0.35f);
		horizontalSplit.setLeftOrTop(leftPanel);
		horizontalSplit.setUseOneTouchExpanders(true);
		horizontalSplit.setRightOrBottom(verticalSplit);

		
		FancyWindow w = new FancyWindow();
		w.setIsResizable(true);
		w.getContentArea().setLayoutManager(new FillLayout());
		w.getContentArea().addChild(horizontalSplit);
		w.sizeToContent();
		screen.addElement(w);

	}
}
