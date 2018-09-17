package org.icescene.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.commons.io.FilenameUtils;
import org.icescene.audio.AudioField;
import org.icescene.audio.AudioQueue;
import org.iceui.controls.ChooserFieldControl.ChooserPathTranslater;
import org.iceui.controls.SoundFieldControl.Type;

import icetone.controls.buttons.PushButton;
import icetone.controls.table.Table;
import icetone.controls.table.TableRow;
import icetone.core.BaseScreen;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserModel;

public class Playlist extends Element {
	private AudioField audio;
	private PushButton deleteItem;
	private Table items;
	private PushButton upItem;
	private PushButton downItem;
	private PushButton newItem;
	private int row = -1;

	public Playlist(BaseScreen screen, Type type, AudioQueue queue, Preferences prefs,
			ChooserModel<String> soundResources) {
		super(screen);

		audio = new AudioField(screen, type, null, soundResources, prefs, queue) {
			@Override
			protected void createChooserButton() {
				chooserButton = new PushButton(screen) {
					{
						setStyleClass("fancy");
					}
				};
				chooserButton.onMouseReleased(evt -> showChooser(evt.getX(), evt.getY()));
				chooserButton.getButtonIcon().setStyleClass("icon icon-edit");
				chooserButton.setToolTipText("Edit Item");
				addElement(chooserButton, "wrap");

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
		newItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		newItem.onMouseReleased(evt -> {
			row = -1;
			audio.setValue("");
			audio.showChooser(evt.getX(), evt.getY());
			setAvailable();
		});
		newItem.getButtonIcon().setStyleClass("icon icon-new");
		newItem.setToolTipText("Add New Item");

		deleteItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		deleteItem.onMouseReleased(evt -> {
			String sel = items.isAnythingSelected() ? (String) items.getSelectedRow().getValue() : null;
			if (sel != null) {
				List<String> a = getAudio();
				a.remove(sel);
				setAudio(a);
			}
			onAudioChanged(getAudio());
		});
		deleteItem.getButtonIcon().setStyleClass("icon icon-trash");
		deleteItem.setToolTipText("Delete Item");

		upItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		upItem.onMouseReleased(evt -> {
			int idx = Math.max(0, items.getSelectedRowIndex() - 1);
			TableRow row = items.getSelectedRow();
			items.removeRow(row);
			items.insertRow(idx, row);
			items.setSelectedRowIndex(idx);
			items.scrollToSelected();
			onAudioChanged(getAudio());
		});
		upItem.getButtonIcon().setStyleClass("icon icon-up");
		upItem.setToolTipText("Move Item Up");

		downItem = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		downItem.onMouseReleased(evt -> {
			int idx = items.getSelectedRowIndex() + 1;
			TableRow row = items.getSelectedRow();
			items.removeRow(row);
			items.insertRow(idx, row);
			items.setSelectedRowIndex(idx);
			items.scrollToSelected();
			onAudioChanged(getAudio());
		});
		downItem.getButtonIcon().setStyleClass("icon icon-down");
		downItem.setToolTipText("Move Item Up");

		StyledContainer tools = new StyledContainer(screen);
		tools.setLayoutManager(new MigLayout(screen, "wrap 1, ins 0, fill", "[grow]", "[][][][]push"));
		tools.addElement(newItem, "growx");
		tools.addElement(deleteItem, "growx");
		tools.addElement(upItem, "growx");
		tools.addElement(downItem, "growx");

		items = new Table(screen);
		items.onChanged(evt -> {
			TableRow selectedRow = evt.getSource().getSelectedRow();
			audio.setValue(selectedRow == null ? "" : selectedRow.getValue().toString());
			row = evt.getSource().getSelectedRowIndex();
			setAvailable();
			onSelectionChanged(audio.getValue());
		});
		items.setHeadersVisible(false);
		items.addColumn("Sound");

		setLayoutManager(new MigLayout(screen, "wrap 2", "[grow, fill][]", "[:32:][:128:]"));
		addElement(audio, "span 2, growx");
		addElement(items, "growx, growy");
		addElement(tools, "growy, growx");

		setAvailable();
	}

	public void setChooserPathTranslater(ChooserPathTranslater<String> chooserPathTranslater) {
		audio.setChooserPathTranslater(chooserPathTranslater);
	}

	public void setResources(ChooserModel<String> resources) {
		audio.setResources(resources);
	}

	public void setAudio(List<String> audio) {
		items.removeAllRows();
		for (String r : audio) {
			addChoice(r);
		}
		items.scrollToTop();
	}

	public String getSelected() {
		return items.isAnythingSelected() ? (String) items.getSelectedRow().getValue() : null;
	}

	public List<String> getAudio() {
		List<String> a = new ArrayList<>();
		for (TableRow r : items.getRows()) {
			a.add((String) r.getValue());
		}
		return a;
	}

	protected void onSelectionChanged(String path) {
	}

	protected void onAudioChanged(List<String> audio) {
	}

	private void setAvailable() {
		audio.getChooserButton().setEnabled(items.isAnythingSelected());
		deleteItem.setEnabled(items.isAnythingSelected());
		upItem.setEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() > 0);
		downItem.setEnabled(items.isAnythingSelected() && items.getSelectedRowIndex() < items.getRowCount() - 1);
	}

	private void addChoice(String r) {
		TableRow row = new TableRow(screen, items, r);
		row.addCell(r, r);
		items.addRow(row);
	}
}