package org.icescene.ui;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Icelib;
import org.icescene.controls.Rotator;
import org.icescene.props.AbstractProp;
import org.icescene.props.EntityFactory;

import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.OSRViewPort;
import icetone.controls.table.Table;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserDialog;
import icetone.extras.chooser.ChooserPanel;
import icetone.fontawesome.FontAwesome;

/**
 * {@link ChooserDialog.ChooserView} that lists models resources as names, and
 * previews them in an {@link OSRViewPort}.
 */
public class PreviewModelView implements ChooserPanel.ChooserView<String> {
	public final class PreviewTable extends Table {

		public PreviewTable(BaseScreen screen, ChooserPanel<String> chooser, Size vpSize) {
			super(screen);
			setPreferredDimensions(vpSize);
			onMouseReleased(evt -> {
				TableRow row = getSelectedRow();
				if (row != null && evt.getClicks() == 2) {
					chooser.choose(getPath((String) row.getCell(0).getValue()));
				}
			});
			onChanged(evt -> {

				TableRow row = getSelectedRow();
				if (row == null) {
					vp.setOSRBridge(new Node(), (int) vpSize.x, (int) vpSize.y);
				} else {
					TableCell cell = row.getCell(0);
					String propName = String.format("%s/%s", cwd, (String) cell.getValue());

					try {
						prop = propFactory.getPropForResourcePath(propName);
						prop.getSpatial().rotate(0, -FastMath.HALF_PI, 0);
						prop.getSpatial().move(0, -0.5f, 0);
						prop.getSpatial().scale(0.25f);

						Node n = new Node();
						AmbientLight al = new AmbientLight();
						al.setColor(ColorRGBA.White);
						n.addLight(al);
						n.attachChild(prop.getSpatial());

						vp.setOSRBridge(n, (int) vpSize.x, (int) vpSize.y);
					} catch (IOException e) {
						LOG.log(Level.SEVERE, "Failed to load.", e);
					}

				}
			});
		}

	}

	final static Logger LOG = Logger.getLogger(PreviewModelView.class.getName());

	private final BaseScreen screen;
	private String cwd;
	private Table table;
	private OSRViewPort vp;
	private final EntityFactory propFactory;
	private AbstractProp prop;
	private Button rotateLeft;
	private Button rotateRight;

	public PreviewModelView(BaseScreen screen, EntityFactory propFactory) {
		this.screen = screen;
		this.propFactory = propFactory;
	}

	public void setIsEnabled(boolean enabled) {
		table.setEnabled(enabled);
		rotateLeft.setEnabled(enabled);
		rotateRight.setEnabled(enabled);
	}

	public BaseElement createView(final ChooserPanel<String> chooser) {

		final Size vpSize = new Size(200, 200);

		Element container = new Element(screen);
		container.setIgnoreMouse(true);
		container.setLayoutManager(new MigLayout(screen, "", "[fill, grow,:50%:][fill, grow,:50%:]", "[fill, grow]"));

		table = new PreviewTable(screen, chooser, vpSize);
		table.setSelectionMode(Table.SelectionMode.ROW);
		table.setHeadersVisible(false);
		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);
		table.addColumn("Name");
		container.addElement(table);

		// Preview panel
		Element previewContainer = new Element(screen);
		previewContainer.setLayoutManager(
				new MigLayout(screen, "gap 0, ins 0, wrap 1", "push[]push", "push[shrink 200][]push"));
		vp = new OSRViewPort(screen, vpSize) {
			@Override
			public void controlHideHook() {
				if (getOSRBridge() != null && getOSRBridge().getViewPort() != null) {
					super.controlHideHook();
				}
			}

			@Override
			public void controlShowHook() {
				if (getOSRBridge() != null && getOSRBridge().getViewPort() != null) {
					super.controlShowHook();
				}
			}
		};
		vp.setIgnoreMouseWheel(false);
		vp.setIgnoreMouse(false);
		vp.setIgnoreMouseButtons(false);
		previewContainer.addElement(vp);

		StyledContainer buttonsContainer = new StyledContainer(screen);
		buttonsContainer.setLayoutManager(new FlowLayout(0, BitmapFont.Align.Center));

		rotateLeft = new PushButton(screen);
		FontAwesome.ROTATE_LEFT.button(24, rotateLeft);
		rotateLeft.onMousePressed(evt -> {
			if (prop != null) {
				prop.getSpatial().addControl(new Rotator(-3f));
			}
		});
		rotateLeft.setStyleClass("rotate-left");
		rotateLeft.onMouseReleased(evt -> {
			if (prop != null) {
				prop.getSpatial().removeControl(Rotator.class);
			}
		});
		buttonsContainer.addElement(rotateLeft);

		rotateRight = new PushButton(screen);
		FontAwesome.ROTATE_RIGHT.button(24, rotateRight);
		rotateRight.setStyleClass("rotate-right");
		rotateRight.onMousePressed(evt -> {
			if (prop != null) {
				prop.getSpatial().addControl(new Rotator(3f));
			}
		});
		rotateRight.onMouseReleased(evt -> {
			if (prop != null) {
				prop.getSpatial().removeControl(Rotator.class);
			}
		});
		buttonsContainer.addElement(rotateRight);

		previewContainer.addElement(buttonsContainer, "ax 50%");

		container.addElement(previewContainer);

		return container;
	}

	public void rebuild(String cwd, Collection<String> filesNames) {
		this.cwd = cwd;
		screen.getApplication().enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				table.removeAllRows();
				return null;
			}
		});

		for (final String s : filesNames) {
			final TableRow tableRow = new TableRow(screen, table);
			screen.getApplication().enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					tableRow.addCell(Icelib.getBaseFilename(s), s);
					table.addRow(tableRow);
					return null;
				}
			});
		}
	}

	public void select(String file) {
		// for (Map.Entry<String, ImageSelect> en : images.entrySet()) {
		// if (en.getKey().equals(file)) {
		// en.getValue().setSelected(true);
		// break;
		// } else {
		// en.getValue().setSelected(false);
		// }
		// }
	}

	private String getPath(String s) {
		final String path = cwd == null ? s : (cwd + "/" + s);
		return path;
	}
}
