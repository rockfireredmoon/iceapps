package org.icescene.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import com.jme3.font.BitmapFont;


/**
 * Encapsulates a single toolbox. A toolbox is a grouping of actions, each represented by
 * an icon. The screen can and will have multiple toolboxes visible at any time in game
 * (both play mode and build mode). Each toolbox can also be one of a number of styles.
 */
public class ToolBox extends ToolElement {
	public static final String PROP_VISIBLE = "visible";

    private final static Logger LOG = Logger.getLogger(ToolBox.class.getName());
    private Tool[] tools;
    private Preferences toolBoxNode;
    private Preferences toolsNode;
    private HudType hud;
    private ToolManager manager;
    private int defaultHorizontalCells = -1;
    private boolean vertical;
    private BitmapFont.Align defaultHorizontalPosition = null;
    private BitmapFont.VAlign defaultVerticalPosition = null;
    private boolean defaultVisible = true;
    private final int slots;
    private String style;
    private int modifiers = -1;
    private Boolean visible;
    private boolean configurable = true;
    private boolean moveable = true;
    private boolean persistent;

    public ToolBox(String name, String help, int weight, int slots) {
        super(name, help, weight);
        if (slots < 1) {
            throw new IllegalArgumentException("Must have at least one slot");
        }
        this.slots = slots;
        this.tools = new Tool[slots];
    }
    
    public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public ToolBox setMoveable(boolean moveable) {
		this.moveable = moveable;
		return this;
	}

	public boolean isConfigurable() {
		return configurable;
	}

	public ToolBox setConfigurable(boolean configurable) {
		this.configurable = configurable;
		return this;
	}

	public Preferences getPreferencesNode() {
        return toolBoxNode;
    }

    public boolean isDefaultVisible() {
        return defaultVisible;
    }

    public ToolBox setDefaultVisible(boolean defaultVisible) {
        this.defaultVisible = defaultVisible;
        return this;
    }
    
    public int getModifiers() {
        return modifiers;
    }

    public ToolBox setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public int getSlots() {
        return slots;
    }

    public String getStyle() {
        return style;
    }

    public ToolBox setStyle(String style) {
        this.style = style;
        return this;
    }

    public BitmapFont.Align getDefaultHorizontalPosition() {
        return defaultHorizontalPosition;
    }

    public ToolBox setDefaultHorizontalPosition(BitmapFont.Align defaultHorizontalPosition) {
        this.defaultHorizontalPosition = defaultHorizontalPosition;
        return this;
    }

    public BitmapFont.VAlign getDefaultVerticalPosition() {
        return defaultVerticalPosition;
    }

    public ToolBox setDefaultVerticalPosition(BitmapFont.VAlign defaultVerticalPosition) {
        this.defaultVerticalPosition = defaultVerticalPosition;
        return this;
    }

	public boolean isVisible() {
        if(visible == null) {
            return defaultVisible;
        }
        return visible;
	}

	public void setVisible(boolean visible) {
		boolean was = isVisible();
		if (!Objects.equals(was, visible)) {
			this.visible = visible;
	        if(configurable)
	        	toolBoxNode.putBoolean("visible", visible);
			firePropertyChange(PROP_VISIBLE, was, visible);
		}
	}
    
    void init(ToolManager manager, HudType hud, Preferences toolBoxNode) {
        this.tools = new Tool[slots];
        this.hud = hud;
        this.toolBoxNode = toolBoxNode;
        this.manager = manager;
        try {
            boolean newToolBox = !Arrays.asList(toolBoxNode.childrenNames()).contains("tools");
            toolsNode = toolBoxNode.node("tools");
            if(configurable && Arrays.asList(toolBoxNode.keys()).contains("visible")) {
                visible = toolBoxNode.getBoolean("visible", defaultVisible);
            }

            List<String> shift = new ArrayList<String>();
            final String[] keys = toolsNode.keys();

            /* If there are no keys, this must be a new tool box. If so, 
             * add any tools that use it by default
             */
            if (newToolBox) {
                int slot = 0;
                for (Tool t : manager.getAllTools(hud)) {
                    if (t.getDefaultToolBox() != null && Arrays.asList(t.getDefaultToolBox()).contains(name)) {
                        if (slot >= tools.length) {
                            LOG.warning(String.format("No enough room for all default tools on tool box %s", name));
                            break;
                        }
                        setTool(slot, manager, hud, t.getName());
                        putTool(slot);
                        slot++;
                    }
                }
            } else {

                for (String key : keys) {
                    int slot = Integer.parseInt(key);
                    String tool = toolsNode.get(key, "");
                    if (slot < 0 || slot >= tools.length || tools[slot] != null) {
                        LOG.info(String.format("Cannot put tool %s at slot %s, will shift", key, slot));
                        shift.add(tool);
                    } else {
                        setTool(slot, manager, hud, tool);
                    }
                }

                // Now add any that must be shifted into the first free slot
                for (String tool : shift) {
                    int nextFree = getNextFreeSlot();
                    if (nextFree == -1) {
                        LOG.warning(String.format("Ran out of slots in tool bar %s for tool %s. Tool will not be visibile", name, tool));
                    } else {
                        setTool(nextFree, manager, hud, tool);
                    }
                }
            }

        } catch (Exception bse) {
            throw new RuntimeException(bse);
        }
    }

