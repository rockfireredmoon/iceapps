package org.icescene.configuration.attachments;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.LazyMap;
import org.icelib.AttachableTemplate;
import org.icelib.EntityKey;
import org.icescene.Service;
import org.icescripting.Scripts;

@Service
public class AttachableDef extends AttachableTemplates {

	private static final long serialVersionUID = 1L;

	public AttachableDef() {
		this(new LinkedHashMap<EntityKey, AttachableTemplate>());
	}

	public AttachableDef(Map<EntityKey, AttachableTemplate> backingMap) {
		super();
		ItemFactory fac = new ItemFactory(this);
		this.backingMap = LazyMap.lazyMap(backingMap, fac);
	}

	protected static class ItemFactory implements Transformer<EntityKey, AttachableTemplate> {

		private AttachableTemplates templates;

		public ItemFactory(AttachableTemplates templates) {
			this.templates = templates;
		}

		@Override
		public AttachableTemplate transform(EntityKey input) {
			String scriptPath = String.format("%1$s/%2$s.js", input.getPath(), input.getItemName());
			if (Scripts.get().isLoaded(scriptPath)) {
				throw new IllegalStateException("Should not happen.");
			}
			Scripts.get().eval(scriptPath);
			return templates.get(input);
		}

	}

}
