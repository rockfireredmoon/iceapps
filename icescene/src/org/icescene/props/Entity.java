package org.icescene.props;

import org.icescene.propertyediting.Property;

import com.google.common.base.Objects;
import com.jme3.scene.Spatial;

public class Entity extends AbstractComponentPiece {
	public static final String ATTR_SCALE = "Scale";
	public static final String ATTR_UNLIT = "Unlit";
	public static final String ATTR_MESH = "Mesh";
	public static final String ATTR_QF = "QF";
	public static final String ATTR_VF = "VF";

	private String mesh;
	private int qf;
	private int vf;
	private Class<Spatial> meshClass;
	private Class<Spatial> physicsMeshClass;
	private boolean unlit;

	public Entity(String mesh) {
		this(mesh, 0, 0);
	}

	public Entity(String mesh, int qf, int vf) {
		this.mesh = mesh;
		this.qf = qf;
		this.vf = vf;
	}

	public Entity clone() {
		Entity entity = new Entity(mesh, qf, vf);
		populateClone(entity);
		return entity;
	}

	@Property(label = "Mesh", weight = 0)
	public String getMesh() {
		return mesh;
	}

	@Property
	public void setMesh(String mesh) {
		String old = getMesh();
		if (!Objects.equal(old, mesh)) {
			this.mesh = mesh;
			changeSupport.firePropertyChange(ATTR_MESH, old, mesh);
		}
	}

	public void setPhysicsMeshClass(Class<Spatial> physicsMeshClass) {
		this.physicsMeshClass = physicsMeshClass;
	}

	public Class<Spatial> getPhysicsMeshClass() {
		return physicsMeshClass;
	}

	public Class<Spatial> getMeshClass() {
		return meshClass;
	}

	public Entity setMeshClass(Class<Spatial> meshClass) {
		this.meshClass = meshClass;
		return this;
	}

	@Property(label = "QF", weight = 60)
	public int getQf() {
		return qf;
	}

	@Property
	public void setQf(int qf) {
		int old = getQf();
		if (!Objects.equal(old, qf)) {
			this.qf = qf;
			changeSupport.firePropertyChange(ATTR_QF, old, qf);
		}
	}

	@Property(label = "VF", weight = 60)
	public int getVf() {
		return vf;
	}

	@Property
	public void setVf(int vf) {
		int old = getVf();
		if (!Objects.equal(old, vf)) {
			this.vf = vf;
			changeSupport.firePropertyChange(ATTR_VF, old, qf);
		}
	}

	public boolean isNormal() {
		return (mesh == null && meshClass != null) || (mesh != null && !isBlocking() && !isWalk());
	}

	public boolean isBlocking() {
		return mesh != null && (mesh.endsWith("-Blocking.mesh") || mesh.endsWith("-Blocking.mesh.xml"));
	}

	public boolean isWalk() {
		return mesh != null && (mesh.endsWith("-WalkMesh.mesh") || mesh.endsWith("-WalkMesh.mesh.xml"));
	}

	@Override
	public String toString() {
		return "Entity [mesh=" + mesh + ", qf=" + qf + ", vf=" + vf + ", meshClass=" + meshClass + ", physicsMeshClass="
				+ physicsMeshClass + ", rotation=" + rotation + ", location=" + translation + ", unlit=" + unlit + "]";
	}

	@Property
	public void setUnlit(boolean unlit) {
		boolean old = isUnlit();
		this.unlit = unlit;
		changeSupport.firePropertyChange(ATTR_UNLIT, old, unlit);
	}

	@Property(label = "Unlit", weight = 80)
	public boolean isUnlit() {
		return unlit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		result = prime * result + ((mesh == null) ? 0 : mesh.hashCode());
		result = prime * result + ((meshClass == null) ? 0 : meshClass.hashCode());
		result = prime * result + ((physicsMeshClass == null) ? 0 : physicsMeshClass.hashCode());
		result = prime * result + qf;
		result = prime * result + ((rotation == null) ? 0 : rotation.hashCode());
		result = prime * result + (unlit ? 1231 : 1237);
		result = prime * result + vf;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (translation == null) {
			if (other.translation != null)
				return false;
		} else if (!translation.equals(other.translation))
			return false;
		if (mesh == null) {
			if (other.mesh != null)
				return false;
		} else if (!mesh.equals(other.mesh))
			return false;
		if (meshClass == null) {
			if (other.meshClass != null)
				return false;
		} else if (!meshClass.equals(other.meshClass))
			return false;
		if (physicsMeshClass == null) {
			if (other.physicsMeshClass != null)
				return false;
		} else if (!physicsMeshClass.equals(other.physicsMeshClass))
			return false;
		if (qf != other.qf)
			return false;
		if (rotation == null) {
			if (other.rotation != null)
				return false;
		} else if (!rotation.equals(other.rotation))
			return false;
		if (unlit != other.unlit)
			return false;
		if (vf != other.vf)
			return false;
		return true;
	}

}
