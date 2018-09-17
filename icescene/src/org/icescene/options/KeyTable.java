package org.icescene.options;

import org.icescene.io.KeyMapManager;
import org.icescene.io.UserKeyMapping;

import com.jme3.math.Vector2f;

import icetone.controls.table.Table;
import icetone.controls.table.TableRow;
import icetone.core.BaseScreen;
import icetone.core.Measurement.Unit;
import icetone.core.Size;

public class KeyTable extends Table {

	private KeyMapManager keyMapManager;

	public KeyTable(BaseScreen screen, KeyMapManager keyMapManager) {
		super(screen);
		this.keyMapManager = keyMapManager;
		setColumnResizeMode(ColumnResizeMode.AUTO_LAST);
		setHeadersVisible(true);
		setSelectionMode(SelectionMode.ROW);
		addColumn("Category").setPreferredDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));
		addColumn("Mapping").setPreferredDimensions(new Size(160, 0, Unit.PX, Unit.AUTO));
		addColumn("Key");

	}

	@Override
	public Vector2f calcPreferredSize() {
		// TODO Auto-generated method stub
		return super.calcPreferredSize();
	}

	public KeyTable reloadKeys() {
		invalidate();
		removeAllRows();
		for (String category : keyMapManager.getCategories()) {
			TableRow row = new TableRow(screen, this);
			row.setLeaf(false);
			row.addCell(category, category);
			row.addCell("", "");
			row.addCell("", "");
			addRow(row);
			row.setExpanded(true);
			for (UserKeyMapping m : keyMapManager.getMappings(category)) {
				TableRow krow = new TableRow(screen, this, m);
				krow.addCell("", "");
				krow.addCell(m.getSource().getMapping(), m.getSource().getMapping());
				krow.addCell(m.getDescription(), m.getTrigger());
				row.addRow(krow, false);
			}
		}
		validate();
		return this;
	}
}
