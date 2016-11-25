package de.fhg.fokus.streetlife.mmecp.containers.coord;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by csc on 23.10.2015.
 */
@JsonSerialize(using = GpsCoordinate.GpsCoordinateSerializer.class)
@JsonDeserialize(using = GpsCoordinate.GpsCoordinateDeserializer.class)
public class GpsCoordinate extends Coordinate {

    private static final Logger LOG = LoggerFactory.getLogger(Coordinate.class);


    public static class GpsCoordinateSerializer extends JsonSerializer<GpsCoordinate> {

        public void serialize(GpsCoordinate coord, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartArray();
            jgen.writeNumber(coord.getLon());
            jgen.writeNumber(coord.getLat());
            jgen.writeEndArray();
            // jgen.flush();
        }

        public Class<GpsCoordinate> handledType() {return GpsCoordinate.class;}
    }

    public static class GpsCoordinateDeserializer extends JsonDeserializer<GpsCoordinate> {

        public GpsCoordinate deserialize(JsonParser jpar, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jpar.getCodec().readTree(jpar);
            return new GpsCoordinate(node);
        }

        public Class<GpsCoordinate> handledType() {return GpsCoordinate.class;}
    }


    double lat, lon;

    public GpsCoordinate(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public GpsCoordinate(com.vividsolutions.jts.geom.Coordinate coord) {
        this((coord==null)?0:coord.y, (coord==null)?0:coord.x);
    }

    public GpsCoordinate(JsonNode node) throws IOException {

        if (node == null) {throw new IOException("GpsCoordinate not readable from JsonNode: null");}

        // at first try to read from just an array [lon,lat]
        if (node.isArray()) {
            Iterator<JsonNode> iter = node.iterator();
            if (iter.hasNext()) {
                this.lon = iter.next().asDouble();
            } else {
                throw new IOException("GpsCoordinate longitude not readable from Array-style JsonNode: " + node.toString());
            }
            if (iter.hasNext()) {
                this.lat = iter.next().asDouble();
            } else {
                throw new IOException("GpsCoordinate latitude not readable from Array-style JsonNode: " + node.toString());
            }
            return;
        }

        // LOG.info("CoordJson = {}", node.toString());

        // try to parse GeoJson style syntax first:
        // i.e. { "type" : "Point", "coordinates" : [12.5492757,52.4207061] }

        JsonNode type = node.get("type");
        if (type != null && type.asText().equals("Point")) {
            JsonNode coordinates = node.get("coordinates");
            if (coordinates == null) {throw new IOException("GpsCoordinate not readable from GeoJson-style JsonNode: " + node.toString());}
            // else
            Iterator<JsonNode> iter = coordinates.iterator();
            if (iter.hasNext()) {
                this.lon = iter.next().asDouble();
            } else {
                throw new IOException("GpsCoordinate longitude not readable from GeoJson-style JsonNode: " + node.toString());
            }
            if (iter.hasNext()) {
                this.lat = iter.next().asDouble();
            } else {
                throw new IOException("GpsCoordinate latitude not readable from GeoJson-style JsonNode: " + node.toString());
            }
        } else {
            // not GeoJson-style but with explicit, named lon/lat fields:
            // e.g.  {"system":"WGS_84","longitude":13.356269836425781,"latitude":52.48455810546875}
            // or    {"lon":13.356269836425781,"lat":52.48455810546875}
            // or    {"gps_longitude":13.34563837,"gps_latitude":52.52291837,...}

            JsonNode lonnode = tryReading(node, "longitude", "lon", "gps_longitude");
            if (!lonnode.isNumber()) throw new IOException("not a valid GPS longitude value: " + lonnode.asText());

            JsonNode latnode = tryReading(node, "latitude", "lat", "gps_latitude");
            if (!latnode.isNumber()) throw new IOException("not a valid GPS latitude value: " + latnode.asText());

            this.lon = lonnode.asDouble();
            this.lat = latnode.asDouble();
        }
    }

    private JsonNode tryReading(JsonNode node, String str1, String str2, String str3) throws IOException {
        JsonNode coord = node.get(str1);
        if (coord == null) {
            coord = node.get(str2);
            if (coord == null) {
                coord = node.get(str3);
                if (coord == null) {
                    throw new IOException("GpsCoordinate " + str1 + " not readable from JsonNode: " + node.toString());
                }
            }
        }
        return coord;
    }


    public Coordinate copy() {
        return new GpsCoordinate(this.lat, this.lon);
    }

    public Coordinate offset(double lat, double lon) {
        this.lat += lat;
        this.lon += lon;
        return this;
    }

    public Coordinate getIntermediatePointTo(Coordinate otherPoint) {
        if (otherPoint instanceof GpsCoordinate) {
            GpsCoordinate op = (GpsCoordinate)otherPoint;
            return new GpsCoordinate((this.getLat()+op.getLat())/2,(this.getLon()+op.getLon())/2);
        } else {
            return this.copy();
        }
    }

    public String getCoordType() {return Coordinate.TYPE_GPS;}

    public String toString() {
        return Util.toJsonString(this);
/*
        final org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
        final ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(this);  // return JSON representation
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
*/
    }

    public static List<Coordinate> parseLineStringGpsCoordinatesFromGeoJsonGeometry(JsonNode geometry) {
        return parseLineStringGpsCoordinatesFromGeoJsonGeometry(geometry, new ArrayList());
    }

    public static List<Coordinate> parseLineStringGpsCoordinatesFromGeoJsonGeometry(JsonNode geometry, List pointsList) {
        if (geometry.get("type").asText().equals("LineString")) {
            JsonNode coordinates = geometry.get("coordinates");
            if (coordinates != null) {
                Iterator<JsonNode> iter = coordinates.iterator();
                while (iter.hasNext()) {
                    Iterator<JsonNode> coord = iter.next().iterator();
                    double lon = coord.next().asDouble();
                    double lat = coord.next().asDouble();
                    pointsList.add(new GpsCoordinate(lat,lon));
                }
            }
        }
        return pointsList;
    }


    public static List<List<Coordinate>> convertGeoJsonMultiLineString(MultiLineString mls) {
        if (mls == null) {return null;}
        List<List<Coordinate>> list = new ArrayList(mls.getNumGeometries());
        for (int i=0; i < mls.getNumGeometries(); i++) {
            Geometry geo = mls.getGeometryN(i);
            List list2 = new ArrayList(geo.getNumPoints());
            for (com.vividsolutions.jts.geom.Coordinate coord : geo.getCoordinates()) {
                list2.add(new GpsCoordinate(coord));
            }
            list.add(list2);
        }
        return list;
    }


    public static List<Coordinate> parseGpsCoordinates(JsonNode node) {
        return (node == null) ? null : parseGpsCoordinates(node, new ArrayList<Coordinate>());
    }

    public static List<Coordinate> parseGpsCoordinates(JsonNode node, List<Coordinate> coordinates) {
        if (node == null || coordinates == null) return null;

        // at first distinguish between the two supported formats
        if (node.isArray()) {
            // expect either this format:
            // [{"system":"WGS_84","longitude":13.14664,"latitude":52.41494},{"system":"WGS_84","longitude":13.14605,"latitude":52.41459},{"system":"WGS_84","longitude":13.14545,"latitude":52.41533},{"system":"WGS_84","longitude":13.14561,"latitude":52.41548}],"segmentId":"1","description":null,"origin":{"time":"2014-09-30T14:16:00+0200","address":"Schulstraße 6, 14109 Berlin, Germany","location":{"system":"WGS_84","longitude":13.1466423,"latitude":52.4149433}},"cost":{"duration":{"unit":"Minute","value":2.0833333333333335},"distance":{"unit":"Meter","value":168},"price":{"unit":"Euro","value":0},"emission":{"unit":"Gram","value":0},"ticketPrice":null},"destination":{"time":"2014-09-30T14:18:05+0200","address":"Chausseestraße 37, 14109 Berlin, Germany","location":{"system":"WGS_84","longitude":13.1456114,"latitude":52.4154826}}}
            for (JsonNode coordAsJson : node) {
                try {
                    coordinates.add(new GpsCoordinate(coordAsJson));
                } catch (IOException e) {
                    // LOG.warn("problem with Json GPS coordinate: " + e.toString());
                    continue;
                }
            }
            return coordinates;
        } else {
            // or, alternatively, expect GeoJson-style:
            // {"type":"LineString","coordinates":[[12.54928,52.42071],[12.5496,52.42121],[12.54991,52.42191],[12.54956,52.42193],[12.5493,52.42197],[12.54798,52.42215],[12.54758,52.42154],[12.54722,52.42093],[12.54704,52.42085],[12.54692,52.42085],[12.54638,52.42097]]},"segmentId":"1","description":null,"origin":{"time":"2015-11-10T13:01:55+01:00","address":"Freiherr-von-Thüngen-Straße 9-10, 14770 Brandenburg an der Havel, Germany","location":{"type":"Point","coordinates":[12.5492757,52.4207061]}
            return parseLineStringGpsCoordinatesFromGeoJsonGeometry(node, coordinates);
        }
    }


    public double getLat() {return lat;}
    public double getLon() {return lon;}

}
