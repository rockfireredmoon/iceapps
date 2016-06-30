package org.icescene.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.LazyMap;
import org.icelib.ClothingTemplate;
import org.icelib.ClothingTemplateKey;
import org.icescene.IcesceneApp;
import org.icescene.Service;
import org.icescripting.Scripts;

@Service
public class ClothingDef extends ClothingTemplates {
	private static final long serialVersionUID = 1L;

	public ClothingDef() {
		this(new LinkedHashMap<ClothingTemplateKey, ClothingTemplate>());
	}

	public ClothingDef(Map<ClothingTemplateKey, ClothingTemplate> backingMap) {
		super();
		ClothingFactory fac = new ClothingFactory(this);
		this.backingMap = LazyMap.lazyMap(backingMap, fac);
	}

	@Override
	public void init(IcesceneApp app) {
	}

	@Override
	protected String allAssetsPattern() {
		return "Armor/.*/.*\\.js";
	}

	static class ClothingFactory implements Transformer<ClothingTemplateKey, ClothingTemplate> {

		private ClothingTemplates templates;

		public ClothingFactory(ClothingTemplates templates) {
			this.templates = templates;
		}

		@Override
		public ClothingTemplate transform(ClothingTemplateKey input) {
			String scriptPath = String.format("%s/%s.js", input.getPath(), input.getName());
			if (Scripts.get().isLoaded(scriptPath)) {
				throw new IllegalStateException("Should not happen.");
			}
			Scripts.get().eval(scriptPath);
			return templates.get(input);
		}

	}
}
