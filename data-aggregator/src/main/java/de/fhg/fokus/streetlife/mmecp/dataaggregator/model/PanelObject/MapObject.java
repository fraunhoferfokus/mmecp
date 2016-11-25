package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by csc on 13.08.2015 for use case: generate output JSON for display of VMZ traffic counters
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "VmzDtvwDataTypeIdResolver")
//@JsonTypeIdResolver(VmzDtvwDataTypeIdResolver.class)
public class MapObject {

    // private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected class Location {
        public String type = "Point";
        public List<Double> coordinates = new ArrayList<>(2);
    }

    protected class DiagramDataValue {
        public String label;
        public double value;

        public DiagramDataValue(String label,double value){
            this.label = label;
            this.value = value;
        }
    }

    public List<DiagramDataValue> getDiagramData() {
        return diagramData;
    }

    public void withDiagramData(ArrayList<String> labels,ArrayList<Double> values,String diagramType,String diagramTitle) {
        this.diagramData = new ArrayList<DiagramDataValue>();
        for(int i = 0;i<labels.size();i++)
        {
            this.diagramData.add(new DiagramDataValue(labels.get(i),values.get(i)));

        }
        this.diagramType = diagramType;
        this.diagramTitle = diagramTitle;
    }

    public String type = "mapobject";
    public String objectID;  /* e.g. "BER_Crossing _ 03" */
    public String objectType; /* e.g. "TrafficCounters" */
    public String objectSubtype; /* e.g. "DTVw" */
    public String description; /* e.g. "via_VMZ" */
    public Location location;
    public List<DiagramDataValue> diagramData;
    public String diagramType = "none";
    public String diagramTitle = "none";
    /* e.g.
        "location": {
          "type": "Point",
          "coordinates": [
            13.286821,
            52.518842
          ]
        }
    */

    public void addElement(Attribute att) {
        if (att != null) {
            this.elements.add(att);
        }
    }

    public String findAttribute(String label) {
        if (label == null || label.isEmpty()) return null;

        for (Object obj : this.elements) {
            if (obj instanceof Attribute) {
                Attribute att = (Attribute)obj;
                if (label.equals(att.getLabel())) {
                    return att.getValue();
                }
            }
        }
        return null;
    }

    public void addElement(MapArea marea) {
        if (marea != null) {
            this.elements.add(marea);
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    private List<Object> elements;  // contains attributes and also a mapArea object
    /* e.g.
        "attribute": {
          "label": "crossing_number",
          "value": "1234"
        }
    */

    // public MapArea mapArea;  // mapArea must be a part of the "elements" list!!!
    /* e.g.
    "maparea": {
      "area": {
        "type": "Polygon",
        "coordinateType": "UTM",
        "coordinates": [
          {
            "zone": "33N",
            "N" :  389811.36812789790565148,
            "E" : 5814398.89597235433757305
          },
          {
            "zone": "33N",
            "N" :  389812.36812789790565148,
            "E" : 5814398.89597235433757305
          },
          {
            "zone": "33N",
            "N" :  389812.36812789790565148,
            "E" : 5814399.89597235433757305
          },
          {
            "zone": "33N",
            "N" :  389811.36812789790565148,
            "E" : 5814399.89597235433757305
          }
        ]
      }
     */

    public void setObjectSubtype(String objectSubtype) {this.objectSubtype = objectSubtype;}

    public MapObject() {
        location = new Location();
        elements = new ArrayList<>();
    }

    public MapObject(String objectID, String objectSubtype) {
        this();

        this.type = "mapobject";
        this.objectID = objectID;
        this.objectType = "none";
        this.objectSubtype = objectSubtype;
        this.description = "from_VMZ";

        this.location.type = "Point";
        this.location.coordinates.add(13.13131313);
        this.location.coordinates.add(52.52525252);

        // this.addElement(new Attribute("ID",objectID));

        //MapArea ma = new MapArea(mapAreaType, Coordinate.TYPE_UTM);
        //this.addElement(ma);
    }


    public MapObject withAttributeOmitNull(String attributeName, String attributeValue) {
        if (attributeName == null || attributeValue == null ||
            attributeName.isEmpty() || attributeValue.isEmpty() ||
            attributeValue.equalsIgnoreCase("null"))
        {
            return this;
        } else {
            this.addElement(new Attribute(attributeName, attributeValue));
            return this;
        }
    }


    public MapObject withAttribute(String attributeName, String attributeValue) {
        if (attributeName != null && attributeValue != null && !attributeName.isEmpty() && !attributeValue.isEmpty()) {
            this.addElement(new Attribute(attributeName, attributeValue));
        }
        return this;
    }

    public MapObject withAttribute(String attributeName, int attributeValue) {
        if (attributeName != null && !attributeName.isEmpty()) {
            this.addElement(new Attribute(attributeName, attributeValue));
        }
        return this;
    }

    public MapObject withAttribute(String attributeName, Long attributeValue) {
        if (attributeName != null && attributeValue != null && !attributeName.isEmpty()) {
            this.addElement(new Attribute(attributeName, attributeValue));
        }
        return this;
    }


    public MapObject withIconAt(Coordinate coord, Area.Icon icon) {
        if (coord != null && icon != null) {
            this.addElement(new MapArea(coord, icon));
        }
        return this;
    }

    public MapObject withIconsAt(List<Coordinate> coordinates, Area.Icon icon) {
        if (coordinates != null && icon != null) {
            for (Coordinate coord : coordinates) {
                this.withIconAt(coord, icon);
            }
        }
        return this;
    }

    public MapObject withMarkerAt(Coordinate coord, int style, double size) {
        if (coord != null) {
            MapArea ma = new MapArea(Area.AREATYPE_POLYGON, coord, style, size);
            ma.color = new Color().random();
            // ma.color.colorize(accidents, Color.FOKUS_COLOR_SCHEME);
            this.addElement(ma);
        }
        return this;
    }

    public MapObject withMarkersAt(List<Coordinate> coordinates, int style, double size) {
        if (coordinates != null) {
            for (Coordinate coord : coordinates) {
                this.withMarkerAt(coord, style, size);
            }
        }
        return this;
    }


    public MapObject withRouteThrough(List<Coordinate> points, Color color) {
        if (points != null) {
            return this.withRouteThrough(points, color, null);
        } else {
            return this;
        }
    }

    public MapObject withRouteThrough(List<Coordinate> points, Color color, String groupID) {
        // this.addElement(new Attribute("groupID", groupID));
        if (points != null && points.size() >= 2) {
            this.withLineThrough(points, (color != null) ? color : Color.BLACK);
            this.withIconAt(points.get(0), Area.Icon.ROUTESTART_ICON);
            this.withIconAt(points.get(points.size() - 1), Area.Icon.ROUTEEND_ICON);
            if (groupID != null && !groupID.isEmpty()) {this.setGroupIdAll(groupID);}
        }
        return this;
    }

    public MapObject withLineThrough(List<Coordinate> points) {
        return withLineThrough(points, new Color().random());
    }

    public MapObject withLineThrough(List<Coordinate> points, Color color) {
        if (points != null && points.size() >= 2) {
            // assume that all coordinates in the list have the same CoordinateType as the first element in the list!
            String coordType = points.get(0).getCoordType();
            MapArea ma = new MapArea(Area.AREATYPE_LINE, coordType);
            ma.area.addLine(points);
            ma.color = color;
            // ma.color.colorize(accidents, Color.FOKUS_COLOR_SCHEME);
            this.addElement(ma);
        }
        return this;
    }

    public MapObject withObjectType(String objectType) {this.objectType = objectType; return this;}

    public MapObject setGroupIdAll(String groupID) {
        if (groupID != null && !groupID.isEmpty()) {
            this.elements.forEach(obj -> {
                if (obj instanceof MapArea) {
                    MapArea ma = (MapArea) obj;
                    ma.setGroupID(groupID);
                }
            });
        }
        return this;
    }

    public String getType() {return type;}
    public String getObjectID() {return objectID;}
    public String getObjectType() {return objectType;}
    public String getObjectSubtype() {return objectSubtype;}
    public String getDescription() {return description;}
    public Location getLocation() {return location;}
    public List<Object> getElements() {return elements;}


    public static Comparator<MapObject> comparator = new Comparator<MapObject>() {
        @Override
        public int compare(final MapObject o1, final MapObject o2) {
            if ((o1 instanceof VmzDtvwData) && (o2 instanceof VmzDtvwData)) {
                return VmzDtvwData.compare((VmzDtvwData)o1, (VmzDtvwData)o2);
            } else {
                return o1.getObjectID().compareTo(o2.getObjectID());
            }
        }
    };


}
