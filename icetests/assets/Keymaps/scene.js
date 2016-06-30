__KeyMaps = __KeyMaps;
with(JavaImporter(org.icescene.io, com.jme3.input.controls, com.jme3.input)) {
	__KeyMaps.Recorder = {
		    trigger : new KeyTrigger(KeyInput.KEY_F12),
		    category : "Debug"
	};
	__KeyMaps.Reset = {
		    trigger: new KeyTrigger(KeyInput.KEY_F11),
		    category: "Debug"
	};
	__KeyMaps.EngineDebug = {
	    trigger : new KeyTrigger(KeyInput.KEY_F9),
	    category: "Debug"
	};
	__KeyMaps.AppStateDump = {
	    trigger: new KeyTrigger(KeyInput.KEY_F10),
	    category: "Debug"
	};
	__KeyMaps.GUIExplorer = {
	    trigger: new KeyTrigger(KeyInput.KEY_F8),
	    category: "Debug"
	};
	__KeyMaps.FLYCAM_Left = {
	    trigger: new KeyTrigger(KeyInput.KEY_LEFT),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_Right = {
	    trigger: new KeyTrigger(KeyInput.KEY_RIGHT),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_Up = {
	    trigger: new KeyTrigger(KeyInput.KEY_UP),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_Down = {
	    trigger: new KeyTrigger(KeyInput.KEY_DOWN),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_ZoomIn = {
	    trigger: new KeyTrigger(KeyInput.KEY_PGUP),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_ZoomOut = {
	    trigger: new KeyTrigger(KeyInput.KEY_PGDN),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_StrafeLeft = {
	    trigger: new KeyTrigger(KeyInput.KEY_A),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_StrafeRight = {
	    trigger: new KeyTrigger(KeyInput.KEY_D),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_Forward = {
	    trigger: new KeyTrigger(KeyInput.KEY_W),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_Backward = {
	    trigger: new KeyTrigger(KeyInput.KEY_S),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_Rise = {
	    trigger: new KeyTrigger(KeyInput.KEY_Q),
	    category: "Camera"
	};
	__KeyMaps.FLYCAM_Lower = {
	    trigger: new KeyTrigger(KeyInput.KEY_Z),
	    category: "Camera"
	};
}