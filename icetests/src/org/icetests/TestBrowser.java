package org.icetests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.FancyWindow;
import org.xhtmlrenderer.event.DocumentListener;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.util.XRLog;

import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.controls.windows.AlertBox;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;
import icetone.xhtml.NaiveUserAgent;
import icetone.xhtml.TGGXHTMLRenderer;

/**
 * A simple browser based on {@link TGGXHTMLRenderer}, serving as an example as
 * to how to
 * use the component.
 *
 * @author rockfire
 */
public class TestBrowser extends IcesceneApp {

	public final static String HOME = TestBrowser.class.getResource("/demos/splash/splash2.html").toString();
	private final static LinkedHashMap<String, String> demoFiles = new LinkedHashMap<String, String>();

	static {
		AppInfo.context = TestBrowser.class;
		// Read in the demo file list if it is there
		InputStream in = TestBrowser.class.getResourceAsStream("/demos/file-list.txt");
		if (in != null) {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				String l;
				try {
					while ((l = r.readLine()) != null) {
						int idx = l.indexOf(",");
						demoFiles.put(l.substring(0, idx), l.substring(idx + 1));
					}
				} finally {
					r.close();
				}
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}

	public static void main(String[] args) {
		TestBrowser app = new TestBrowser();
		XRLog.setLoggingEnabled(false);
		app.start();
	}

	private TGGXHTMLRenderer xhtml;
	private Stack<String> history = new Stack<String>();
	private Stack<String> future = new Stack<String>();
	private Label status;

	public TestBrowser() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

		if (System.getProperty("tg.record") != null) {
			File dir = new File(System.getProperty("user.home"));
			File recordingFile = new File(dir, new SimpleDateFormat("ddMMyy-HHmmss").format(new Date()) + ".mjpeg");
			stateManager.attach(new VideoRecorderAppState(recordingFile));
		}

		// XRLog.setLoggerImpl(new XRLogger() {
		// public void log(String string, Level level, String string1) {
		// System.err.println("FS: " + level + " : " + string1);
		// }
		//
		// public void log(String string, Level level, String string1, Throwable
		// thrwbl) {
		// System.err.println("FS: " + level + " : " + string1);
		// if (thrwbl != null)
		// thrwbl.printStackTrace();
		// }
		//
		// public void setLevel(String string, Level level) {
		// }
		// });

		flyCam.setDragToRotate(true);

		// Window 1
		final FancyWindow window1 = new FancyWindow(screen, FancyWindow.Size.LARGE, false);
		window1.setTitle("Layout Aware XHTML Renderer");
		window1.getContentArea()
				.setLayoutManager(new MigLayout(screen, "wrap 1", "[fill, grow]", "[shrink 0][fill, grow][shrink 0]"));

		// Tools
		Element c = new Element(new MigLayout(screen, "fill", "[shrink 0][shrink 0][shrink 0][grow][shrink 0]", "[]"));

		// Back
		Button back = new ButtonAdapter() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (!history.isEmpty()) {
					String uri = history.pop();
					future.push(uri);
					xhtml.setDocument(uri);
				}
			}
		};
		back.setStyles("FancyButton");
		back.setToolTipText("Back");
		back.setButtonIcon(screen.getStyle("Common").getString("arrowLeft"));
		c.addChild(back);

		// Forward
		Button fwd = new ButtonAdapter() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (!future.isEmpty()) {
					String uri = future.pop();
					history.push(uri);
					xhtml.setDocument(uri);
				}
			}
		};
		fwd.setStyles("FancyButton");
		fwd.setToolTipText("Forward");
		fwd.setButtonIcon(screen.getStyle("Common").getString("arrowRight"));
		c.addChild(fwd);

		// Home
		Button home = new ButtonAdapter() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				history.push(HOME);
				future.clear();
				xhtml.setDocument(HOME);
			}
		};
		home.setStyles("FancyButton");
		home.setToolTipText("Home");
		home.setButtonIcon(screen.getStyle("Common").getString("arrowUp"));
		c.addChild(home);

		// Forward
		final TextField address = new TextField() {
			@Override
			public void onKeyRelease(KeyInputEvent evt) {
				if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
					xhtml.setDocument(getText());
				}
			}
		};
		c.addChild(address, "growx");

		// Reload
		Button reload = new ButtonAdapter("R") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				System.err.println("history; " + history);
				if (history.size() > 0) {
					xhtml.setDocument(history.peek());
				}
			}
		};
		reload.setStyles("FancyButton");
		reload.setToolTipText("Reload");
		c.addChild(reload);

		// The XHTML renderer itself
		xhtml = new TGGXHTMLRenderer(screen, screen.getStyle("XHTML").getVector4f("resizeBorders"),
				screen.getStyle("XHTML").getString("defaultImg"), new NaiveUserAgent(screen)) {
			private int currentDemo;

			@Override
			protected void onFormSubmit(XhtmlForm form) {
				super.onFormSubmit(form);

				// You can capture form submissions
				AlertBox ab = new AlertBox(screen) {
					@Override
					public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
					}
				};
				StringBuffer mesg = new StringBuffer("Form Submitted");
				for (Iterator<FormControl> fcIt = form.getControls(); fcIt.hasNext();) {
					FormControl c = fcIt.next();
					mesg.append("\n   ");
					mesg.append(c.getName());
					mesg.append(" = ");
					mesg.append(c.getValue());
				}
				ab.setWindowTitle("Form Submission!");
				ab.setMsg(mesg.toString());
				screen.addElement(ab);
			}

			@Override
			protected void onFormReset(XhtmlForm form) {
				super.onFormReset(form); // To change body of generated methods,
										 // choose Tools | Templates.
			}

			@Override
			protected void onHover(org.w3c.dom.Element el) {
				// Override this method to get notified when a link is hovered
				// over

				if (el != null) {
					Link uri = findLink(el);
					if (uri != null) {
						status.setText(uri.getUri());
					} else {
						status.setText("");
					}
				} else {
					status.setText("");
				}
			}

			@Override
			protected void linkClicked(Link uri) {
				// Override this method for special handling of clicked links.
				// For example,
				// here you can intercept the URI and deal with special schemes.
				try {
					URI uriObject = new URI(uri.getUri());
					if (uriObject.getScheme() != null) {
						if (uriObject.getScheme().equals("demoNav") && !demoFiles.isEmpty()) {
							List<String> demoList = new ArrayList<String>(demoFiles.keySet());

							if (uriObject.getSchemeSpecificPart().equals("forward")) {
								currentDemo++;
								if (currentDemo >= demoList.size()) {
									currentDemo = 0;
								}
							} else if (uriObject.getSchemeSpecificPart().equals("back")) {
								currentDemo--;
								if (currentDemo < 0) {
									currentDemo = demoList.size() - 1;
								}

							} else {
								throw new Exception("Unknown demo URI.");
							}

							String resourcePath = demoFiles.get(demoList.get(currentDemo));
							URL cpUrl = getClass().getResource("/" + resourcePath.substring(5));
							uri = new Link(cpUrl.toURI().toString());
						}
					}
					super.linkClicked(uri);
					history.push(uri.getUri());
					future.clear();
				} catch (Exception e) {
					errorPage(e);
				}
			}
		};
		// xhtml.setContentIndents(screen.getStyle("XHTML").getVector4f("contentIndents"));
		xhtml.setUseContentPaging(false);

		// TODO fix bg / fg ( see splash.html)
		// xhtml.setForegroundColor(ColorRGBA.Black);
		// xhtml.setBackgroundColor(ColorRGBA.White);

		// LScrollPanel p = new LScrollPanel(screen);

		// Status
		status = new Label("Status Bar");

		//
		window1.setMinDimensions(new Vector2f(600, 600));
		window1.setMinimizable(true);
		window1.setMaximizable(true);
		window1.setIsResizable(true);
		window1.getContentArea().addChild(c);
		window1.getContentArea().addChild(xhtml);
		// window1.getContentArea().addChild(p);
		window1.getContentArea().addChild(status);
		window1.sizeToContent();

		// Listen for events from the document
		xhtml.addDocumentListener(new DocumentListener() {
			public void documentStarted() {
			}

			public void documentLoaded() {
				String documentURI = xhtml.getSharedContext().getBaseURL();
				window1.getDragBar().setText(xhtml.getDocumentTitle());
				if (documentURI == null) {
					address.setText("");
				} else {
					documentURI = documentURI.replace(System.getProperty("user.dir"), ".");
					address.setText(documentURI);
					address.setCaretPositionToEnd();
				}
			}

			public void onLayoutException(Throwable thrwbl) {
			}

			public void onRenderException(Throwable thrwbl) {
			}
		});

		screen.setGlobalAlpha(1f);

		try {
			xhtml.setDocument(HOME);
			history.push(HOME);
		} catch (Exception ex) {
			XRLog.load(Level.SEVERE, "Failed to parse URI.", ex);
			XRLog.log(INPUT_MAPPING_EXIT, Level.OFF, INPUT_MAPPING_EXIT);
		}

		screen.addElement(window1);
	}
}
