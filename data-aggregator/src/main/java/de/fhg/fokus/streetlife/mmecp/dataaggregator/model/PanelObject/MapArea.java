package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by csc on 05.11.2015.
 */
@JsonTypeName("maparea")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class MapArea {

    private String groupID = null;
    public Area area = null;
    public Color color = null;

    public MapArea(String areaType, String coordinateType) {
        area = new Area(areaType, coordinateType);
        color = new Color();
    }

    public MapArea(String areaType, Coordinate coord, int polygonStyle, double size) {
        area = new Area(areaType, coord, polygonStyle, size);
        color = new Color();
    }

    public MapArea(Coordinate coord, Area.Icon icon) {
        area = new Area(coord, icon);
        color = null;
    }

    public Area getArea() {return area;}
    public Color getColor() {return color;}
    public String getGroupID() {return groupID;}

    public void setGroupID(String groupID) {this.groupID = groupID;}

}