    public int getHorizontalCells() {
        if (configurable && toolBoxNode == null) {
            throw new IllegalStateException("May not get horizontal cells till added to the tool manager.");
        }
        return configurable ? toolBoxNode.getInt("hCells", getDefaultHorizontalCells()) : getDefaultHorizontalCells();
    }


    public int getVerticalCells() {
        return getSlots() / getHorizontalCells();
    }

    public void setHorizontalCells(int horizontalCells) {
        if (toolBoxNode == null) {
            throw new IllegalStateException("May not set vertical till added to the tool manager.");
        }
        if(configurable)
        	toolBoxNode.putInt("hCells", horizontalCells);
    }

    public int getDefaultHorizontalCells() {
        return defaultHorizontalCells == -1 ? slots : defaultHorizontalCells;
    }

    public ToolBox setDefaultHorizontalCells(int defaultHorizontalCells) {
        this.defaultHorizontalCells = defaultHorizontalCells;
        return this;
    }

    private int getNextFreeSlot() {
        for (int i = 0; i < tools.length; i++) {
            if (tools[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private void setTool(int slot, ToolManager manager, HudType hud, String tool) {
        setTool(slot, manager, hud, tool == null || tool.equals("") ? null : manager.getTool(hud, tool));
    }

    private void setTool(int slot, ToolManager manager, HudType hud, Tool tool) {
        tools[slot] = tool;
        if (tools[slot] == null) {
            LOG.warning(String.format("Tool %s is missing and will not be added to tool box %s", tool, name));
        }
    }

    public Tool[] getTools() {
        return tools;
    }

    public void drop(ToolManager.DragOperation op, Tool draggedTool, int slot, ToolBox sourceToolBox, int sourceSlot) {
        switch (op) {
            case DROP:
                tools[slot] = draggedTool;
                break;
            case TRASH:
                tools[slot] = null;
                break;
            case SWAP:
                Tool t = tools[slot];
                tools[slot] = draggedTool;
                sourceToolBox.tools[sourceSlot] = t;
                break;
        }
    }

    public void trash(Tool tool) {
        final int index = Arrays.asList(tools).indexOf(tool);
        tools[index] = null;
        LOG.info(String.format("Trashed %s", tool.getName()));
        putTool(index);
    }

    public void drop(Tool tool, int slot) {
        tools[slot] = tool;
        LOG.info(String.format("Dropped %s in slot %d", tool, slot));
        putTool(slot);
    }

    public void swap(int targetSlot, int sourceSlot, ToolBox sourceToolBox) {
        Tool t = tools[targetSlot];
        tools[targetSlot] = sourceToolBox.tools[sourceSlot];
        putTool(targetSlot);
        sourceToolBox.tools[sourceSlot] = t;
        sourceToolBox.putTool(sourceSlot);
        LOG.info(String.format("Swapped %s (%d) and %s (%d)", t, sourceSlot, tools[targetSlot], targetSlot));
    }

    @Override
	public String toString() {
		return "ToolBox [tools=" + Arrays.toString(tools) + ", toolBoxNode=" + toolBoxNode + ", toolsNode=" + toolsNode + ", hud="
				+ hud + ", manager=" + manager + ", defaultHorizontalCells=" + defaultHorizontalCells + ", vertical=" + vertical
				+ ", defaultHorizontalPosition=" + defaultHorizontalPosition + ", defaultVerticalPosition="
				+ defaultVerticalPosition + ", defaultVisible=" + defaultVisible + ", slots=" + slots + ", style=" + style
				+ ", modifiers=" + modifiers + ", visible=" + visible + ", configurable=" + configurable + ", moveable=" + moveable
				+ "]";
	}

	private void putTool(int slot) {
        Tool tool = tools[slot];
        toolsNode.put(String.valueOf(slot), tool == null ? "" : tool.getName());
    }
}
