package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import com.vividsolutions.jts.geom.MultiLineString;
import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.UtmCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.UidGenerator;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl.ShapeFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by csc on 13.08.2015 for use case: generate output JSON for display of VMZ traffic counters
 */
public class VmzBikeAccData extends MapObject {

    public VmzBikeAccData() {
        super();
    }

    private static final int SIZE = 100;
    public  static final String ACCIDENTS = "accidents";
    private static final String STREET = "street";

    public VmzBikeAccData(String objectID, String streetName, Long accidents,
                          Coordinate coord, Integer uid, boolean severe) {
        this(objectID, streetName, accidents, uid);

        if (coord != null) {
            MapArea ma = new MapArea(Area.AREATYPE_POLYGON, Coordinate.TYPE_UTM);
            ma.area.addHexagon(coord, SIZE);
            ma.color.colorize(accidents, (severe)? Color.FOKUS_COLOR_SCHEME_SEVERE:Color.FOKUS_COLOR_SCHEME);
            this.addElement(ma);
        }
    }

    public VmzBikeAccData(String objectID, String streetName, Long accidents, Integer uid) {
        super();

        this.type = "mapobject";
        this.objectID = objectID;
        this.objectType = ACCIDENTS;
        this.objectSubtype = "Ba" + uid.toString();
        this.description = "via_VMZ";

        this.location.type = "Point";
        this.location.coordinates.add(13.13131313);
        this.location.coordinates.add(52.52525252);

        this.addElement(new Attribute(STREET, streetName));
        this.addElement(new Attribute(ACCIDENTS, accidents));
    }


    public VmzBikeAccData(String nodeId, String streetName, Long accidents, MultiLineString coll, Integer uid, boolean severe) {
        this(nodeId, streetName, accidents, UtmCoordinate.convertGeoJsonMultiLineString(coll), uid, severe);
    }

    public VmzBikeAccData(String nodeId, String streetName, Long accidents, List<List<Coordinate>> lines, Integer uid, boolean severe) {
        this(nodeId, streetName, accidents, uid);
        Color col = Color.LookupColor(accidents, (severe)? Color.FOKUS_COLOR_SCHEME_SEVERE:Color.FOKUS_COLOR_SCHEME);
        for (List<Coordinate> points : lines) {
            this.withLineThrough(points, col);
        }
    }

    static int t = 0; // coordinate offset, increases per method call

    public static VmzBikeAccData makeExample() {
        VmzBikeAccData data = new VmzBikeAccData();

        data.type = "mapobject";
        data.objectID = "BER_Crossing _ 044 - " + Integer.toString((int) (Math.random() * 10000));
        data.objectType = ACCIDENTS;
        data.objectSubtype = "node_based";
        data.description = "via_VMZ";

        data.location.type = "Point";
        data.location.coordinates.add(13.286821);
        data.location.coordinates.add(52.518842);

        data.addElement(new Attribute(STREET, "Manteuffelstr."));
        data.addElement(new Attribute(ACCIDENTS, "123"));

        MapArea ma = new MapArea(Area.AREATYPE_POLYGON, Coordinate.TYPE_GPS);
        ma.color = new Color().random();
        ma.color.setAlpha(0.6f);
        // ma.area.addStar(new UtmCoordinate(5813796 + t, 389702 + t), 200);
        ma.area.addStar(new GpsCoordinate(52.47018 + (double)t/100000, 13.38648 + (double)t/100000), 0.002);
        data.addElement(ma);

        t += 100;
        return data;
    }


    public static MapObjectSet makeExampleList(int n) {
        MapObjectSet dataset = new MapObjectSet();
        for(int i=0; i<n; i++) {
            dataset.add(VmzBikeAccData.makeExample());
        }
        return dataset;
    }


