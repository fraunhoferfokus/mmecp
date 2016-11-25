package de.fhg.fokus.streetlife.mmecp.containers.coord;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by csc on 23.10.2015.
 * this class is base for UtmCoordinate and GpsCoordinate classes
 */
public abstract class Coordinate {

    public static final String TYPE_GPS = "GPS";
    public static final String TYPE_UTM = "UTM";

    @JsonIgnore
    public abstract String getCoordType();

    public abstract Coordinate copy();
    public abstract Coordinate offset(double y, double x);

    @JsonIgnore
    public abstract Coordinate getIntermediatePointTo(Coordinate point2);

}
