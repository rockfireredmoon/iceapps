package org.icescene.ui;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.controls.Rotator;
import org.icescene.props.AbstractProp;
import org.icescene.props.EntityFactory;
import org.iceui.controls.chooser.ChooserDialog;
import org.iceui.controls.chooser.ChooserPanel;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.extras.OSRViewPort;
import icetone.controls.lists.Table;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.listeners.MouseButtonListener;

/**
 * {@link ChooserDialog.ChooserView} that lists models resources as names, and
 * previews them in an {@link OSRViewPort}.
 */
public class PreviewModelView implements ChooserPanel.ChooserView {
	public final class PreviewTable extends Table implements MouseButtonListener {
		private final ChooserPanel chooser;
		private final Vector2f vpSize;

		public PreviewTable(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
				ChooserPanel chooser, Vector2f vpSize) {
			super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
			this.chooser = chooser;
			this.vpSize = vpSize;
		}

		@Override
		public void onChange() {
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
					al.setColor(ColorRGBA.White.mult(3));
					n.addLight(al);
					n.attachChild(prop.getSpatial());

					vp.setOSRBridge(n, (int) vpSize.x, (int) vpSize.y);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Failed to load.", e);
				}

			}
		}

		@Override
		public void onMouseLeftReleased(MouseButtonEvent evt) {
			TableRow row = getSelectedRow();
			if (row != null && LUtil.isDoubleClick(evt)) {
				chooser.choose(getPath((String) row.getCell(0).getValue()));
			}
		}

		@Override
		public void onMouseLeftPressed(MouseButtonEvent evt) {
		}

		@Override
		public void onMouseRightPressed(MouseButtonEvent evt) {
		}

		@Override
		public void onMouseRightReleased(MouseButtonEvent evt) {
		}
	}

	final static Logger LOG = Logger.getLogger(PreviewModelView.class.getName());

	private final ElementManager screen;
	private String cwd;
	private Table table;
	private OSRViewPort vp;
	private final EntityFactory propFactory;
	private AbstractProp prop;
	private ButtonAdapter rotateLeft;
	private ButtonAdapter rotateRight;

	public PreviewModelView(ElementManager screen, EntityFactory propFactory) {
		this.screen = screen;
		this.propFactory = propFactory;
	}

	public void setEnabled(boolean enabled) {
		table.setIsEnabled(enabled);
		rotateLeft.setIsEnabled(enabled);
		rotateRight.setIsEnabled(enabled);
	}

	public Element createView(final ChooserPanel chooser) {

		final Vector2f vpSize = new Vector2f(200, 200);
		Element container = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("Menu").getVector4f("resizeBorders"), screen.getStyle("Menu").getString("defaultImg"));
		container.setIgnoreMouse(true);
		container.setLayoutManager(new MigLayout(screen, "", "[fill, grow,:50%:][fill, grow,:50%:]", "[fill, grow]"));

		table = new PreviewTable(screen, UIDUtil.getUID(), Vector2f.ZERO, Vector4f.ZERO, null, chooser, vpSize);
		table.setSelectionMode(Table.SelectionMode.ROW);
		table.setHeadersVisible(false);
		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);
		table.addColumn("Name");
		container.addChild(table);

		// Preview panel
		Container previewContainer = new Container(screen);
		previewContainer.setLayoutManager(new MigLayout(screen, "gap 0, ins 0, wrap 1", "push[]push", "push[shrink 200][]push"));
		vp = new OSRViewPort(screen, Vector2f.ZERO, vpSize, Vector4f.ZERO, null) {
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
		vp.setDocking(null);
		vp.setIgnoreMouseWheel(false);
		vp.setIgnoreMouse(false);
		vp.setIgnoreMouseButtons(false);
		vp.setScaleEW(false);
		vp.setScaleNS(false);
		previewContainer.addChild(vp);

		Container buttonsContainer = new Container(screen);
		buttonsContainer.setLayoutManager(new FlowLayout(0, BitmapFont.Align.Center));

		rotateLeft = new ButtonAdapter(screen, screen.getStyle("RotateLeftButton").getVector2f("defaultSize")) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (prop != null) {
					prop.getSpatial().addControl(new Rotator(-3f));
				}
			}

			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (prop != null) {
					prop.getSpatial().removeControl(Rotator.class);
				}
			}
		};
		rotateLeft.setStyles("RotateLeftButton");
		buttonsContainer.addChild(rotateLeft);

		rotateRight = new ButtonAdapter(screen, screen.getStyle("RotateRightButton").getVector2f("defaultSize")) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (prop != null) {
					prop.getSpatial().addControl(new Rotator(3f));
				}
			}

			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (prop != null) {
					prop.getSpatial().removeControl(Rotator.class);
				}
			}
		};
		rotateRight.setStyles("RotateRightButton");
		buttonsContainer.addChild(rotateRight);

		previewContainer.addChild(buttonsContainer, "ax 50%");

		container.addChild(previewContainer);

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
			final Table.TableRow tableRow = new Table.TableRow(screen, table);
			screen.getApplication().enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					tableRow.addCell(s, s);
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
