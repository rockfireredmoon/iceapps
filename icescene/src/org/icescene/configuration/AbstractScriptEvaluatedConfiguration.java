package org.icescene.configuration;

import org.icescripting.Scripts;

import icemoon.iceloader.BaseConfiguration;

public class AbstractScriptEvaluatedConfiguration<C> extends BaseConfiguration<C> {

	public AbstractScriptEvaluatedConfiguration(String scriptPath) {
		super(scriptPath, (C)Scripts.get().eval(scriptPath));
	}

}
