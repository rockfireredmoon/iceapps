package org.icetests;

import java.util.ArrayList;
import java.util.List;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.AutocompleteTextField;
import org.iceui.controls.AutocompleteTextField.AutocompleteItem;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestAutocomplete extends IcesceneApp {

	static {
		AppInfo.context = TestAutocomplete.class;
	}

	public static void main(String[] args) {
		TestAutocomplete app = new TestAutocomplete();
		app.start();
	}

	public TestAutocomplete() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseCustomCursors(true);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		AutocompleteTextField<String> tabs = new AutocompleteTextField<String>(screen,
				new AutocompleteTextField.AutocompleteSource<String>() {
					public List<AutocompleteItem<String>> getItems(String text) {
						text = text.toLowerCase();
						List<AutocompleteItem<String>> l = new ArrayList<>();
						for (String n : new String[] { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7",
								"Item 8" }) {
							l.add(new AutocompleteItem<String>(n, n));
						}
						return l;
					}
				});

		// Panel for actions and character selection
		Panel xcw = new Panel();
		xcw.setLayoutManager(new MigLayout("wrap 1"));
		xcw.addChild(tabs);

		// xcw.pack(false);
		// Add window to screen (causes a layout)
		screen.addElement(xcw);
		xcw.show();

	}

}
