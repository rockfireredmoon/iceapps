package org.icescene.terrain;

import java.util.logging.Logger;

import org.icelib.PageLocation;
import org.icescene.configuration.TerrainTemplateConfiguration;

import com.jme3.math.Vector2f;

public class PageConfiguration extends PageLocation {

    private final static Logger LOG = Logger.getLogger(PageConfiguration.class.getName());
    private TerrainTemplateConfiguration configuration;
    
    public PageConfiguration(PageLocation page, TerrainTemplateConfiguration configuration) {
        super(page);
        this.configuration = configuration;
    }

    public PageConfiguration(int x, int y) {
        super(x, y);
    }

    public TerrainTemplateConfiguration getConfiguration() {
        return configuration;
    }

    public Vector2f getRelativeLocation(Vector2f otherLocation) {
        return new Vector2f(otherLocation.x - (configuration.getPageWorldX() * x), otherLocation.y - (configuration.getPageWorldZ() * y));
    }

    public Vector2f getRelativeRatio(Vector2f otherLocation) { 
        return new Vector2f((otherLocation.x - (configuration.getPageWorldX() * x))/configuration.getPageWorldX(), (otherLocation.y - (configuration.getPageWorldZ() * y))/configuration.getPageWorldZ());
    }

    public Vector2f getWorldLocation(Vector2f relativeLocation) {
        return new Vector2f(relativeLocation.x + (configuration.getPageWorldX() * x), relativeLocation.y + (configuration.getPageWorldZ() * y));
    }
}
