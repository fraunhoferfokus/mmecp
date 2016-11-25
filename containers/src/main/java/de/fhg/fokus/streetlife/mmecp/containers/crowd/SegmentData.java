package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;

import java.util.*;

/**
 * Created by csc on 20.11.2015.
 */
public class SegmentData {

    // public static final String TRIPID = "tripID";

    private String tripUid;
    private String origin;
    private String destination;
    private String trafficType;
    private String timeStamp;
    private List<Coordinate> coordinates = Collections.emptyList();
    public int detailed;


    public SegmentData(String tripUid, String origin, String destination, String trafficType,
                       List<Coordinate> coordinates, String timeStamp, int detailed) {
        this.tripUid = tripUid;
        this.origin = origin;
        this.destination = destination;
        this.trafficType = trafficType;
        this.coordinates = coordinates;
        this.timeStamp = timeStamp;
        this.detailed = detailed;
    }


    public int length() {return coordinates.size();}
    public Coordinate startCoordinate() {return (coordinates.isEmpty()) ? null : coordinates.get(0);}
    public Coordinate endCoordinate() {return (coordinates.isEmpty()) ? null : coordinates.get(coordinates.size()-1);}

    public String getTripUid() {return tripUid;}
    public String getOrigin() {return origin;}
    public void setOrigin(String origin) {this.origin = origin;}
    public String getDestination() {return destination;}
    public void setDestination(String destination) {this.destination = destination;}
    public String getTrafficType() {return trafficType;}
    public List<Coordinate> getCoordinates() {return coordinates;}

    public Coordinate getStartCoordinate() {
        if (coordinates.size() >= 1) {
            return coordinates.get(0);
        } else {
            return new GpsCoordinate(0,0);
        }
    }

    public Coordinate getEndCoordinate() {
        if (coordinates.size() >= 1) {
            return coordinates.get(coordinates.size() - 1);
        } else {
            return new GpsCoordinate(0, 0);
        }
    }

    public static Set<String> getTransportsSet(List<SegmentData> tripData) {
        Set set = new HashSet();
        for (SegmentData segment : tripData) {
            set.add(segment.getNiceTrafficType());
        }
        return set;
    }

    public static String formatTransportSet(Set<String> transportsSet) {
        StringBuffer buf = new StringBuffer();
        String sep = "";
        for (String trans : transportsSet) {
            buf.append(sep);
            buf.append(trans);
            sep = ", ";
        }
        return buf.toString();
    }


    private static Map<String, String> trafficTypeMap = new HashMap<>();

    public String getNiceTrafficType() {
        String tt = this.getTrafficType();
        // check in the cache first:
        String rewrite = trafficTypeMap.get(tt);
        if (rewrite == null) {
            // not in the cache, so rewrite it first:
            // check if there's any _second_ capital letter, as e.g. in "CityTrain"
            // if yes, then make it into "City Train"
            if (tt.length() > 1) {
                int pos = 1; // start at second letter!
                while (pos < tt.length()) {
                    char c = tt.charAt(pos);
                    if (c >= 'A' && c <= 'Z') {
                        tt = tt.substring(0, pos) + " " + tt.substring(pos);
                        break;
                    }
                    pos++;
                }
            }
            // finally make "City Train" into "city train"
            rewrite = tt.toLowerCase();
            // add it to the cache before returning:
            trafficTypeMap.put(this.getTrafficType(), rewrite);
        }
        return rewrite;
    }


    public void setStartCoordinate(Coordinate point) {
        if (point != null && this.coordinates.size() >= 1) {
            coordinates.set(0,point);
        }
    }

    public void setEndCoordinate(Coordinate point) {
        if (point != null && this.coordinates.size() >= 1) {
            coordinates.set(coordinates.size()-1,point);
        }
    }


    public boolean isDetailedData() {
        return (this.detailed != 0);
    }

    public boolean noOrigin() {return no(origin);}
    public boolean noDestination() {return no(destination);}
    public boolean noTimeStamp() {return no(timeStamp);}

    private boolean no(String s) {
        return (s == null) || (s.isEmpty()) || s.equals("null");
    }


    public String getTimeStamp() {
        return timeStamp;
    }
}
