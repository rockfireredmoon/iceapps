package org.icescene.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;

public class ToolManager {

    
    public enum DragOperation {

        REJECT, SWAP, TRASH, DROP
    }
    private Map<HudType, TreeMap<ToolCategory, List<Tool>>> tools = new LinkedHashMap<HudType, TreeMap<ToolCategory, List<Tool>>>();
    private Map<HudType, Map<String, Tool>> toolMap = new LinkedHashMap<HudType, Map<String, Tool>>();
    private Map<HudType, List<ToolBox>> toolBoxes = new LinkedHashMap<HudType, List<ToolBox>>();

    public ToolCategory addCategory(ToolCategory category) {
        TreeMap<ToolCategory, List<Tool>> categoryTools = tools.get(category.getType());
        if (categoryTools == null) {
            categoryTools = new TreeMap<ToolCategory, List<Tool>>(new Comparator<ToolCategory>() {
                public int compare(ToolCategory o1, ToolCategory o2) {
                    int o = Integer.valueOf(o1.getWeight()).compareTo(o2.getWeight());
                    return o == 0 ? o1.getName().compareTo(o2.getName()) : o;
                }
            });
            tools.put(category.getType(), categoryTools);
        }
        if (categoryTools.containsKey(category)) {
            throw new IllegalArgumentException("Category " + category + " already added");
        }
        category.initManager(this);
        categoryTools.put(category, new ArrayList<Tool>());
        return category;
    }

    public Map<ToolCategory, List<Tool>> getTools(HudType type) {
        return tools.get(type);
    }

    public void addTool(Tool tool) {


        // Index the tool
        Map<String, Tool> tm = toolMap.get(tool.getCategory().getType());
        if (tm == null) {
            tm = new LinkedHashMap<String, Tool>();
            toolMap.put(tool.getCategory().getType(), tm);
        }
        if (tm.containsKey(tool.getName())) {
            throw new IllegalArgumentException(String.format("Already a tool with name of %s registered.", tool.getName()));
        }
        tm.put(tool.getName(), tool);


        // Indev the tool within it's category
        TreeMap<ToolCategory, List<Tool>> category = tools.get(tool.getCategory().getType());
        if (category == null) {
            throw new IllegalArgumentException("Tool belongs to type (" + tool.getCategory().getType() + ") that has not been added");
        }
        List<Tool> categoryTools = category.get(tool.getCategory());
        if (categoryTools == null) {
            throw new IllegalArgumentException("Tool belongs to category (" + tool.getCategory() + ") that has not been added");
        }
        categoryTools.add(tool);
    }

    public List<Tool> getAllTools(HudType type) {
        Map<ToolCategory, List<Tool>> cats = getTools(type);
        List<Tool> l = new ArrayList<Tool>();
        if (cats != null) {
            for (Map.Entry<ToolCategory, List<Tool>> en : cats.entrySet()) {
                l.addAll(en.getValue());
            }
        }
        return l;
    }

    public void addToolBox(HudType hud, ToolBox toolBox) {
        List<ToolBox> tb = toolBoxes.get(hud);
        if (tb == null) {
            tb = new ArrayList<ToolBox>();
            toolBoxes.put(hud, tb);
        }
        tb.add(toolBox);
        toolBox.init(this, hud, hud.preferenceNode().node(toolBox.getName()));
    }

    public void reset(HudType hud) {
        try {
            hud.preferenceNode().removeNode();
            List<ToolBox> tb = toolBoxes.get(hud);
            if (tb != null) {
                for (ToolBox t : tb) {
                    t.init(this, hud, hud.preferenceNode().node(t.getName()));
                }
            }
        } catch (BackingStoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Tool getTool(HudType hud, String tool) {
        Map<String, Tool> t = toolMap.get(hud);
        return t == null || !t.containsKey(tool) ? null : t.get(tool);
    }

    public List<ToolBox> getToolBoxes(HudType hud) {
        return toolBoxes.get(hud);
    }

    public ToolBox getToolBox(HudType hudType, String name) {
        List<ToolBox> t = toolBoxes.get(hudType);
        for (ToolBox a : t) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }
}
