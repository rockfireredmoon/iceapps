package org.icescene.props;

import org.icescene.entities.EntityContext;
import org.icescene.propertyediting.Property;

import com.google.common.base.Objects;

public class BuildingXMLEntity extends XMLProp {
	public static final String ATTR_ATS = "ATS";

	public BuildingXMLEntity(final String name, final EntityContext app) {
		super(name, app);
	}

	@Override
	public void onConfigureProp() {
		sceneryItem.setDefaultVariable(ATTR_ATS, "ATS-Urban");
		super.onConfigureProp();
	}

	protected String resolveSubmesh(String mesh) {
		if (mesh.startsWith("ATS-")) {
			return String.format("ATS/%s/%s", getATS(), mesh.replace('#', '/'));
		} else {
			return super.resolveSubmesh(mesh);
		}
	}

	@Property(label = "ATS", weight = 10)
	public String getATS() {
		return sceneryItem.getVariables().get(ATTR_ATS);
	}

	@Property
	public void setATS(String ats) {
		String old = sceneryItem.getVariables().get(ATTR_ATS);
		sceneryItem.getVariables().put(ATTR_ATS, ats);
		reload();
		if (!Objects.equal(old, ats)) {
			changeSupport.firePropertyChange(ATTR_ATS, old, ats);
		}
	}
}
