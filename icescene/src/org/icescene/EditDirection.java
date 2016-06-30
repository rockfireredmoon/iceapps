package org.icescene;

import com.jme3.math.Vector3f;


public enum EditDirection {

    X, Y, Z, RY, RP, RR, XZ;
    
    public boolean isMoveAxis() {
    	switch(this) {
    	case X:
    	case Y:
    	case Z:
    		return true;
    	default:
        	return false;
    	} 
    }
    
    public boolean isRotate() {
    	switch(this) {
    	case RP:
    	case RY:
    	case RR:
    		return true;
    	default:
        	return false;
    	} 
    }

    /**
     * Get the direction vector of the plane on which mouse motion should happen to
     * cause an edit in this direction.
     *
     * @return
     */
    public Vector3f toPlaneNormal() {
        switch (this) {
            case X:
              return Vector3f.UNIT_Y;
            case Y:
                return Vector3f.UNIT_X;
            case Z:
                return Vector3f.UNIT_Y;

                // Rotate yaw uses left to right motions
//                case RY:
//                    return Vector3f.UNIT_Y;
//                // Rotate pitch and rotateBuildable roll use up and down motions
//                case RP:
//                    return Vector3f.UNIT_X;
//                case RR:
//                    return Vector3f.UNIT_X;
                
            case RY:
                return Vector3f.UNIT_X;
            // Rotate pitch and rotateBuildable roll use up and down motions
            case RP:
            case RR:
                return Vector3f.UNIT_Y;
        }
        throw new IllegalArgumentException();
    }
}