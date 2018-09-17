package org.icescene.props;

import org.icescene.scene.Buildable;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public interface ComponentPiece extends Buildable {

	void setTranslation(Vector3f localTranslation);

	void setRotation(Quaternion localRotation);

	Quaternion getRotation();

	Vector3f getTranslation();

}
