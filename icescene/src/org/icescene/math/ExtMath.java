package org.icescene.math;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class ExtMath {

	public static Quaternion orientation(float y, float p, float r) {
		return new Quaternion().fromAngleAxis(circleRatio(y), new Vector3f(1.0f, 0.0f, 0.0f))
				.mult(new Quaternion().fromAngleAxis(circleRatio(p), new Vector3f(0.0f, 1.0f, 0.0f)))
				.mult(new Quaternion().fromAngleAxis(circleRatio(r), new Vector3f(0.0f, 0.0f, 1.0f)));
	}

	public static float circleRatio(float ratio) {
		return FastMath.TWO_PI * ratio;
	}

}
