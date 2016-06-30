package org.icescene.props;

import org.icescene.props.AbstractProp.Visibility;

import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

public class PropUserDataBuilder {

	private static final String LAST_CULL = "lastCull";
	private static final String VISIBILITY = "visibility";
	private static final String VISIBLE = "visible";
	private static final String SELECTABLE = "selectable";

	public static Visibility getVisibility(Spatial s) {
		String v = s.getUserData(VISIBILITY);
		return v == null ? Visibility.BOTH : Visibility.valueOf(v);
	}

	public static void setVisibility(Spatial s, Visibility v) {
		s.setUserData(VISIBILITY, v.name());
	}

	public static boolean isVisible(Spatial s) {
		Boolean v = s.getUserData(VISIBLE);
		return v == null ? true : v;
	}
	
	public static void setSelectable(Spatial s, boolean selectable) {
		s.setUserData(SELECTABLE, selectable);
	}

	public static boolean isSelectable(Spatial s) {
		Boolean v = s.getUserData(SELECTABLE);
		return v == null ? false : v;
	}

	public static void setVisible(Spatial s, boolean v) {
		boolean vis = isVisible(s);
		if (v != vis) {
			s.setUserData(VISIBLE, v);
			if (!v) {
				s.setUserData(LAST_CULL, s.getCullHint().name());
				s.setCullHint(CullHint.Always);
			} else {
				s.setCullHint(CullHint.valueOf((String) s.getUserData(LAST_CULL)));
			}
		}
	}
}
