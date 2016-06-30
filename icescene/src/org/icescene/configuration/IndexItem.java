package org.icescene.configuration;

public class IndexItem {
    private String name;
    private long lastModified;
    private long size;

    public IndexItem(String name) {
        this(name, Long.MAX_VALUE, -1);
    }
    public IndexItem(String name, long lastModified, long size) {
        this.name = name;
        this.lastModified = lastModified;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getSize() {
        return size;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final IndexItem other = (IndexItem) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
}
