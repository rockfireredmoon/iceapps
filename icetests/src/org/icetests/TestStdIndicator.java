package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.extras.Indicator;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestStdIndicator extends IcesceneApp {
	static {
		AppInfo.context = TestStdIndicator.class;
	}

    public static void main(String[] args) {
        TestStdIndicator app = new TestStdIndicator();
        app.start();
    }

    public TestStdIndicator() {
        setUseUI(true);
//        setStylePath("icetone/style/def/style_map.gui.xml");


    }

    @Override
    public void onSimpleInitApp() {
        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);

        final ColorRGBA color = new ColorRGBA();

        final Indicator ind = new Indicator(
                screen,
                Vector2f.ZERO,
                new Vector2f(300, 30),
                Indicator.Orientation.HORIZONTAL) {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        };
//        ind.setBaseImage(screen.getStyle("Window").getString("defaultImg"));
//ind.setIndicatorImage(screen.getStyle("Window").getString("defaultImg"));
        ind.setIndicatorColor(ColorRGBA.randomColor());
//        ind.setAlphaMap(screen.getStyle("Indicator").getString("alphaImg"));
        ind.setIndicatorPadding(new Vector4f(7, 7, 7, 7));
        ind.setMaxValue(100);
        ind.setDisplayPercentage();

//        ModelledSlider slider = new ModelledSlider() {
//            @Override
//            public void onChange(int selectedIndex, Object value) {
//                float blend = selectedIndex * 0.01f;
//                color.interpolate(ColorRGBA.Red, ColorRGBA.Green, blend);
//                ind.setIndicatorColor(color);
//                ind.setCurrentValue((Integer) value);
//            }
//        };
        
        Panel p = new Panel(new MigLayout("fill"));
        p.addChild(ind, "growx");
//        p.addChild(slider, "growx");

        screen.addElement(p);

    }
}
