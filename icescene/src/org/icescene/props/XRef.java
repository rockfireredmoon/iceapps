package org.icescene.props;

import org.icescene.propertyediting.Property;

import com.google.common.base.Objects;

public class XRef extends AbstractComponentPiece {
	public static final String ATTR_SCALE = "Scale";
	public static final String ATTR_CREF = "CRef";
	public static final String ATTR_FLOOR_ANCHOR = "FloorAnchor";

	private String cref;
	private int floorAnchor;

	public XRef(String cref) {
		this.cref = cref;
	}

	public XRef clone() {
		XRef xRef = new XRef(cref);
		populateClone(xRef);
		xRef.floorAnchor = floorAnchor;
		return xRef;
	}

	@Property(label = "CRef", weight = 0)
	public String getCRef() {
		return cref;
	}

	@Property
	public void setCRef(String cref) {
		String old = getCRef();
		if (!Objects.equal(old, cref)) {
			this.cref = cref;
			changeSupport.firePropertyChange(ATTR_CREF, old, cref);
		}
	}

	@Property(label = "Floor Anchor", weight = 60)
	public int getFloorAnchor() {
		return floorAnchor;
	}

	@Property
	public void setFloorAnchor(int floorAnchor) {
		int old = getFloorAnchor();
		if (!Objects.equal(old, floorAnchor)) {
			this.floorAnchor = floorAnchor;
			changeSupport.firePropertyChange(ATTR_FLOOR_ANCHOR, old, floorAnchor);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cref == null) ? 0 : cref.hashCode());
		result = prime * result + floorAnchor;
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		result = prime * result + ((rotation == null) ? 0 : rotation.hashCode());
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
		XRef other = (XRef) obj;
		if (cref == null) {
			if (other.cref != null)
				return false;
		} else if (!cref.equals(other.cref))
			return false;
		if (floorAnchor != other.floorAnchor)
			return false;
		if (translation == null) {
			if (other.translation != null)
				return false;
		} else if (!translation.equals(other.translation))
			return false;
		if (rotation == null) {
			if (other.rotation != null)
				return false;
		} else if (!rotation.equals(other.rotation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XRef [cref=" + cref + ", floorAnchor=" + floorAnchor + ", rotation=" + rotation + ", location="
				+ translation + "]";
	}

}
