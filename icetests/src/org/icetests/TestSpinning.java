package org.icetests;

import org.icescene.IcesceneApp;
import org.iceui.controls.BusySpinner;

import com.jme3.math.Vector2f;

import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

public class TestSpinning extends IcesceneApp {

    public static void main(String[] args) {
        TestSpinning app = new TestSpinning();
        app.start();
    }

    public TestSpinning() {
        setUseUI(true);
    }

    @Override
    public void onSimpleInitApp() {
        screen.setUseCustomCursors(true);

        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);

        Panel xcw = new Panel(screen, new Vector2f(100, 100), new Vector2f(500, 350));
        xcw.setLayoutManager(new BorderLayout(8, 8));
        
        Container c = new Container(screen);
        c.setLayoutManager(new MigLayout(screen, "", "push[31!]push", "push[31!]push"));
        c.addChild(new BusySpinner(screen, new Vector2f(31,31)).setSpeed(10f));
        
        xcw.addChild(c, BorderLayout.Border.CENTER);
        screen.addElement(xcw);
        xcw.show();

    }
}
