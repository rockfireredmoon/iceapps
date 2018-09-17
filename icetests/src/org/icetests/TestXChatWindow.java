package org.icetests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.icelib.ChannelType;
import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.iceui.ChatChannel;
import org.iceui.IceUI;
import org.iceui.XChatBox;
import org.iceui.XChatWindow;
import org.iceui.controls.ElementStyle;

import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icemoon.iceloader.ServerAssetManager;
import icetone.controls.buttons.SelectableItem;
import icetone.controls.containers.Panel;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.layout.mig.MigLayout;

public class TestXChatWindow extends IcesceneApp {

	public static void main(String[] args) {
		TestXChatWindow app = new TestXChatWindow();
		app.start();
	}

	private Spinner<Integer> sp;
	private ComboBox<String> f;
	private XChatWindow xcw;

	public TestXChatWindow() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		final Preferences tabs = Preferences.userRoot().node("testTabs");
		final String tabNamesString = tabs.get("tabNames", "");
		final List<String> tabNames = tabNamesString.isEmpty() ? new ArrayList<String>()
				: new ArrayList<String>(Arrays.asList(tabNamesString.split("\n")));

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Panel for actions and character selection
		xcw = new XChatWindow("Chat", screen, Preferences.userRoot().node("testchat")) {
			@Override
			public void onSendChatMsg(XChatBox tab, Object o, String text) {
				super.onSendChatMsg(tab, o, text);
				ChannelType currentChannel = (ChannelType) o;
				if (text.startsWith("/")) {
					int sp = text.indexOf(' ');
					String chan = text.substring(1);
					if (sp != -1) {
						chan = text.substring(1, sp);
						text = text.substring(sp + 1);
					} else
						text = "";
					try {
						setChannelByCommand(currentChannel = ChannelType.valueOf(chan.toUpperCase()));
						System.out.println("Channell now " + currentChannel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (text.length() > 0) {
					System.out.println("On send: " + text);
					receiveMsg("Player", null, currentChannel, text);
				}
			}

			protected ColorRGBA getColorForChannelCommand(XChatBox tab, Object command) {
				return IceUI.toRGBA(((ChannelType) command).getColor());
			}

			@Override
			protected void configureChannel(XChatBox tab, ChatChannel channel) {
				Preferences channelPrefs = tabs.node(tab.getTabName()).node(channel.getName());
				channel.setIsFiltered(channelPrefs.getBoolean("filtered", channel.getIsFiltered()));
			}

			@Override
			protected void channelChanged(XChatBox tab, ChatChannel channel) {
				super.channelChanged(tab, channel);
				Preferences tabPrefs = tabs.node(tab.getTabName());
				writeChannels(tabPrefs, tab);
			}

			@Override
			protected void renameChatTab(int index, String name) {
				super.renameChatTab(index, name);
				String oldName = tabNames.set(index - 1, name);
				Preferences tabPrefs = tabs.node(oldName);
				try {
					tabPrefs.removeNode();
					tabPrefs = tabs.node(name);
					writeChannels(tabPrefs, getChatTab(index));
					tabs.put("tabNames", Icelib.toSeparatedList(tabNames, "\n"));
				} catch (BackingStoreException bse) {
					bse.printStackTrace();
				}
			}

			void writeChannels(Preferences prefs, XChatBox cb) {
				for (ChatChannel c : cb.getChannels()) {
					Preferences cn = prefs.node(c.getName());
					cn.putBoolean("filtered", c.getIsFiltered());
				}
			}

			@Override
			protected void deleteChatTab(int index) {
				super.deleteChatTab(index);
				tabNames.remove(index - 1);
				tabs.put("tabNames", Icelib.toSeparatedList(tabNames, "\n"));
			}

			@Override
			protected void newChatTab(String name) {
				super.newChatTab(name);
				tabNames.add(name);
				tabs.put("tabNames", Icelib.toSeparatedList(tabNames, "\n"));
			}
		};
		// xcw.setChatFontSize(10);
		xcw.setSendKey(KeyInput.KEY_RETURN);
		for (ChannelType t : ChannelType.values()) {
			xcw.addChatChannel(t.name(), Icelib.toEnglish(t), t, Icelib.toEnglish(t), true);
		}

		for (String tab : tabNames) {
			xcw.addTab(tab);
		}

		// for (int i = 0; i < 3; i++) {
		// xcw.receiveMsg("Someone", null, ChannelType.REGION, "Test line " +
		// i);
		// }
		// xcw.addTab("Tab 3");
		// xcw.addTab("Tab 4");
		// xcw.addTab("Tab 5");

		// Add window to screen (causes a layout)
		screen.addElement(xcw);

		//

		Panel p = new Panel(screen);
		p.setLayoutManager(new MigLayout(screen, "wrap 2", "[][fill, grow]", "[]"));
		// Font
		p.addElement(new Label("Font", screen));
		f = new ComboBox<String>(screen);
		for (String fntName : ((ServerAssetManager) assetManager).getAssetNamesMatching(".*\\.fnt")) {
			f.addListItem(Icelib.getBaseFilename(fntName), fntName);
		}
		if (xcw.getChatFont() != null)
			f.setSelectedByValue(xcw.getChatFont());
		f.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				setFontOnScroller(evt.getNewValue());
				f.setToolTipText(evt.getNewValue());
			}
		});
		p.addElement(f);

		// Font size
		p.addElement(new Label("Font Size", screen));
		sp = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		sp.setSpinnerModel(new IntegerRangeSpinnerModel(1, 64, 1, 8));
		sp.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				setFontSizeOnScroller(evt.getNewValue());
			}
		});
		p.addElement(sp, "");

		// Alpha
		p.addElement(new Label("Alpha", screen));
		Spinner<Float> spf = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		spf.setSpinnerModel(new FloatRangeSpinnerModel(0, 1, 0.1f, 1f));
		spf.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				setAlphaOnScroller(evt.getNewValue());
			}
		});
		p.addElement(spf, "");

		p.sizeToContent();
		screen.addElement(p);

		xcw.show();

	}

	private void setFontOnScroller(String path) {
		xcw.setChatFont(path);
	}

	private void setFontSizeOnScroller(int value) {
		xcw.setChatFontSize(value);
	}

	private void setAlphaOnScroller(float value) {
		xcw.setLocalAlpha(value);
	}

	SelectableItem createPanel(BaseScreen screen, String n) {
		SelectableItem p = new SelectableItem(screen);
		p.setIgnoreMouse(true);
		MigLayout l = new MigLayout(screen, "ins 0, wrap 1, fill", "[grow, fill]", "");
		p.setLayoutManager(l);

		Label l1 = new Label(screen, "Label1" + n);
		l1.setText(n);
		l1.setIgnoreMouse(true);
		ElementStyle.medium(l1);
		p.addElement(l1);

		Label l2 = new Label(screen, "Label1X" + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		l2.setText("Label 2 for " + n);
		l2.setIgnoreMouse(true);
		p.addElement(l2);
		return p;
	}

}
