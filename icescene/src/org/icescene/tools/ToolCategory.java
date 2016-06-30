package org.icescene.tools;


public class ToolCategory extends ToolElement {
    private final HudType type;
    protected String name;
    protected ToolManager manager;
    private boolean showInToolBox;

    public ToolCategory(HudType type, String name, String help, int weight) {
        super(name, help, weight);
        this.type = type;
    }
    
    public ToolCategory addTool(Tool tool) {
        if(manager == null) {
            throw new IllegalStateException("Category must be added to manager first.");
        }
        tool.category = this;
        manager.addTool(tool);
        return this;
    }
    
    protected void initManager(ToolManager manager) {
        this.manager=  manager;
    }
    
    public boolean isShowInToolBox() {
		return showInToolBox;
	}

	public void setShowInToolBox(boolean showInToolBox) {
		this.showInToolBox = showInToolBox;
	}

	public ToolManager getManager() {
        return manager;
    }

    public HudType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ToolCategory other = (ToolCategory) obj;
        if (this.type != other.type) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
}
