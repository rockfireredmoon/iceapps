package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.FancyWindow;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.lists.SelectList;
import icetone.controls.lists.SelectList.SelectionMode;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;

public class TestSelectList extends IcesceneApp {

	static {
		AppInfo.context = TestSelectList.class;
	}

	public static void main(String[] args) {
		TestSelectList app = new TestSelectList();
		app.start();
	}

	public TestSelectList() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Panel for actions and character selection
		FancyWindow panel = new FancyWindow(FancyWindow.Size.SMALL);
		Element contentArea = panel.getContentArea();
		contentArea.setLayoutManager(new MigLayout("wrap 4", "[][][][]", "[][][][]"));
		panel.setIsResizable(true);

		contentArea.addChild(new Label("Single"));
		contentArea.addChild(new Label("Multiple"));
		contentArea.addChild(new Label("Toggle"));
		contentArea.addChild(new Label("None"));
		

		SelectList<Integer> scr = new SelectList<Integer>();
		scr.setSelectionMode(SelectionMode.SINGLE);
		for (int i = 0; i < 10; i++) {
			scr.addListItem("Item " + i, i);
		}
		contentArea.addChild(scr);

		SelectList<Integer> scr2 = new SelectList<Integer>();
		scr2.setSelectionMode(SelectionMode.MULTIPLE);
		for (int i = 0; i < 10; i++) {
			scr2.addListItem("Item " + i, i);
		}
		contentArea.addChild(scr2);

		SelectList<Integer> scr3 = new SelectList<Integer>();
		scr3.setSelectionMode(SelectionMode.TOGGLE);
		for (int i = 0; i < 10; i++) {
			scr3.addListItem("Item " + i, i);
		}
		contentArea.addChild(scr3);

		SelectList<Integer> scr4 = new SelectList<Integer>();
		scr4.setSelectionMode(SelectionMode.TOGGLE);
		for (int i = 0; i < 10; i++) {
			scr4.addListItem("Item " + i, i);
		}
		contentArea.addChild(scr4);

		
		panel.sizeToContent();

		screen.addElement(panel);
	}

}
