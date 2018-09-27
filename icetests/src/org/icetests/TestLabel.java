package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.appstates.PopupMessageAppState;
import icetone.extras.appstates.PopupMessageAppState.Channel;

public class TestLabel extends IcesceneApp {

	static {
		AppInfo.context = TestLabel.class;
	}

	public static void main(String[] args) {
		TestLabel app = new TestLabel();
		app.start();
	}

	public TestLabel() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		// Panel panel = new Panel(new MigLayout("fill, wrap 1"));
		// panel.addElement(new Label("Just an ordinary label"), "growx");
		//
		// panel.addElement(new Label("<i>Some text in a
		// label</i>!!").setParseTextTags(true).setFontColor(ColorRGBA.Green)
		// .setFontFamily("mediumOutline").setFontSize(24), "growx");
		// panel.addElement(new ScrollPanel(new Label("<i>Some text in a
		// label</i>!!").setDefaultColor(ColorRGBA.Red)
		// .setParseTextTags(true).setTextWrap(LineWrapMode.Word)), "growx,
		// growy");
		// panel.addElement(new Label("With <i>parse tags
		// off</i>!!").setParseTextTags(false), "growx");
		// panel.addElement(new Label("With some CSS!!").setCss("color: red;
		// font-size: 16px;"), "growx");
		// panel.addElement(new Label("Some text\nwith some newlines\nembedded.
		// Does preferred\nsize work?"), "growx");
		// panel.addElement(new Label(
		// "A much longer unbroken piece of text. Some text\nwith some
		// newlines\nembedded. Does preferred\nsize work?")
		// .setTextWrap(LineWrapMode.Word),
		// "growx");
		// panel.setFontColor(ColorRGBA.Red);
		// panel.setFontFamily("mediumOutline");
		// panel.setFontSize(64);

		/* Examples menu */
//		ScrollPanel examples = new ScrollPanel();
//		// examples.setHorizontalScrollBarMode(ScrollBarMode.Never);
//
//		MigLayout scrollAreaLayout = new MigLayout("wrap 1, ins 0, fill", "[]", "[]");
//		examples.setScrollContentLayout(scrollAreaLayout);
//		for (String abcdef : new String[] { "ABCDEF", "ABCDEF", "ABCDEF", "ABCDEF", "ABCDEF", "ABCDEF", "ABCDEF", "ABCDEF", "ABCDEF" }) {
//			examples.addScrollableContent(new PushButton(abcdef), "growx");
//			examples.addScrollableContent(
//					new Label("asdfasdfsad asd asd asd sa asd asd asd ad adas ").setTextWrap(LineWrapMode.Word),
//					"growx");
//			examples.addScrollableContent(new Separator(Orientation.HORIZONTAL), "growx");
//		}
//		examples.setDefaultColor(ColorRGBA.Blue);
//		examples.getScrollableArea().setDefaultColor(ColorRGBA.Red);
//		examples.getScrollBounds().setDefaultColor(ColorRGBA.Green);
//
//		TabControl tab = new TabControl();
//		tab.addTab("Tab 1", examples);
//
//		// panel.addElement(tab, "growx,growy");

//		SplitPanel panel = new SplitPanel();
//		panel.setDefaultColor(ColorRGBA.Blue);
//		panel.setTexture("/bgx.jpg");
//		panel.setLeftOrTop(new Label("left"));
//		panel.setRightOrBottom(new Label("right"));
//		panel.setDefaultDividerLocationRatio(0.5f);
		

		PopupMessageAppState pamm = new PopupMessageAppState(screen);		
		
		Panel p = new Panel(new MigLayout());
		p.addElement(new PushButton("Info").onMouseReleased(evt -> pamm.message(Channel.INFORMATION, "This is a test information message!")));
		p.addElement(new PushButton("Error").onMouseReleased(evt -> pamm.message(Channel.ERROR, "This is a test error message!")));
		p.addElement(new PushButton("Warning").onMouseReleased(evt -> pamm.message(Channel.WARNING, "This is a test warning message!")));
		p.addElement(new PushButton("Success").onMouseReleased(evt -> pamm.message(Channel.SUCCESS, "This is a test success message!")));
//
		screen.addElement(p);
//		stateManager.attach(new GUIExplorerAppState());

		stateManager.attach(pamm);
//		pamm.message(Channel.INFORMATION, "This is a test message!");
		
	}
}
