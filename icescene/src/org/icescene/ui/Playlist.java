package org.icescene.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.commons.io.FilenameUtils;
import org.icescene.audio.AudioField;
import org.icescene.audio.AudioQueue;
import org.iceui.controls.ChooserFieldControl.ChooserPathTranslater;
import org.iceui.controls.FancyButton;
import org.iceui.controls.SoundFieldControl.Type;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.lists.Table;
import icetone.controls.lists.Table.TableRow;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;

public class Playlist extends Element {
	private AudioField audio;
	private FancyButton deleteItem;
	private Table items;
	private FancyButton upItem;
	private FancyButton downItem;
	private FancyButton newItem;
	private int row = -1;

	public Playlist(ElementManager screen, Type type, AudioQueue queue, Preferences prefs, Set<String> soundResources) {
		super(screen);

		audio = new AudioField(screen, type, null, soundResources, prefs, queue) {
			@Override
			protected void createChooserButton() {
				chooserButton = new FancyButton(screen) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						showChooser(evt.getX(), evt.getY());
					}
				};
				chooserButton.getMinDimensions().x = 64;
				chooserButton.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Icons/edit.png");
				chooserButton.setToolTipText("Edit Item");
				addChild(chooserButton, "wrap");

			}

			@Override
			protected void onResourceChosen(String newResource) {
				List<String> existing = getAudio();
				String name = FilenameUtils.getName(newResource);
				if (row == -1) {
					if (!existing.contains(name))
						addChoice(name);
				} else {
					items.getRow(row).setValue(newResource);
					row = 1;
				}
				onAudioChanged(getAudio());
				setAvailable();
			}
		};
		newItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				row = -1;
				audio.setValue("");
				audio.showChooser(evt.getX(), evt.getY());
				setAvailable();
			}
		};
		newItem.getMinDimensions().x = 64;
		newItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Icons/new.png");
		newItem.setToolTipText("Add New Item");

		deleteItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				String sel = items.isAnythingSelected() ? (String)items.getSelectedRow().getValue() : null;
				if(sel != null) {
					List<String> a = getAudio();
					a.remove(sel);
					setAudio(a);
				}
				onAudioChanged(getAudio());
			}
		};
		deleteItem.getMinDimensions().x = 64;
		deleteItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Icons/trash.png");
		deleteItem.setToolTipText("Delete Item");

		upItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				int idx = Math.max(0, items.getSelectedRowIndex() - 1);
				TableRow row = items.getSelectedRow();
				items.removeRow(row);
				items.insertRow(idx, row);
				items.setSelectedRowIndex(idx);
				items.scrollToSelected();
				onAudioChanged(getAudio());
			}
		};
		upItem.getMinDimensions().x = 64;
		upItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Arrows/arrow_up.png");
		upItem.setToolTipText("Move Item Up");

		downItem = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				int idx = items.getSelectedRowIndex() + 1;
				TableRow row = items.getSelectedRow();
				items.removeRow(row);
				items.insertRow(idx, row);
				items.setSelectedRowIndex(idx);
				items.scrollToSelected();
				onAudioChanged(getAudio());
			}
		};
		downItem.getMinDimensions().x = 64;
		downItem.setButtonIcon(16, 16, "Interface/Styles/Gold/Common/Arrows/arrow_down.png");
		downItem.setToolTipText("Move Item Up");

		Container tools = new Container(screen);
		tools.setLayoutManager(new MigLayout(screen, "wrap 1, ins 0, fill", "[grow]", "[][][][]push"));
		tools.addChild(newItem, "growx");
		tools.addChild(deleteItem, "growx");
		tools.addChild(upItem, "growx");
		tools.addChild(downItem, "growx");

		items = new Table(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("TextField").getVector4f("resizeBorders"), screen.getStyle(
				"TextField").getString("defaultImg")) {
			@Override
			public void onChange() {
				TableRow selectedRow = getSelectedRow();
				audio.setValue(selectedRow == null ? "" : selectedRow.getValue().toString());
				row = getSelectedRowIndex();
				setAvailable();
			}
		};
		items.setHeadersVisible(false);
		items.addColumn("Sound");

		setLayoutManager(new MigLayout(screen, "wrap 2", "[grow, fill][]", "[:32:][:128:]"));
		addChild(audio, "span 2, growx");
		addChild(items, "growx, growy");
		addChild(tools, "growy, growx");

		setAvailable();
	}

	public void setChooserPathTranslater(ChooserPathTranslater chooserPathTranslater) {
		audio.setChooserPathTranslater(chooserPathTranslater);
	}

	public void setResources(Set<String> resources) {
		audio.setResources(resources);
	}

	public void setAudio(List<String> audio) {
		items.removeAllRows();
		for (String r : audio) {
			addChoice(r);
		}
		items.scrollToTop();
	}

	public List<String> getAudio() {
		List<String> a = new ArrayList<>();
		for (TableRow r : items.getRows()) {
			a.add((String) r.getValue());
		}
		return a;
	}
	
	protected void onAudioChanged(List<String> audio) {
	}

	private void setAvailable() {
		audio.getChooserButton().setIsEnabled(items.isAnythingSelected());
		deleteItem.setIsEnabled(items.isAnythingSelected());
		upItem.setIsEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() > 0);
		downItem.setIsEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() < items.getRowCount() - 1);
	}

	private void addChoice(String r) {
		TableRow row = new TableRow(screen, items, r);
		row.addCell(r, r);
		items.addRow(row);
	}
}