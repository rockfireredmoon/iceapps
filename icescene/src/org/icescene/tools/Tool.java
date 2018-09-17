package org.icescene.tools;

public class Tool extends ToolElement {

	private String icon;
	protected ToolCategory category;
	private boolean mayDrag = true;
	private String[] defaultToolBox;
	private boolean trashable;

	public Tool(String icon, String name, String help, int weight) {
		this(null, icon, name, help, weight);
	}

	public Tool(ToolCategory category, String icon, String name, String help, int weight) {
		super(name, help, weight);
		this.icon = icon;
		this.category = category;
	}

	public String[] getDefaultToolBox() {
		return defaultToolBox;
	}

	public Tool setDefaultToolBox(String... defaultToolBox) {
		this.defaultToolBox = defaultToolBox;
		return this;
	}

	public Tool setTrashable(boolean trashable) {
		this.trashable = trashable;
		return this;
	}

	public ToolManager getManager() {
		return category.getManager();
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + (this.category != null ? this.category.hashCode() : 0);
		hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
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
		final Tool other = (Tool) obj;
		if (this.category != other.category && (this.category == null || !this.category.equals(other.category))) {
			return false;
		}
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public ToolCategory getCategory() {
		return category;
	}

	public Tool setCategory(ToolCategory category) {
		this.category = category;
		return this;
	}

	public String getIcon() {
		return icon;
	}

	public Tool setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public boolean isMayDrag() {
		return mayDrag;
	}

	public Tool setMayDrag(boolean mayDrag) {
		this.mayDrag = mayDrag;
		return this;
	}

	public void actionPerformed(ActionData data) {
		// Subclasses can override and do extra stuff in the tool implementation
	}

	public boolean isSwappable(ToolBox targetToolBox, ToolBox sourceToolBox, Tool draggedTool) {
		return true;
	}

	public boolean isTrashable() {
		return trashable;
	}

	public boolean isDroppable(ToolBox targetToolBox) {
		return true;
	}

	public boolean isSwappable(ToolBox target, Tool swapWith) {
		return true;
	}
}
