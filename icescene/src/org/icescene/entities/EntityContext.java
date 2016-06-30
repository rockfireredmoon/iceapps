package org.icescene.entities;

import java.util.prefs.Preferences;

import org.icescene.Alarm;
import org.icescene.IcesceneApp;
import org.icescene.props.ComponentManager;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;

public interface EntityContext {

	AssetManager getAssetManager();

	Alarm getAlarm();

	AppStateManager getStateManager();

	Preferences getPreferences();
	
	IcesceneApp getApp();

	public static EntityContext create(final Application application) {
		return new EntityContext() {
			@Override
			public AssetManager getAssetManager() {
				return application.getAssetManager();
			}

			@Override
			public Alarm getAlarm() {
				return ((IcesceneApp) application).getAlarm();
			}

			@Override
			public AppStateManager getStateManager() {
				return application.getStateManager();
			}

			@Override
			public Preferences getPreferences() {
				return ((IcesceneApp) application).getPreferences();
			}

			@Override
			public IcesceneApp getApp() {
				return  ((IcesceneApp) application);
			}

			@Override
			public ComponentManager getComponentManager() {
				return ((IcesceneApp)application).getComponentManager();
			}
		};
	}

	ComponentManager getComponentManager();

}
