package org.icescene.io;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;

public class ModifierKeysAppState extends AbstractAppState {

    public interface Listener {

        void modifiersChange(int newMods);
    }
    public final static int L_SHIFT_MASK = 1;
    public final static int L_CTRL_MASK = 2;
    public final static int L_ALT_MASK = 4;
    public final static int L_META_MASK = 8;
    public final static int R_SHIFT_MASK = 16;
    public final static int R_CTRL_MASK = 32;
    public final static int R_ALT_MASK = 64;
    public final static int R_META_MASK = 128;
    public final static int SHIFT_MASK = 256;
    public final static int CTRL_MASK = 512;
    public final static int ALT_MASK = 1024;
    public final static int META_MASK = 2048;
    private final static Logger LOG = Logger.getLogger(ModifierKeysAppState.class.getName());
    private final static Trigger LEFT_SHIFT_TRIGGER = new KeyTrigger(KeyInput.KEY_LSHIFT);
    private final static Trigger LEFT_CONTROL_TRIGGER = new KeyTrigger(KeyInput.KEY_LCONTROL);
    private final static Trigger LEFT_ALT_TRIGGER = new KeyTrigger(KeyInput.KEY_LMENU);
    private final static Trigger LEFT_META_TRIGGER = new KeyTrigger(KeyInput.KEY_LMETA);
    private final static Trigger RIGHT_SHIFT_TRIGGER = new KeyTrigger(KeyInput.KEY_RSHIFT);
    private final static Trigger RIGHT_CONTROL_TRIGGER = new KeyTrigger(KeyInput.KEY_RCONTROL);
    private final static Trigger RIGHT_ALT_TRIGGER = new KeyTrigger(KeyInput.KEY_RMENU);
    private final static Trigger RIGHT_META_TRIGGER = new KeyTrigger(KeyInput.KEY_RMETA);
    public final static String MAPPING_LEFT_SHIFT = "LeftShift";
    public final static String MAPPING_LEFT_CONTROL = "LeftControl";
    public final static String MAPPING_LEFT_ALT = "LeftAlt";
    public final static String MAPPING_LEFT_META = "LeftMeta";
    public final static String MAPPING_RIGHT_SHIFT = "RightShift";
    public final static String MAPPING_RIGHT_CONTROL = "RightControl";
    public final static String MAPPING_RIGHT_ALT = "RightAlt";
    public final static String MAPPING_RIGHT_META = "RightMeta";
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_LEFT_SHIFT)) {
                lshift = isPressed;
            } else if (name.equals(MAPPING_LEFT_CONTROL)) {
                lctrl = isPressed;
            } else if (name.equals(MAPPING_LEFT_ALT)) {
                lalt = isPressed;
            } else if (name.equals(MAPPING_LEFT_META)) {
                lmeta = isPressed;
            } else if (name.equals(MAPPING_RIGHT_SHIFT)) {
                rshift = isPressed;
            } else if (name.equals(MAPPING_RIGHT_CONTROL)) {
                rctrl = isPressed;
            } else if (name.equals(MAPPING_RIGHT_ALT)) {
                ralt = isPressed;
            } else if (name.equals(MAPPING_RIGHT_META)) {
                rmeta = isPressed;
            }

            int mask = getMask();
            for(Listener l : listeners) {
                l.modifiersChange(mask);
            }
        }
    };
    
    private boolean lshift, lctrl, lalt, lmeta;
    private boolean rshift, rctrl, ralt, rmeta;
    private Application app;
    private List<Listener> listeners = new ArrayList<Listener>();
    private static ModifierKeysAppState instance;
    
    public static ModifierKeysAppState get() {
        return instance;
    }

    public static boolean isShift(int dragMods) {
        return (dragMods & ModifierKeysAppState.SHIFT_MASK) > 0;
    }

    public static boolean isAlt(int dragMods) {
        return (dragMods & ModifierKeysAppState.ALT_MASK) > 0;
    }

    public static boolean isCtrl(int dragMods) {
        return (dragMods & ModifierKeysAppState.CTRL_MASK) > 0;
    }

    public static boolean isMeta(int dragMods) {
        return (dragMods & ModifierKeysAppState.META_MASK) > 0;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        instance = this;
        super.initialize(stateManager, app);

        this.app = app;

        app.getInputManager().addMapping(MAPPING_LEFT_SHIFT, LEFT_SHIFT_TRIGGER);
        app.getInputManager().addMapping(MAPPING_LEFT_CONTROL, LEFT_CONTROL_TRIGGER);
        app.getInputManager().addMapping(MAPPING_LEFT_ALT, LEFT_ALT_TRIGGER);
        app.getInputManager().addMapping(MAPPING_LEFT_META, LEFT_META_TRIGGER);
        app.getInputManager().addMapping(MAPPING_RIGHT_SHIFT, RIGHT_SHIFT_TRIGGER);
        app.getInputManager().addMapping(MAPPING_RIGHT_CONTROL, RIGHT_CONTROL_TRIGGER);
        app.getInputManager().addMapping(MAPPING_RIGHT_ALT, RIGHT_META_TRIGGER);
        app.getInputManager().addMapping(MAPPING_RIGHT_META, LEFT_SHIFT_TRIGGER);

        app.getInputManager().addListener(actionListener, new String[]{MAPPING_LEFT_SHIFT, MAPPING_LEFT_CONTROL,
            MAPPING_LEFT_ALT, MAPPING_LEFT_META, MAPPING_RIGHT_SHIFT, MAPPING_RIGHT_CONTROL, MAPPING_RIGHT_ALT, MAPPING_RIGHT_META});
    }

    @Override
    public void cleanup() {
        super.cleanup();
        app.getInputManager().deleteMapping(MAPPING_LEFT_SHIFT);
        app.getInputManager().deleteMapping(MAPPING_LEFT_CONTROL);
        app.getInputManager().deleteMapping(MAPPING_LEFT_ALT);
        app.getInputManager().deleteMapping(MAPPING_LEFT_META);
        app.getInputManager().deleteMapping(MAPPING_RIGHT_SHIFT);
        app.getInputManager().deleteMapping(MAPPING_RIGHT_CONTROL);
        app.getInputManager().deleteMapping(MAPPING_RIGHT_ALT);
        app.getInputManager().deleteMapping(MAPPING_RIGHT_META);
        app.getInputManager().removeListener(actionListener);
        instance = null;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public boolean isShift() {
        return lshift || rshift;
    }

    public boolean isCtrl() {
        return lctrl || rctrl;
    }

    public boolean isAlt() {
        return lalt || ralt;
    }

    public boolean isMeta() {
        return lmeta || rmeta;
    }

    public boolean isLshift() {
        return lshift;
    }

    public boolean isLctrl() {
        return lctrl;
    }

    public boolean isLalt() {
        return lalt;
    }

    public boolean isLmeta() {
        return lmeta;
    }

    public boolean isRshift() {
        return rshift;
    }

    public boolean isRctrl() {
        return rctrl;
    }

    public boolean isRalt() {
        return ralt;
    }

    public boolean isRmeta() {
        return rmeta;
    }

    public int getMask() {
        int mask = 0;
        mask = mask | (lshift ? L_SHIFT_MASK : 0);
        mask = mask | (lctrl ? L_CTRL_MASK : 0);
        mask = mask | (lalt ? L_ALT_MASK : 0);
        mask = mask | (lmeta ? L_META_MASK : 0);
        mask = mask | (rshift ? R_SHIFT_MASK : 0);
        mask = mask | (rctrl ? R_CTRL_MASK : 0);
        mask = mask | (ralt ? R_ALT_MASK : 0);
        mask = mask | (rmeta ? R_META_MASK : 0);
        mask = mask | (lshift | rshift ? SHIFT_MASK : 0);
        mask = mask | (lctrl | rctrl ? CTRL_MASK : 0);
        mask = mask | (lalt | ralt ? ALT_MASK : 0);
        mask = mask | (lmeta | rmeta ? META_MASK : 0);
        return mask;
    }
}
