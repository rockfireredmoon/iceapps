package org.icescene.configuration;

import icemoon.iceloader.AbstractConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.icelib.DOSWriter;
import org.icesquirrel.interpreter.SquirrelInterpretedScript;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;

/**
 * Parses Squirrel script data files.
 */
public abstract class AbstractSquirrelConfiguration extends AbstractConfiguration<SquirrelInterpretedScript> {

	protected Vector3f stringToVector3f(final String str) throws NumberFormatException {
		StringTokenizer t = new StringTokenizer(str, ", \t");
		final Vector3f vector3f = new Vector3f(Float.parseFloat(t.nextToken()), Float.parseFloat(t.nextToken()), Float.parseFloat(t
				.nextToken()));
		return vector3f;
	}

	public AbstractSquirrelConfiguration(SquirrelInterpretedScript backingObject, AssetManager assetManager) {
		super(backingObject, assetManager);
	}

	public AbstractSquirrelConfiguration(String resourceName, AssetManager assetManager, SquirrelInterpretedScript backingObject) {
		super(resourceName, assetManager, backingObject);
	}

	public abstract void fill();

	public void write(OutputStream out) {
		fill();
		backingObject.format(new DOSWriter(out), 0);
	}

	protected abstract String getHeader();

	@Override
	protected void load(InputStream in, SquirrelInterpretedScript backingObject) throws IOException {
		try {
			backingObject.execute(in);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			throw new IOException("Failed to load configuration from Squirrel script.", e);
		}
		if (backingObject.getRootTable().get(getHeader()) == null) {
			throw new IOException("No slot named '" + getHeader() + "'");
		}
	}
}
