package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.SegmentData;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.*;

import java.util.List;

/**
 * Created by csc on 13.08.2015 for use case: generate output JSON for display of VMZ traffic counters
 */
public class CrowdSourceBikeAccData extends MapObject {

    public CrowdSourceBikeAccData() {
        super();
    }

    public boolean hasBikeTransport = false;

    private static final double SIZE = 0.002;
    public  static final String OBJTYPE = "dangerousPoints";
    // private static final String STREET = "street";

    public CrowdSourceBikeAccData(String objectID, String objType, Integer uid) {
        super();

        this.type = "mapobject";
        this.objectID = objectID + uid.toString();
        this.objectType = objType;
        this.objectSubtype = "Dp";
        this.description = "via_crowdsourcing";

        this.location.type = "Point";
        this.location.coordinates.add(13.13131313); // dummy data
        this.location.coordinates.add(52.52525252); // dummy data
    }

    public CrowdSourceBikeAccData(String objectID, String objType, String streetName, int accidents, Integer uid) {
        this(objectID, objType, uid);
        // this.elements.add(new Attribute(STREET, streetName));
        if (objType != null && !objType.isEmpty()) {
            this.addElement(new Attribute(objType, accidents));
        }
    }

    public CrowdSourceBikeAccData(String objectID, String objType, SegmentData segment) {
        this(objectID, objType, segment.hashCode());
        if (segment != null) {
            // withAttribute(SegmentData.TRIPID, segment.getTripUid());
            withAttributeOmitNull("transport", segment.getNiceTrafficType());
            withAttributeOmitNull("start", segment.getOrigin());
            withAttributeOmitNull("destination", segment.getDestination());
            withAttributeOmitNull("time stamp", segment.getTimeStamp());
            // Color col = (segment.isDetailedData()) ? Color.YELLOW : Color.forTrafficType(segment.getTrafficType(),/*mode*/1).withAlpha(0.9f);
            Color col = (Color.forTrafficType(segment.getTrafficType(),/*mode*/1, segment.isDetailedData())).withAlpha(0.9f);
            withLineThrough(segment.getCoordinates(), col);
        }
    }


    public CrowdSourceBikeAccData(String objectID, String objType, List<SegmentData> trip) {
        this(objectID, objType, (trip != null) ? trip.hashCode(): new Double(Math.random()).hashCode());
        if (trip != null && trip.size() >= 1) {
            // withAttribute(SegmentData.TRIPID, trip.get(0).getTripUid());
            String transports = SegmentData.formatTransportSet(SegmentData.getTransportsSet(trip));
            this.hasBikeTransport = transports.toLowerCase().contains("bike");
            withAttributeOmitNull("transport", transports);
            withAttributeOmitNull("start", trip.get(0).getOrigin());
            withAttributeOmitNull("destination", trip.get(trip.size() - 1).getDestination());
            withAttributeOmitNull("time stamp", trip.get(0).getTimeStamp());

            boolean first = true;
            for (SegmentData segment : trip) {
                if (first == true) { // skip first segment
                    first = false;
                } else {
                    // for each following segment, place an anchor icon at the junction to the next segment
                    withIconAt(segment.getStartCoordinate(), Area.Icon.ROUTELINK_ICON);
                }
            }
            withIconAt(trip.get(0).getStartCoordinate(), Area.Icon.ROUTESTART_ICON);
            withIconAt(trip.get(trip.size() - 1).getEndCoordinate(), Area.Icon.ROUTEEND_ICON);

            for (SegmentData segment : trip) {
                // Color col = (segment.isDetailedData()) ? Color.YELLOW : Color.forTrafficType(segment.getTrafficType(),/*mode*/1).withAlpha(0.9f);
                Color col = (Color.forTrafficType(segment.getTrafficType(),/*mode*/1,segment.isDetailedData())).withAlpha(0.9f);
                withLineThrough(segment.getCoordinates(), col);
            }

            this.setGroupIdAll(trip.get(0).getTripUid());
        }
    }


    public MapObject withTrianglesAt(List<Coordinate> coordinates) {
        if (coordinates != null && !coordinates.isEmpty()) {
            this.withMarkersAt(coordinates, Area.STYLE_TRIANGLE, SIZE);
        }
        return this;
    }

    public MapObject withStarsAt(List<Coordinate> coordinates) {
        if (coordinates != null && !coordinates.isEmpty()) {
            this.withMarkersAt(coordinates, Area.STYLE_STAR, SIZE);
        }
        return this;
    }


    static double t = 0; // coordinate offset, increases per method call
    public static CrowdSourceBikeAccData makeExample() {
        CrowdSourceBikeAccData data = new CrowdSourceBikeAccData();

        data.type = "mapobject";
        data.objectID = "BER_DangPoint _ 055 - " + Integer.toString((int) (Math.random() * 10000));
        data.objectType = OBJTYPE;
        data.objectSubtype = "node_based";
        data.description = "via_crowdsourcing";

        data.location.type = "Point";
        data.location.coordinates.add(13.286821); // dummy data
        data.location.coordinates.add(52.518842); // dummy data

        // data.elements.add(new Attribute(STREET, "Manteuffelstr."));
        data.addElement(new Attribute(OBJTYPE, "456"));

        MapArea ma = new MapArea(Area.AREATYPE_POLYGON, Coordinate.TYPE_GPS);
        ma.color = new Color().random();

        ma.area.addHexagon(new GpsCoordinate(52.523595 + t, 13.3491834 + t), SIZE);
        data.addElement(ma);

        t += 0.001;

        return data;
    }


    public static MapObjectSet makeExampleList() {
        MapObjectSet dataset = new MapObjectSet();

        dataset.add(CrowdSourceBikeAccData.makeExample());
        dataset.add(CrowdSourceBikeAccData.makeExample());
        dataset.add(CrowdSourceBikeAccData.makeExample());

        return dataset;
    }


}
