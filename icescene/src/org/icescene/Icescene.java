package org.icescene;

public class Icescene {
    
    

    public static String checkAssetPath(String relative) {
        if (relative.endsWith(".png")) {
            return relative;
        } else if (relative.endsWith(".mesh")) {
            relative = relative + ".xml";
        }
        return relative;
    }

    
}
