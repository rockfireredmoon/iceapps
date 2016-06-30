package org.icescene.ogreparticle.emitters;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Logger;

import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.FloatRange;
import org.icescene.propertyediting.Property;

import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Torus;

import emitter.Emitter;

public class HollowEllipsoidEmitter extends Abstract3DEmitter {

	private static final Logger LOG = Logger.getLogger(HollowEllipsoidEmitter.class.getName());
	private Vector3f innerSize = new Vector3f(0.5f, 0.5f, 0.5f);

	public HollowEllipsoidEmitter(OGREParticleScript group) {
		super(group);
	}

	@Property(label = "Inner Size", weight = 25, hint = Property.Hint.SCALE)
	@FloatRange(incr = 0.1f)
	public Vector3f getInnerSize() {
		return innerSize;
	}

	@Property
	public void setInnerSize(Vector3f innerSize) {
		this.innerSize = innerSize;
	}

	@Override
	public boolean parse(String[] args, int lineNo) throws ParseException {
		boolean ok = super.parse(args, lineNo);
		if (!ok) {
			if (args[0].equals("inner_height")) {
				if (args.length == 2) {
					innerSize.y = (Float.parseFloat(args[1]));
					ok = true;
				} else {
					throw new ParseException("Expected single inner_height at line " + lineNo + ".", 0);
				}
			} else if (args[0].equals("inner_width")) {
				if (args.length == 2) {
					innerSize.x = (Float.parseFloat(args[1]));
					ok = true;
				} else {
					throw new ParseException("Expected single inner_width at line " + lineNo + ".", 0);
				}
			} else if (args[0].equals("inner_depth")) {
				if (args.length == 2) {
					innerSize.z = (Float.parseFloat(args[1]));
					ok = true;
				} else {
					throw new ParseException("Expected single inner_depth at line " + lineNo + ".", 0);
				}
			}
		}
		return ok;
	}

	@Override
	protected void createEmitterShape(Emitter emitter) {
		LOG.info("    Tri Mesh");
		emitter.setUseRandomEmissionPoint(true);

		float fac = innerSize == null ? 0.25f : innerSize.x / size.x / 2f;

		Torus torus = new Torus(32, 32, fac, 0.5f);
		LOG.info(String.format("    Hollow Ellipsoid emitter shape of %f x %f x %f", size.x, size.y, size.z));
		emitter.setShape(torus);
		emitter.setLocalScale(size);
	}

	@Override
	protected void writeEmitter(PrintWriter pw) {
		super.writeEmitter(pw);
		pw.println(String.format("\t\tinner_width %1.1f", innerSize.x));
		pw.println(String.format("\t\tinner_height %1.1f", innerSize.y));
		pw.println(String.format("\t\tinner_depth %1.1f", innerSize.z));
	}
}
