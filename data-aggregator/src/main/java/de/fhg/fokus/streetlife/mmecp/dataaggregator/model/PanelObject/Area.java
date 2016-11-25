package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by csc on 05.11.2015.
 */
@JsonSerialize(using = Area.AreaSerializer.class)
public class Area {

    public static class AreaSerializer extends JsonSerializer<Area> {

        private static void writeSystem(JsonGenerator jgen) throws IOException {
            // add static Json content: {"type": "EPSG", "properties": {"code": 4326}}
            jgen.writeFieldName("system");
            jgen.writeStartObject();
            jgen.writeStringField("type", "EPSG");
            jgen.writeFieldName("properties");
            jgen.writeStartObject();
            jgen.writeNumberField("code", 4326);
            jgen.writeEndObject();
            jgen.writeEndObject();
        }

        public void serialize(Area area, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            // AreaSerializer.writeSystem(jgen);
            jgen.writeStringField("type", area.type);
            jgen.writeStringField("coordinateType", area.coordinateType);

            if (AREATYPE_ICON.equals(area.type)) {
                if (area.icon != null) {
                    jgen.writeStringField("icon", area.icon.fileName);
                    jgen.writeNumberField("scale", area.icon.scale);
                    jgen.writeFieldName("anchor");
                    jgen.writeStartArray();
                    jgen.writeNumber(area.icon.anchorX);
                    jgen.writeNumber(area.icon.anchorY);
                    jgen.writeEndArray();
                }
                jgen.writeObjectField("coordinates",area.coordinates.get(0));
            } else if (AREATYPE_POLYGON.equals(area.type)) {
                // add one more pair of "[ ]" to Json output for Polygons, compared to hetamap or line (see below)
                List<List<Coordinate>> coordinatesList = new ArrayList<>(1);
                coordinatesList.add(area.coordinates);
                jgen.writeObjectField("coordinates",coordinatesList);
            } else {
                // no extra pair of "[ ]" needed for Line and Heatmap output in GeoJson format
                jgen.writeObjectField("coordinates",area.coordinates);
            }
            jgen.writeEndObject();
            // jgen.flush();
        }

        public Class<Area> handledType() {return Area.class;}
    }

    public static class Icon {
        public String fileName;
        public double scale = 1.0;
        public double anchorX = 0;
        public double anchorY = 0;

        public Icon(String fileName, double scale, double anchorX, double anchorY) {
            this.fileName = fileName;
            this.scale = scale;
            this.anchorX = anchorX;
            this.anchorY = anchorY;
        }

        public static final Icon DANGER_ICON= new Icon("danger", 0.2, 0.5, 200);
        public static final Icon ROUTESTART_ICON = new Icon("routestart", 0.6, 0.5, 16);
        public static final Icon ROUTEEND_ICON = new Icon("routeend", 0.6, 0.5, 16);
        public static final Icon ROUTELINK_ICON = new Icon("link", 0.5 , 0.5, 8);
        public static final Icon EVENT_ICON = new Icon("icon_event", 0.5 , 0.5, 98);
        public static final Icon BIKE_STATION_ICON = new Icon("icon_bike_station", 0.5 , 0.5, 98);
    }

    public String type;
    public Icon icon = null;
    public String coordinateType;
    public List<Coordinate> coordinates;

    public static final String AREATYPE_ICON    = "icon";    // export coordinates in Json as [x,y]
    public static final String AREATYPE_LINE    = "line";    // export coordinates in Json as [[x,y],[x,y]]
    public static final String AREATYPE_HEATMAP = "heatmap"; // export coordinates in Json as [[x,y],[x,y]]
    public static final String AREATYPE_POLYGON = "polygon"; // export coordinates in Json as [[[x,y],[x,y]]]

    public static final int STYLE_POINT = 0;
    public static final int STYLE_TRIANGLE = 1;
    public static final int STYLE_SQUARE = 2;
    public static final int STYLE_RECTANGLE = 3;
    public static final int STYLE_HEXAGON = 4;
    public static final int STYLE_STAR = 5;
    public static final int DEFAULT_STYLE = STYLE_HEXAGON;

    public Area(String areaType, String coordinateType) {
        this.type = areaType;
        this.coordinateType = coordinateType;
        this.coordinates = new ArrayList<>();
    }

    public Area(String areaType, Coordinate coord, int polygonStyle, double size) {
        this(areaType, coord.getCoordType());
        switch (polygonStyle) {
            case STYLE_POINT:
                addPoint(coord);
                break;
            case STYLE_TRIANGLE:
                addTriangle(coord, size);
                break;
            case STYLE_SQUARE:
            case STYLE_RECTANGLE:
                addRectangle(coord, size);
                break;
            case STYLE_STAR:
                addStar(coord, size);
                break;
            case STYLE_HEXAGON:
            default:
                addHexagon(coord, size);
        }
    }

    Area(Coordinate coord, Icon icon) {
        this(Area.AREATYPE_ICON, coord.getCoordType());
        addPoint(coord);
        this.icon = icon;
    }

    public void addPoint(Coordinate coord) {
        if (coord != null) {
            coordinates.add(coord.copy());
        }
    }

    public void addTriangle(Coordinate coord, double size) {
        if (coord != null) {
            double a = size / 2;
            coordinates.add(coord.copy().offset(0, +a));
            coordinates.add(coord.copy().offset(-a, -a));
            coordinates.add(coord.copy().offset(+a, -a));
        }
    }

    public void addRectangle(Coordinate coord, double size) {
        if (coord != null) {
            double a = size / 2;
            coordinates.add(coord.copy().offset(-a, -a));
            coordinates.add(coord.copy().offset(+a, -a));
            coordinates.add(coord.copy().offset(+a, +a));
            coordinates.add(coord.copy().offset(-a, +a));
        }
    }

    public void addHexagon(Coordinate coord, double size) {
        if (coord != null) {
            double a = size / 2;
            coordinates.add(coord.copy().offset(-a - a / 2, 0));
            coordinates.add(coord.copy().offset(0 - a / 2, -a));
            coordinates.add(coord.copy().offset(+a - a / 2, -a));
            coordinates.add(coord.copy().offset(2 * a - a / 2, 0));
            coordinates.add(coord.copy().offset(+a - a / 2, +a));
            coordinates.add(coord.copy().offset(0 - a / 2, +a));
        }
    }

    public void addStar(Coordinate coord, double size) {
        if (coord != null) {
            final double[] dx = new double[]{3, 5, 1, 0, -1, -5, -3, -8, -3, -5, -1, 0, 1, 5, 3, 8};
            final double[] dy = new double[]{1, 5, 3, 8, 3, 5, 1, 0, -1, -5, -3, -8, -3, -5, -1, 0};
            double a = size / 16;
            for (int i = 0; i < dy.length; i++) {
                coordinates.add(coord.copy().offset(a * dy[i], a * dx[i]));
            }
        }
    }

    public void addLine(List<Coordinate> coordinates) {
        if (coordinates != null) {
            for (Coordinate coord : coordinates) {
                addPoint(coord);
            }
        }
    }


}
