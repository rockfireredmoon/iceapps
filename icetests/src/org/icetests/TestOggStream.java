package org.icetests;

import java.util.logging.Logger;

import org.icescene.IcesceneApp;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.UrlLocator;
import com.jme3.audio.AudioNode;
import com.jme3.audio.plugins.OGGLoader;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeSystem;

public class TestOggStream extends IcesceneApp {

    private static final Logger LOG = Logger.getLogger(TestOggStream.class.getName());

    public static void main(String[] args) {
        TestOggStream app = new TestOggStream();
        app.start();
    }

    public TestOggStream() {
        setUseUI(true);
//        setStylePath("icetone/style/def/style_map.gui.xml");


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

        AssetManager   manager = JmeSystem.newAssetManager();
        
        manager.registerLocator("http://revolutionradio.ru/",
                                      UrlLocator.class.getName());
        manager.registerLoader(OGGLoader.class, "ogg");
         AudioNode src = new AudioNode(manager,"live.ogg", true, false);
         src.setPositional(false);
         src.play();
    }
}
