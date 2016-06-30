package org.icescene.tools;

import org.icescene.IcesceneApp;

public class ActionData {

    private final IcesceneApp app;
    private final float x, y;

    public ActionData(IcesceneApp app, float x, float y) {
        this.app = app;
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public IcesceneApp getApp() {
        return app;
    }
}
