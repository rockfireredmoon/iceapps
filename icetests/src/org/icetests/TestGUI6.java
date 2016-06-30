package org.icetests;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.ui.Picture;

public class TestGUI6 extends SimpleApplication {

    public static void main(String[] args) {
        TestGUI6 app = new TestGUI6();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);
       

        Picture p = new Picture("background");
        p.setImage(assetManager, "Interface/login.jpg", false);

        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setPosition(0, 0);

        ViewPort pv = renderManager.createPreView("background", cam);
        pv.setClearFlags(true, true, true);

        pv.attachScene(p);
// pv.setBackgroundColor(ColorRGBA.Red);

        viewPort.setClearFlags(false, true, true);
        p.updateGeometricState();
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
