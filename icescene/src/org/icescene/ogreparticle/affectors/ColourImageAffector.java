package org.icescene.ogreparticle.affectors;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.text.ParseException;

import org.icescene.ogreparticle.AbstractOGREParticleAffector;
import org.icescene.ogreparticle.OGREParticleScript;
import org.icescene.propertyediting.ChooserInfo;
import org.icescene.propertyediting.Property;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;

import emitter.influencers.ParticleInfluencer;
import emitter.particle.ParticleData;

/**
 */
public class ColourImageAffector extends AbstractOGREParticleAffector {

	private boolean enabled = true;
	private String image;
	private Image bitmap;
	private int x = 0;
	private ByteBuffer buf;

	public ColourImageAffector(OGREParticleScript script) {
		super(script);
	}

	public ColourImageAffector(OGREParticleScript script, String image) {
		super(script);
		this.image = image;
	}

	@Override
	public void update(ParticleData p, float tpf) {
		if(x == bitmap.getWidth()) {
			buf.rewind();
			x = 0;
		}
		if(bitmap.getFormat() == Format.RGBA8) {
			p.color.r = (float)(buf.get() & 0xff) / 255f;
			p.color.g = (float)(buf.get() & 0xff) / 255f;
			p.color.b = (float)(buf.get() & 0xff) / 255f;
			p.color.a = (float)(buf.get() & 0xff) / 255f;
			x++;
		}
		else if(bitmap.getFormat() == Format.BGR8) {
			p.color.b = (float)(buf.get() & 0xff) / 255f;
			p.color.g = (float)(buf.get() & 0xff) / 255f;
			p.color.r = (float)(buf.get() & 0xff) / 255f;
			p.color.a = 1;
			x++;
		}
		else {
			throw new RuntimeException(String.format("Unsupported image format %s", bitmap.getFormat()));
		}
	}

	@Override
    public void initialize(ParticleData p) {
		Texture loadTexture = p.emitter.getAssetManager().loadTexture("Effects/" + image);
		bitmap = loadTexture.getImage();
		buf = bitmap.getData(0);
		buf.rewind();
		System.out.println("BUFLE: " + buf.limit() + " WID: " + bitmap.getWidth());
    }

	@Override
	public void reset(ParticleData p) {
	}

	@Property
	public String getImage() {
		return image;
	}

	@ChooserInfo(root = "Textures/Effects", pattern = "Textures/Effects/.*")
	@Property(label = "Image", weight = 10, hint = Property.Hint.TEXTURE_PATH)
	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
	}

	@Override
	public void read(JmeImporter im) throws IOException {
	}

	@Override
	public ParticleInfluencer clone() {
		ColourImageAffector clone = new ColourImageAffector(script, image);
		clone.setEnabled(enabled);
		return clone;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public Class getInfluencerClass() {
		return ColourImageAffector.class;
	}

	@Override
	protected void writeAffector(PrintWriter pw) {
		pw.println(String.format("\t\timage %s", image));
		pw.flush();
	}

	public boolean parse(String[] args, int lineNumber) throws ParseException {
		if (args[0].equals("image")) {
			if (args.length == 2) {
				image = args[1];
			} else {
				throw new ParseException("Expected texture path value after image at line " + lineNumber + ".", 0);
			}
		} else {
			return false;
		}
		return true;
	}
}
