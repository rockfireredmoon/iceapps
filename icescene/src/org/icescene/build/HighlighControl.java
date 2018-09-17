package org.icescene.build;

import org.icescene.NodeVisitor;
import org.icescene.NodeVisitor.VisitResult;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import emitter.Emitter;
import emitter.EmitterMesh.DirectionType;
import emitter.influencers.AlphaInfluencer;
import emitter.influencers.ColorInfluencer;
import emitter.influencers.SizeInfluencer;
import emitter.particle.ParticleDataTriMesh;

public class HighlighControl extends Emitter {

	public HighlighControl(AssetManager assetManager) {

		setMaxParticles(500);
		addInfluencer(new ColorInfluencer());
		addInfluencer(new AlphaInfluencer());
		addInfluencer(new SizeInfluencer());
		setShapeSimpleEmitter();
		setDirectionType(DirectionType.Random);
		setEmissionsPerSecond(100);
		setParticlesPerEmission(5);
		setParticleType(ParticleDataTriMesh.class);
		setBillboardMode(BillboardMode.Camera);
		setForce(1);
		setLife(0.999f);
		setSprite("Textures/default.png");
		getInfluencer(SizeInfluencer.class).addSize(0.1f);
		getInfluencer(SizeInfluencer.class).addSize(0f);
		initialize(assetManager);
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		
		if(spatial instanceof Node) {
			NodeVisitor nv = new NodeVisitor((Node)spatial);
			nv.visit(new NodeVisitor.Visit() {
				
				@Override
				public VisitResult visit(Spatial spatial) {
					if(spatial != null && spatial instanceof Geometry) {
						setShape(((Geometry)spatial).getMesh());
						return VisitResult.END;
					}
					return VisitResult.CONTINUE;
				}
			});
		}
		else if(spatial != null && spatial instanceof Geometry) {
			setShape(((Geometry)spatial).getMesh());
		}
	}

}
