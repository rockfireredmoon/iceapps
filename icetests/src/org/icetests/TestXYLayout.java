package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.windows.Panel;
import icetone.core.layout.LUtil;
import icetone.core.layout.XYLayoutManager;

public class TestXYLayout extends IcesceneApp {
	
	static {
		AppInfo.context = TestXYLayout.class;
	}

    public static void main(String[] args) {
        TestXYLayout app = new TestXYLayout();
        app.start();
    }

    public TestXYLayout() {
        setUseUI(true);
    }

    @Override
    public void onSimpleInitApp() {
        screen.setUseUIAudio(false);

        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);

        Panel panel = new Panel(screen, "Panel",
                new Vector2f(8, 8), new Vector2f(300f, 300));
        panel.setLayoutManager(new XYLayoutManager());
        
            final Vector2f bs = new Vector2f(64, 24);
        for(int i = 0 ; i < 10 ; i++) {
            addRandomButton(panel, bs);
        }

        // Add window to screen (causes a layout)        
        screen.addElement(panel);

    }

    private void addRandomButton(final Panel panel, final Vector2f bs) {
        Vector2f p = new Vector2f((int)((float)Math.random() * (panel.getWidth() - bs.x)), (int)((float)Math.random() * ( panel.getHeight() - bs.y)));
        ButtonAdapter ba = new ButtonAdapter(screen, Vector2f.ZERO, bs) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                addRandomButton(panel, bs);
            }
        };
        LUtil.noScaleNoDock(ba);
        
        ba.setText((int)p.x + "," + (int)p.y);
        panel.addChild(ba, p);
    }
}