    public static MapObjectSet readAccidentDataFromDbfResource(String resourceName, Event event) throws Exception {

        URL url1 = ShapeFileReader.getResourceUrl(resourceName, ".dbf");
        if( url1 == null ) {
            throw new java.io.FileNotFoundException("File resource '" + resourceName + ".dbf" + " cannot be opened.");
        }
        ReadableByteChannel channel = Channels.newChannel(url1.openStream());
        DbaseFileReader dbfReader =  new DbaseFileReader(channel, false, Charset.forName("ISO-8859-1"));
        DbaseFileHeader header = dbfReader.getHeader();

        System.out.println("number of fields is: " + header.getNumFields());
        System.out.println("number of records is: " + header.getNumRecords());
        System.out.println("header is: " + header.toString());

        int counterIndex = VmzBikeAccData.findCounterColumnIndex(header);
        if( counterIndex == -1 ) {  // no counter column in table?
            return null;
        }
        Map<String,ShapeFileReader.NamedMultiLineString> shapeFileIndexMap;
        URL url2 = ShapeFileReader.getDefaultBerShapeFileUrl();
        // URL url2 = ShapeFileReader.getDefaultBerDnShapeFileUrl();

        int idIndex = VmzBikeAccData.findIdColumnIndex(header, "LINK_ID");
        if( idIndex != -1 ) {
            shapeFileIndexMap = ShapeFileReader.generateLinkNodeIndexFromFile(url2);
        } else {
            idIndex = VmzBikeAccData.findIdColumnIndex(header,"NODE_ID");
            if( idIndex != -1 ) {
                shapeFileIndexMap = ShapeFileReader.generateToNodeIndexFromFile(url2);
            } else { // neither link id column not node id column present
                return null;
            }
        }

        int found = 0;
        int not_found = 0;

        Map<String, Long> countersMap = new HashMap(header.getNumRecords() * 2);

        // account the accidents' counters into a local map of counters
        while ( dbfReader.hasNext() )
        {
            final Object[] fields = dbfReader.readEntry();

            String nodeId  = (String) fields[idIndex];
            Long accidents = (Long)   fields[counterIndex];

            // LookupColor entry for a given nodeId
            Long count = countersMap.get(nodeId);

            if( count == null ) { // no entry for this nodeId yet, then add a new entry
                countersMap.put(nodeId, accidents);
            } else { // if entry exists then put sum of old and new entry under same index into the map
                countersMap.put(nodeId, accidents + count);
            }
        }

        MapObjectSet dataset = new MapObjectSet(event);
        Integer uid = UidGenerator.getNewUid();

        for (Map.Entry<String, Long> entry : countersMap.entrySet()) {
            String nodeId = entry.getKey();
            Long accidents = entry.getValue();

            // LookupColor a matching NamedMultiLineString object to get street name and coordinates for a node (by nodeId)
            ShapeFileReader.NamedMultiLineString ngc = shapeFileIndexMap.get(nodeId);
            if (ngc != null) {
                found += 1;
                if (event.isStreetType()) {
                    dataset.add(new VmzBikeAccData(nodeId, ngc.streetName, accidents, ngc.mls, uid, event.isSevereType()));
                } else {
                    dataset.add(new VmzBikeAccData(nodeId, ngc.streetName, accidents, new UtmCoordinate(ngc.getCenterPoint()), uid, event.isSevereType()));
                }
            } else {
                not_found += 1;
            }
        }

        dbfReader.close();
        channel.close();

        //String json = dataset.toJsonString();
        //System.out.println(json);

        System.out.println("found node IDs: " + found);
        System.out.println("not found node IDs: " + not_found);

        return dataset;
    }


    private static int findColumnIndex(DbaseFileHeader header, String columnName) {

        if( columnName == null || columnName.isEmpty() || header == null || header.getNumFields() < 1)
            return -1; // signal a parameter error

        for( int i=0; i<header.getNumFields(); i++) {
            if( columnName.equals(header.getFieldName(i)) )
                return i;
        }
        // no match
        return -1;
    }


    private static int findIdColumnIndex(DbaseFileHeader header, String colNameStart) {

        if( header == null || header.getNumFields() < 1)
            return -1; // signal a parameter error

        for( int i=0; i<header.getNumFields(); i++) {
            String colName = header.getFieldName(i);
            if( colName.startsWith(colNameStart) )
                return i;
        }
        // no match
        return -1;
    }


    private static int findCounterColumnIndex(DbaseFileHeader header) {

        if( header == null || header.getNumFields() < 1)
            return -1; // signal a parameter error

        for( int i=0; i<header.getNumFields(); i++) {
            String colName = header.getFieldName(i);
            if( colName.startsWith("ANZ") )
                return i;
        }
        // no match
        return -1;
    }


}
