package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import icetone.controls.lists.SelectList;
import icetone.controls.windows.Panel;

public class TestStandardSelectList extends IcesceneApp {
	
	static {
		AppInfo.context = TestStandardSelectList.class;
	}

    public static void main(String[] args) {
        TestStandardSelectList app = new TestStandardSelectList();
        app.start();
    }
    
    public TestStandardSelectList() {
        setUseUI(true);
//        setStylePath("icetone/style/def/style_map.gui.xml");
    }

    @Override
    public void onSimpleInitApp() {
        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);

        Panel panel = new Panel();
        SelectList<Integer> scr = new SelectList<>();
        panel.addChild(scr);
        for (int i = 0; i < 10; i++) {
            scr.addListItem("This is list item " + i + " so it is", i);
        }

        // Add window to screen (causes a layout)        
        screen.addElement(panel);

    }
}
