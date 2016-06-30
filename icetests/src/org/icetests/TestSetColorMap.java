package org.icetests;

import com.jme3.app.SimpleApplication;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.windows.Panel;
import icetone.controls.windows.Window;
import icetone.core.Screen;

public class TestSetColorMap extends SimpleApplication {

	//
	// Change these to paths of 3 different images in your assets
	//
    private static final String IMAGE_1 = "Interface/bgx.jpg";
	private static final String IMAGE_2 = "Interface/bgy.jpg";
	private static final String IMAGE_3 = "Interface/bgz.jpg";

	public static void main(String[] args) {
        TestSetColorMap app = new TestSetColorMap();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setDragToRotate(true);

        Screen screen = new Screen(this, "icetone/style/def/style_map.gui.xml");
        screen.setUseToolTips(true);
        
        // Preview window
        final Panel imgPreviewWindow = new Panel(screen, new Vector2f(100, 100));
        
        // Button window
        Window buttonWindow = new Window(screen, new Vector2f(400, 300), new Vector2f(340, 100));
        buttonWindow.getDragBar().setText("Window 1");

        // Swing to image 1
        ButtonAdapter b1 = new ButtonAdapter(screen, new Vector2f(20, 40), new Vector2f(100, 30)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				imgPreviewWindow.setColorMap(IMAGE_1);
			}
        	
        };
        b1.setText("Img 1");
        buttonWindow.addChild(b1);

        // Swing to image 2
        ButtonAdapter b2 = new ButtonAdapter(screen, new Vector2f(120, 40), new Vector2f(100, 30)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				imgPreviewWindow.setColorMap(IMAGE_2);
			}
        	
        };
        b2.setText("Img 2");
        buttonWindow.addChild(b2);

        // Swing to image 3
        ButtonAdapter b3 = new ButtonAdapter(screen, new Vector2f(220, 40), new Vector2f(100, 30)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				imgPreviewWindow.setColorMap(IMAGE_3);
			}
        	
        };
        b3.setText("Img 3");
        buttonWindow.addChild(b3);
        
        screen.addElement(buttonWindow);
        screen.addElement(imgPreviewWindow);
        guiNode.addControl(screen);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
