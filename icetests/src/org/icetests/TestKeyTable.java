package org.icetests;


import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.options.KeyTable;

import icetone.controls.containers.Panel;
import icetone.core.layout.BorderLayout;

public class TestKeyTable extends IcesceneApp {

	static {
		AppInfo.context = TestKeyTable.class;
	}

	public static void main(String[] args) {
		TestKeyTable app = new TestKeyTable();
		app.start();
	}

	public TestKeyTable() {
		setUseUI(true);
	}

	protected void onInitialize() {
		
		Panel panel = new Panel(new BorderLayout());
		panel.addElement(new KeyTable(screen, getKeyMapManager()).reloadKeys());
		screen.showElement(panel);

	}

}
