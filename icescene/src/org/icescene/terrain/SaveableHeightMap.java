package org.icescene.terrain;

import java.io.OutputStream;

import com.jme3.texture.Image;

public interface SaveableHeightMap {

    Image save(OutputStream out);

    Image save(OutputStream out, boolean flipX, boolean flipY);

	void setHeightData(float[] heightMap);

	Image getColorImage();

	float getHeightScale();
}
