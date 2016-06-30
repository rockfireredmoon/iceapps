package org.icescene.scene;

import org.icelib.Region;

public class ClothingTextureKey {

    private String clothingGroupName;
    private String subTextureName;
    private Region region;

    public ClothingTextureKey(String clothingGroupName, String subTextureName, Region region) {
        this.clothingGroupName = clothingGroupName;
        this.subTextureName = subTextureName;
        this.region = region;
    }

    public String getClothingGroupName() {
        return clothingGroupName;
    }

    public String getSubTextureName() {
        return subTextureName;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.clothingGroupName != null ? this.clothingGroupName.hashCode() : 0);
        hash = 79 * hash + (this.subTextureName != null ? this.subTextureName.hashCode() : 0);
        hash = 79 * hash + (this.region != null ? this.region.hashCode() : 0);
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
        final ClothingTextureKey other = (ClothingTextureKey) obj;
        if ((this.clothingGroupName == null) ? (other.clothingGroupName != null) : !this.clothingGroupName.equals(other.clothingGroupName)) {
            return false;
        }
        if ((this.subTextureName == null) ? (other.subTextureName != null) : !this.subTextureName.equals(other.subTextureName)) {
            return false;
        }
        if (this.region != other.region) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "ClothingTextureKey [clothingGroupName=" + clothingGroupName + ", subTextureName=" + subTextureName + ", region="
				+ region + "]";
	}

}
