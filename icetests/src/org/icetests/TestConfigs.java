package org.icetests;

import org.icescene.IcesceneApp;

public class TestConfigs extends IcesceneApp {

    public static void main(String[] args) {
        TestConfigs app = new TestConfigs();
        app.start();
    }
    @Override
    protected void onSimpleInitApp() {
        flyCam.setMoveSpeed(100);
        flyCam.setDragToRotate(true);
    }
}
