package org.icescene.configuration.attachments;

import org.icelib.Appearance;
import org.icelib.Icelib;
import org.icelib.beans.MappedMap;
import org.icescene.IcesceneApp;
import org.icescene.IcesceneService;
import org.icescene.Service;

@Service
public class AttachmentPoints extends MappedMap<String, AttachmentPointSet> implements IcesceneService {

	public AttachmentPoints() {
		super(String.class, AttachmentPointSet.class);
	}

	@Override
	public void init(IcesceneApp app) {
	}

	public AttachmentPointSet getSet(Appearance appearance) {
		// Get the attachments config for this creature
		String key;
		switch (appearance.getName()) {
		case C2:
			key = "Biped." + Icelib.toEnglish(appearance.getGender());
			break;
		case N4:
			key = appearance.getBodyTemplate();
			break;
		case P1:
			return null;
		default:
			throw new UnsupportedOperationException();
		}
		return get(key);
	}

}
