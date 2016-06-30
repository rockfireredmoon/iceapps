package org.icescene.io;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.crypto.dsig.keyinfo.KeyName;

import com.jme3.input.KeyInput;

/**
 * Helper that uses reflection to get a human readable key name from the key code constant
 */
public class KeyNames {

	final static Logger LOG = Logger.getLogger(KeyName.class.getName());
	
    private final static Map<Integer, String> codeToName = new HashMap<Integer, String>();
    private final static Map<String, Integer> nameToCode = new HashMap<String, Integer>();
    
    static {
    	try {
            Field[] fields = KeyInput.class.getDeclaredFields();
            for (Field f : fields) {
            	int keyCode = f.getInt(null);
            	String name = f.getName().substring(4);
				codeToName.put(keyCode, name);
				nameToCode.put(name, keyCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getKeyName(int keyCode) {
        String name = codeToName.get(keyCode);        
        if(name == null) {
        	return "UNKNOWN";
        }
        return name;
    }
    public static int getKeyCode(String keyName) {
        Integer kc = nameToCode.get(keyName);        
        if(kc == null) {
        	throw new IllegalArgumentException("No key named " + keyName);
        }
		return kc;
    }
}
