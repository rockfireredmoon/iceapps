package org.icetests;

import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icemoon.tools.impl.AbilitiesTool;
import org.icemoon.tools.impl.BuildModeTool;
import org.icemoon.tools.impl.CharacterSheetTool;
import org.icemoon.tools.impl.CreatureTweakTool;
import org.icemoon.tools.impl.InventoryTool;
import org.icemoon.tools.impl.OptionsTool;
import org.icemoon.tools.impl.QuestJournalTool;
import org.icemoon.tools.impl.SocialTool;
import org.icescene.IcesceneApp;
import org.icescene.MiniMapWindow;
import org.icescene.SceneConfig;
import org.icescene.controls.Rotator;
import org.icescene.gamecontrols.BuffBar;
import org.icescene.gamecontrols.CharacterBar;
import org.icescene.gamecontrols.PartyMemberBar;
import org.icescene.gamecontrols.PartyMemberBar.Status;
import org.icescene.gamecontrols.TargetBar;
import org.icescene.gamecontrols.TimedItemBar.TimedItem;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.tools.AbstractToolArea;
import org.icescene.tools.ActionBarsAppState;
import org.icescene.tools.ActionData;
import org.icescene.tools.DragContext;
import org.icescene.tools.HudType;
import org.icescene.tools.Tool;
import org.icescene.tools.ToolBox;
import org.icescene.tools.ToolCategory;
import org.icescene.tools.ToolManager;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.Button;
import icetone.controls.text.ToolTip;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.layout.GridLayout;

public class TestActions extends IcesceneApp {

