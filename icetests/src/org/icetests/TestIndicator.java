package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.extras.Indicator;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestIndicator extends IcesceneApp {
	
	static {
		AppInfo.context = TestIndicator.class;
	}
    
    public static void main(String[] args) {
        TestIndicator app = new TestIndicator();
        app.start();
    }
    private Indicator i;
    
    public TestIndicator() {
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

        // Panel for actions and character selection
        final Panel panel = new Panel();
        panel.setLayoutManager(new MigLayout("fill, wrap 1", "[]", "[][]"));
        
        i = new Indicator() {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        };
//        i.setIndicatorPadding(new Vector4f(7, 7, 7, 7));
        i.setMaxValue(100);
        i.setCurrentValue(75);
        i.setIndicatorText("Indicator Text");
//         i.addControl(new AbstractControl() {
//                    
//                    
//                    @Override
//                    protected void controlUpdate(float tpf) {
//                        spatial.center();
//                        spatial.move(panel.getWidth() / 2f, panel.getHeight() / 2f, tpf);
//                        spatial.rotate(0, 0, tpf);
//                    }
//
//                    @Override
//                    protected void controlRender(RenderManager rm, ViewPort vp) {
//                    }
//                });
         
        panel.addChild(i, "growx");
        
        panel.addChild(new Label("Above is a progress bar type thing", screen), "ax 50%");

        // Add window to screen (causes a layout)        
        screen.addElement(panel);
        
    }
    
    @Override
    public void onUpdate(float tpf) {
        i.setCurrentValue(i.getCurrentValue() + tpf);
        if(i.getCurrentValue() >= i.getMaxValue()) {
            i.setCurrentValue(0);
        }
    }
}
