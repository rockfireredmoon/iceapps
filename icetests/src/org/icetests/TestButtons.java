package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.SelectableItem;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.RadioButton;
import icetone.controls.buttons.RadioButtonGroup;
import icetone.controls.text.Label;
import icetone.controls.text.Password;
import icetone.controls.text.TextField;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.ElementManager;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;

public class TestButtons extends IcesceneApp {

	static {
		AppInfo.context = TestButtons.class;
	}

	public static void main(String[] args) {
		TestButtons app = new TestButtons();
		app.start();
	}

	private CheckBox c1;
	private CheckBox c2;

	public TestButtons() {
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

		// Radio Buttons
		RadioButtonGroup rgb = new RadioButtonGroup(screen) {
			@Override
			public void onSelect(int index, Button value) {
			}
		};
		Container radios = new Container(screen);
		radios.setLayoutManager(new FlowLayout(0, BitmapFont.VAlign.Top).setFill(true));

		RadioButton r1 = new RadioButton(screen);

		r1.setLabelText("Radio Button 1");
		rgb.addButton(r1);
		radios.addChild(r1);
		RadioButton r2 = new RadioButton(screen);
		r2.setLabelText("Radio Button 2 with a long label");
		rgb.addButton(r2);
		radios.addChild(r2);
		RadioButton r3 = new RadioButton(screen);
		r3.setLabelText("Radio Button 3 with an even longer label");
		rgb.addButton(r3);
		radios.addChild(r3);

		// Ordinary buttons
		Container buttons = new Container(screen);
		buttons.setLayoutManager(new BorderLayout());

		Button buttonNoTextOrIcon = new ButtonAdapter();
		buttons.addChild(buttonNoTextOrIcon, BorderLayout.Border.NORTH);

		Button buttonWithText = new ButtonAdapter() {

			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				super.onButtonMouseLeftDown(evt, toggled);
				c1.setIsCheckedNoCallback(!c1.getIsChecked());
			}

		};
		buttonWithText.setText("South");
		buttons.addChild(buttonWithText, BorderLayout.Border.SOUTH);

		Button buttonWithIcon = new ButtonAdapter();
		buttonWithIcon.setButtonIcon(screen.getStyle("Common").getVector2f("arrowSize").x,
				screen.getStyle("Common").getVector2f("arrowSize").y, screen.getStyle("Common").getString("arrowRight"));
		buttons.addChild(buttonWithIcon, BorderLayout.Border.EAST);

		Button buttonWithIconAndText = new ButtonAdapter("West");
		buttonWithIconAndText.setButtonIcon(screen.getStyle("Common").getVector2f("arrowSize").x,
				screen.getStyle("Common").getVector2f("arrowSize").y, screen.getStyle("Common").getString("arrowUp"));
		buttonWithIconAndText.setText("West");
		buttonWithIconAndText.setButtonIconAlign(BitmapFont.Align.Center);
		buttons.addChild(buttonWithIconAndText, BorderLayout.Border.WEST);

		buttons.addChild(new Label("C"), BorderLayout.Border.CENTER);
		
		// Check Boxes
		Container checks = new Container();
		checks.setLayoutManager(new FlowLayout(4, BitmapFont.VAlign.Top).setFill(true));

		c1 = new CheckBox("Check Box 1");
		checks.addChild(c1);

		c2 = new CheckBox(screen) {

			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				c2.setIsCheckedNoCallback(c2.getIsChecked());

			}
		};
		c2.setLabelText("Check Box 2 with a long label");
		checks.addChild(c2);

		CheckBox c3 = new CheckBox("Check Box 3 with an even longer label");
		checks.addChild(c3);

		// Text Fields
		Container textfields = new Container();
		textfields.setLayoutManager(new FlowLayout(4, BitmapFont.VAlign.Top).setFill(true));

		TextField textfield = new TextField();
		textfields.addChild(textfield);

		Password password = new Password(screen);
		textfields.addChild(password);

		// A label
		Label l1 = new Label("Buttons And Borders");
		l1.setTextAlign(BitmapFont.Align.Center);
		ElementStyle.medium(screen, l1);

		// Panel for actions and character selection
		Panel xcw = new Panel();
		xcw.setLayoutManager(new BorderLayout(8, 8));
		xcw.addChild(l1, BorderLayout.Border.NORTH);
		xcw.addChild(buttons, BorderLayout.Border.EAST);
		xcw.addChild(textfields, BorderLayout.Border.WEST);
		xcw.addChild(checks, BorderLayout.Border.SOUTH);
		xcw.addChild(radios, BorderLayout.Border.CENTER);

		screen.addElement(xcw);

	}

	SelectableItem createPanel(ElementManager screen, String n) {
		SelectableItem p = new SelectableItem(screen, n);
		p.setIgnoreMouse(true);
		MigLayout l = new MigLayout(screen, "ins 0, wrap 1, fill", "[grow, fill]", "");
		p.setLayoutManager(l);

		Label l1 = new Label(n);
		l1.setText(n);
		l1.setIgnoreMouse(true);
		l1.setFontSize(screen.getStyle("Common").getFloat("mediumFontSize"));
		p.addChild(l1);

		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		l2.setIgnoreMouse(true);
		p.addChild(l2);
		return p;
	}
}
