package org.icescene.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.icelib.ClothingTemplate;
import org.icelib.ClothingTemplateKey;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.Service;

@Service
public class ClothingTemplates extends AbstractConfigurationMap<ClothingTemplateKey, ClothingTemplate> implements IcesceneService {
	private static final long serialVersionUID = 1L;
	
	public ClothingTemplates() {
		this(new LinkedHashMap<ClothingTemplateKey, ClothingTemplate>());
	}

	public ClothingTemplates(Map<ClothingTemplateKey, ClothingTemplate> backingMap) {
		super(backingMap, ClothingTemplateKey.class, ClothingTemplate.class);
	}


	protected ClothingTemplate putToBackingMap(ClothingTemplateKey key, ClothingTemplate value) {
		value.setKey(key);
		return backingMap.put(key, value);
	}

	@Override
	protected String allAssetsPattern() {
		return "Armor/.*/.*\\.js";
	}

	@Override
	public void init(IcesceneApp app) {
	}

}