	static {
		AppInfo.context = TestActions.class;
	}

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestActions.class, "Icetest");
	}

	private CharacterBar charBar;
	private TargetBar targetBar;
	private PartyMemberBar partyBar1;
	private PartyMemberBar partyBar2;
	private BuffBar playerBuff;

	public TestActions(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);
		flyCam.setEnabled(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
		geom.addControl(new Rotator());

		ToolManager toolManager = new ToolManager();
		TestHudType testHud = new TestHudType();

		// Build - Dialogs
		final ToolCategory testCategory = new ToolCategory(testHud, "Tests", "Test tools", 1);
		testCategory.setShowInToolBox(false);
		toolManager.addCategory(testCategory);
		testCategory.addTool(new TestTool1());

		// Game - Dialogs
		final ToolCategory gameDialogsCategory = new ToolCategory(testHud, "Game Dialogs",
				"Additional dialogs available in game", 1);
		toolManager.addCategory(gameDialogsCategory);
		gameDialogsCategory.addTool(new BuildModeTool());
		gameDialogsCategory.addTool(new CreatureTweakTool());
		gameDialogsCategory.addTool(new OptionsTool());
		gameDialogsCategory.addTool(new InventoryTool());
		gameDialogsCategory.addTool(new AbilitiesTool());
		gameDialogsCategory.addTool(new QuestJournalTool());
		gameDialogsCategory.addTool(new SocialTool());
		gameDialogsCategory.addTool(new CharacterSheetTool());

		// Game quick bars

		toolManager.addToolBox(testHud, new ToolBox("Quickbar1", "Quickbar (Ctrl hotkey)", 2, 8)
				.setDefaultVerticalPosition(BitmapFont.VAlign.Top).setDefaultHorizontalPosition(BitmapFont.Align.Left)
				.setStyle("quickbar").setDefaultVisible(false).setModifiers(ModifierKeysAppState.CTRL_MASK));
		toolManager.addToolBox(testHud, new ToolBox("Quickbar2", "Quickbar (Shift hotkey)", 3, 8)
				.setDefaultVerticalPosition(BitmapFont.VAlign.Top).setDefaultHorizontalPosition(BitmapFont.Align.Center)
				.setStyle("quickbar").setDefaultVisible(false).setModifiers(ModifierKeysAppState.SHIFT_MASK));
		toolManager.addToolBox(testHud, new ToolBox("Quickbar3", "Quickbar (Alt hotkey)", 4, 8)
				.setDefaultVerticalPosition(BitmapFont.VAlign.Top).setDefaultHorizontalPosition(BitmapFont.Align.Right)
				.setStyle("quickbar").setDefaultVisible(false).setModifiers(ModifierKeysAppState.ALT_MASK));
		toolManager.addToolBox(testHud,
				new ToolBox("Quickbar4", "Quickbar (Ctrl+Alt hotkey)", 5, 8)
						.setDefaultVerticalPosition(BitmapFont.VAlign.Center).setStyle("quickbar")
						.setDefaultHorizontalPosition(BitmapFont.Align.Left).setDefaultVisible(false)
						.setModifiers(ModifierKeysAppState.CTRL_MASK | ModifierKeysAppState.ALT_MASK));
		toolManager.addToolBox(testHud,
				new ToolBox("Quickbar5", "Quickbar (Ctrl+Shift hotkey)", 6, 8)
						.setDefaultVerticalPosition(BitmapFont.VAlign.Center).setStyle("quickbar")
						.setDefaultHorizontalPosition(BitmapFont.Align.Right).setDefaultVisible(false)

						.setModifiers(ModifierKeysAppState.CTRL_MASK | ModifierKeysAppState.SHIFT_MASK));
		toolManager.addToolBox(testHud,
				new ToolBox("Quickbar6", "Quickbar (Ctrl+Alt+Shift hotkey)", 7, 8)
						.setDefaultVerticalPosition(BitmapFont.VAlign.Center)
						.setDefaultHorizontalPosition(BitmapFont.Align.Center).setDefaultVisible(false)
						.setStyle("quickbar").setModifiers(ModifierKeysAppState.CTRL_MASK
								| ModifierKeysAppState.ALT_MASK | ModifierKeysAppState.SHIFT_MASK));
		toolManager.addToolBox(testHud, new ToolBox("Quickbar7", "Quickbar (Alt+Shift hotkey)", 8, 8)
				.setDefaultVerticalPosition(BitmapFont.VAlign.Bottom)
				.setDefaultHorizontalPosition(BitmapFont.Align.Right).setDefaultVisible(false).setStyle("quickbar")
				.setModifiers(ModifierKeysAppState.SHIFT_MASK | ModifierKeysAppState.ALT_MASK));

		toolManager.addToolBox(testHud, new ToolBox("Windows", "Various windows", 1, 8).setStyle("options")
				.setConfigurable(false).setMoveable(false));
		toolManager.addToolBox(testHud, new ToolBox("Main", "Main game toolbox", 1, 8).setStyle("primary-abilities")
				.setConfigurable(false).setMoveable(false));

		DragContext dragContext = new DragContext();
		ActionBarsAppState actions = new ActionBarsAppState(prefs, testHud, dragContext, toolManager);
		stateManager.attach(actions);

		charBar = new CharacterBar(screen);
		charBar.setPosition(100, 100);
		charBar.setMaxHealth(10000).setHealth(3538f).setDisplayName("Emerald Icemoon").setLevel(55);
		screen.addElement(charBar);

		targetBar = new TargetBar(screen);
		targetBar.setMaxHealth(10000).setHealth(1538f).setDisplayName("Questy McLongname").setLevel(50);
		targetBar.setPosition(screen.getWidth() - 100, 100);
		screen.addElement(targetBar);

		partyBar1 = new PartyMemberBar(screen);
		partyBar1.setMaxHealth(10000).setHealth(1538f).setDisplayName("Rockfire Redmoon");
		partyBar1.setPosition(50, 500);

		partyBar2 = new PartyMemberBar(screen);
		partyBar2.setMaxHealth(10000).setHealth(1538f).setDisplayName("Blaze Whitemoon").setStatus(Status.LEADER);
		partyBar2.setPosition(50, 500);

		// Element partyBars = new Element(screen, new MigLayout(screen, "gap 0,
		// // ins 0, wrap 1"));
		BaseElement partyBars = new BaseElement(screen, new GridLayout(1, 2));
		partyBars.addElement(partyBar1);
		partyBars.addElement(partyBar2);
		partyBars.setMovable(true);
		partyBars.setResizable(true);

		playerBuff = new BuffBar(screen) {
			{
				setStyleClass("player-buffs");
			}
		};
		playerBuff.setPreferredDimensions(new Size(128, 32));
		playerBuff.setResizable(true);
		playerBuff.setPosition(screen.getWidth() - 200, 20);

		screen.addElement(playerBuff);
		screen.addElement(partyBars);

		screen.addElement(new MiniMapWindow(screen, prefs) {

			@Override
			protected void onZoomOut() {
			}

			@Override
			protected void onZoomIn() {
			}

			@Override
			protected void onOpenWorldMap() {
			}

			@Override
			protected void onHome() {
			}
		});

	}

	class TestTool1 extends Tool {

		public TestTool1() {
			super("BuildIcons/Icon-32-Build-BuildMode.png", "Test 1", "Test Tool 1", 1);
			setDefaultToolBox("Tests");
			setTrashable(false);
			setMayDrag(true);
		}

		@Override
		public void actionPerformed(ActionData data) {
		}
	}

	class TestHudType implements HudType {

		@Override
		public Preferences preferenceNode() {
			return SceneConfig.get();
		}

		@Override
		public String name() {
			return "test";
		}

		@Override
		public AbstractToolArea createToolArea(ToolManager toolManager, BaseScreen screen) {
			final TestToolArea testToolArea = new TestToolArea(this, toolManager, screen);
			testToolArea.addControl(new AbstractControl() {

				float luck;
				double xp;
				float hc;
				float next = 1;

				{
					testToolArea.setMaxXp(500);
				}

				@Override
				protected void controlUpdate(float tpf) {
					luck += tpf * 5;
					xp += tpf * 5;
					hc += tpf;
					if (hc > next) {
						next = 1f + ((float) Math.random() * 3f);
						charBar.setHealth((float) Math.random() * charBar.getMaxHealth());
						charBar.setWill((int) (Math.random() * 10));
						charBar.setMight((int) (Math.random() * 10));
						charBar.setWillCharge((int) (Math.random() * 5));
						charBar.setMightCharge((int) (Math.random() * 5));

						targetBar.setHealth((float) Math.random() * targetBar.getMaxHealth());
						targetBar.setWillCharge((int) (Math.random() * 5));
						targetBar.setMightCharge((int) (Math.random() * 5));

						partyBar1.setHealth((float) Math.random() * targetBar.getMaxHealth());
						partyBar2.setHealth((float) Math.random() * targetBar.getMaxHealth());

						if (Math.random() > 0.25) {
							partyBar1.getBuffBar().addTimedItem(
									new TimedItem(new Button(-1, -1, pickRandomIcon()).setDestroyOnHide(true), 10, 2));
							partyBar1.layoutChildren();
						}

						if (Math.random() > 0.25) {
							partyBar2.getBuffBar().addTimedItem(
									new TimedItem(new Button(-1, -1, pickRandomIcon()).setDestroyOnHide(true), 10, 2));
							partyBar2.layoutChildren();
						}

						if (Math.random() > 0.25) {
							targetBar.getBuffBar().addTimedItem(
									new TimedItem(new Button(-1, -1, pickRandomIcon()).setDestroyOnHide(true), 10, 2));
							targetBar.layoutChildren();
						}

						if (Math.random() > 0.25) {
							playerBuff.addTimedItem(
									new TimedItem(new Button(-1, -1, pickRandomIcon()).setDestroyOnHide(true), 10, 2));
							// targetBar.layoutChildren();
						}

						hc = 0;
					}
					testToolArea.setLuck((int) luck);
					testToolArea.setXp((long) xp);
				}

				@Override
				protected void controlRender(RenderManager rm, ViewPort vp) {
				}
			});
			return testToolArea;
		}
	}

	private String pickRandomIcon() {
		String[] a = new String[] { "Icon-32-Ability-D_Mystical.png", "Icon-32-Ability-D_Nefritaris_Aura.png",
				"Icon-32-Ability-D_Reflective_Ward.png", "Icon-32-Ability-D_Sacrifice.png",
				"Icon-32-Ability-D_Shadow_Spirit.png", "Icon-32-Ability-D_Snare.png",
				"Icon-32-Ability-D_Soul_Burst.png", "Icon-32-Ability-D_Soul_Needles.png",
				"Icon-32-Ability-D_Spirit_Of_Solomon.png", "Icon-32-Ability-D_Sting.png",
				"Icon-32-Ability-D_Swarm.png" };
		return "Icons/" + a[(int) (Math.random() * a.length)];
	}

	class TestToolArea extends AbstractToolArea {

		public TestToolArea(HudType hud, ToolManager toolMgr, final BaseScreen screen) {
			super(hud, toolMgr, screen, "main-toolbar", "Quickbar", 7);
			updateBarText();
		}

		@Override
		protected void updateBarText() {
		}

		@Override
		protected void onDestroy() {
		}

		@Override
		protected BaseElement createInfoToolTip(BaseElement el) {
			return new ToolTip(screen).setText("This is a test tooltip for " + el);
		}
	}
}
