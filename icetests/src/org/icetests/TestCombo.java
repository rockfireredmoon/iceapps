package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.Vector2f;

import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestCombo extends IcesceneApp {
	
	static {
		AppInfo.context = TestCombo.class;
	}

    public static void main(String[] args) {
        TestCombo app = new TestCombo();
        app.start();
    }

    public TestCombo() {
        setUseUI(true);
    }

    @Override
    public void onSimpleInitApp() {
        screen.setUseUIAudio(false);

        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);

        ComboBox<Integer> lcd = new ComboBox<Integer>();
        lcd.addListItem("This is the first list item", 1);
        lcd.addListItem("This is the second list item", 2);
        lcd.addListItem("This is the third list item", 3);
        lcd.addListItem("This is the fourth list item", 4);
        lcd.addListItem("This is the fifth list item", 5);
        lcd.addListItem("This is the sixth list item", 6);
        lcd.addListItem("This is the seventh list item", 7);
        lcd.addListItem("This is the eigth list item", 8);
        lcd.addListItem("This is the ninth list item", 9);
        lcd.addListItem("This is the tenth list item", 10);
        lcd.addListItem("This is the eleventh list item", 11);
        lcd.addListItem("This is the twelvth list item", 12);
        lcd.addListItem("This is the thirteenth list item", 13);
        

        ComboBox<Integer> lcd2 = new ComboBox<Integer>();
        lcd2.addListItem("This is the first list item", 1);
        lcd2.addListItem("This is the second list item", 2);
        lcd2.addListItem("This is the third list item", 3);
        lcd2.addListItem("This is the fourth list item", 4);
        lcd2.addListItem("This is the fifth list item", 5);
        lcd2.addListItem("This is the sixth list item", 6);
        lcd2.addListItem("This is the seventh list item", 7);
        lcd2.addListItem("This is the eigth list item", 8);
        lcd2.addListItem("This is the ninth list item", 9);
        lcd2.addListItem("This is the tenth list item", 10);
        lcd2.addListItem("This is the eleventh list item", 11);
        lcd2.addListItem("This is the twelvth list item", 12);
        lcd2.addListItem("This is the thirteenth list item", 13);
        lcd2.getMenu().setMaxDimensions(new Vector2f(Float.MAX_VALUE, 150));

        Panel panel = new Panel();
        panel.setLayoutManager(new MigLayout());
        panel.addChild(new Label("Normal:", screen));
        panel.addChild(lcd);
        panel.addChild(new Label("Vis of 150:", screen));
        panel.addChild(lcd2);

        // Add window to screen (causes a layout)        
        screen.addElement(panel);

    }
}
