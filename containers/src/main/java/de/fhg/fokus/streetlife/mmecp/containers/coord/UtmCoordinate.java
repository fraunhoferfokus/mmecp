package de.fhg.fokus.streetlife.mmecp.containers.coord;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by csc on 23.10.2015.
 */
public class UtmCoordinate extends Coordinate {

    private static final String ZONE = "33N";

    String zone;
    double N, E;

    public UtmCoordinate(double n, double e) {
        this(ZONE, n, e);
    }

    public UtmCoordinate(String zone, double n, double e) {
        this.zone = zone;
        this.E = n;
        this.N = e;
    }

    public UtmCoordinate(com.vividsolutions.jts.geom.Point p) {this(p.getY(), p.getX());}

    public UtmCoordinate(com.vividsolutions.jts.geom.Coordinate coord) {this(coord.y, coord.x);}

    public Coordinate copy() {
        return new UtmCoordinate(this.zone, this.N, this.E);
    }

    public Coordinate offset(double north, double east) {
        this.N += north;
        this.E += east;
        return this;
    }

    public Coordinate getIntermediatePointTo(Coordinate otherPoint) {
        if (otherPoint instanceof UtmCoordinate) {
            UtmCoordinate op = (UtmCoordinate) otherPoint;
            if (this.getZone().equals(op.getZone())) {
                return new UtmCoordinate(this.getZone(), (this.getN() + op.getN()) / 2, (this.getE() + op.getE()) / 2);
            } else {
                return this.copy();
                // TODO: implement averaging of points from two different zones!
            }
        } else {
            return this.copy();
        }
    }


    public static List<Coordinate> convertGeoJsonLineString(com.vividsolutions.jts.geom.Coordinate[] coords) throws IOException {
        if (coords == null) {return null;}
        List list = new ArrayList(coords.length);
        for (com.vividsolutions.jts.geom.Coordinate coord : coords) {
            list.add(new UtmCoordinate(coord));
        }
        return list;
    }

    public static List<List<Coordinate>> convertGeoJsonMultiLineString(MultiLineString mls) {
        if (mls == null) {return null;}
        List<List<Coordinate>> list = new ArrayList(mls.getNumGeometries());
        for (int i=0; i < mls.getNumGeometries(); i++) {
            Geometry geo = mls.getGeometryN(i);
            List list2 = new ArrayList(geo.getNumPoints());
            for (com.vividsolutions.jts.geom.Coordinate coord : geo.getCoordinates()) {
                list2.add(new UtmCoordinate(coord));
            }
            list.add(list2);
        }
        return list;
    }

    public String toString() {
        return Util.toJsonString(this);
    }

    public String getCoordType() {return TYPE_UTM;}

    public String getZone() {return zone;}
    public double getN() {return N;}
    public double getE() {return E;}

}
