package org.icescene.configuration.creatures;

import java.util.HashMap;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.LazyMap;
import org.icelib.beans.MappedMap;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.Service;
import org.icescene.ServiceRef;
import org.icescripting.Scripts;

/**
 * Creature definitions. In order to only load definitions as and when they are
 * needed, the elements of this map are initialised lazily from the creatures
 * script the first time the key is used. Ordinary only the the
 * {@link ContentDef#get(Object)} should be called meaning only individual
 * scripts are loaded, but some of the design tools may want to load all of the
 * definitions to get a list of names (or objects). In this case all unloaded
 * scripts will be loaded.
 *
 */
@Service
public class ContentDef extends MappedMap<CreatureKey, CreatureDefinition> implements IcesceneService {

	private static final long serialVersionUID = 1L;

	public ContentDef() {
		super(LazyMap.lazyMap(new HashMap<CreatureKey, CreatureDefinition>(), new ContentFactory()), CreatureKey.class,
				CreatureDefinition.class);
	}

	@Override
	public void init(IcesceneApp app) {
	}

	static class ContentFactory implements Transformer<CreatureKey, CreatureDefinition> {

		@ServiceRef
		private static ContentDef contentDef;

		@Override
		public CreatureDefinition transform(CreatureKey input) {
			String scriptPath = input.getPath() + ".js";
			if (Scripts.get().isLoaded(scriptPath)) {
				throw new IllegalStateException("Should not happen.");
			}
			Scripts.get().eval(scriptPath);
			return contentDef.get(input);
		}

	}
}
