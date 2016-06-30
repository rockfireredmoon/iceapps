package org.icescene.configuration.attachments;

import java.util.LinkedHashMap;
import java.util.Map;

import org.icelib.AttachableTemplate;
import org.icelib.EntityKey;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.Service;
import org.icescene.configuration.AbstractConfigurationMap;

@Service
public class AttachableTemplates extends AbstractConfigurationMap<EntityKey, AttachableTemplate> implements
		IcesceneService {

	private static final long serialVersionUID = 1L;

	public AttachableTemplates() {
		this(new LinkedHashMap<EntityKey, AttachableTemplate>());
	}

	public AttachableTemplates(Map<EntityKey, AttachableTemplate> backingMap) {
		super(backingMap, EntityKey.class, AttachableTemplate.class);
	}


	protected AttachableTemplate putToBackingMap(EntityKey key, AttachableTemplate value) {
		value.setKey(key);
		return backingMap.put(key, value);
	}

	@Override
	protected String allAssetsPattern() {
		return "(Item|Armor)/.*/.*\\.js";
	}

	@Override
	public void init(IcesceneApp app) {
	}

}
