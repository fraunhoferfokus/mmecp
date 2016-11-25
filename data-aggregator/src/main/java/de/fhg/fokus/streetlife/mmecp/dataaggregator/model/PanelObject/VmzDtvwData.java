package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import com.vividsolutions.jts.geom.MultiLineString;
import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.UtmCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Comparator;
import java.util.List;

/**
 * Created by csc on 13.08.2015 for use case: generate output JSON for display of VMZ traffic counters
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "VmzDtvwDataTypeIdResolver")
//@JsonTypeIdResolver(VmzDtvwDataTypeIdResolver.class)
public class VmzDtvwData extends MapObject {

    // private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    // private static final Logger LOG = LoggerFactory.getLogger(VmzDtvwData.class);

    public VmzDtvwData() {
        super();
    }

    private static final int SIZE = 120;
    public  static final String TRAFFICRATE = "traffic_rate";
    private static final String STREET = "street";

    @JsonIgnore
    private Long trafficRate;

    public VmzDtvwData(String objectID, String crossingID, String streetName, Long trafficRate,
                       Coordinate coord, Integer uid)
    {
        this(objectID, crossingID, streetName, trafficRate, coord, uid,
                Area.AREATYPE_POLYGON, Area.DEFAULT_STYLE);
    }

    public VmzDtvwData(String objectID, String crossingID, String streetName, Long trafficRate,
                       Coordinate coord, Integer uid, String areaType, int polygonStyle)
    {
        super();

        this.type = "mapobject";
        this.objectID = objectID;
        this.objectType = "TrafficCounters";
        this.objectSubtype = "DTVw" + uid.toString();
        this.description = "via_VMZ";
        this.trafficRate = trafficRate;

        this.location.type = "Point";
        this.location.coordinates.add(13.13131313);
        this.location.coordinates.add(52.52525252);

        this.addElement(new Attribute("crossing_number", crossingID));
        this.addElement(new Attribute(STREET, streetName));
        this.addElement(new Attribute(TRAFFICRATE, trafficRate));

        if (coord != null) {
            this.addElement(new MapArea(areaType, coord, polygonStyle, SIZE));
        }
    }


    public VmzDtvwData(String objectID, String crossingID, String streetName, Long trafficRate, MultiLineString coll, Integer uid) {
        this(objectID, crossingID, streetName, trafficRate, UtmCoordinate.convertGeoJsonMultiLineString(coll), uid);
    }

    public VmzDtvwData(String objectID, String crossingID, String streetName, Long trafficRate, List<List<Coordinate>> lines, Integer uid) {
        this(objectID, crossingID, streetName, trafficRate, null, uid, "", 0);
        for (List<Coordinate> points : lines) {
            this.withLineThrough(points);
        }
    }

    static int t = 0; // coordinate offset, increases per method call


    public static VmzDtvwData makeExample() {
        VmzDtvwData data = new VmzDtvwData();

        data.type = "mapobject";
        data.objectID = "BER_Crossing _ 04 - " + Integer.toString((int) (Math.random() * 10000));
        data.objectType = "TrafficCounters";
        data.objectSubtype = "DTVw";
        data.description = "via_VMZ";

        data.location.type = "Point";
        data.location.coordinates.add(13.286821);
        data.location.coordinates.add(52.518842);

        data.addElement(new Attribute("crossing_number", "1234"));
        data.addElement(new Attribute(TRAFFICRATE, "12345"));
        data.addElement(new Attribute("co2 (est.)", "123"));
        data.addElement(new Attribute(STREET, "Manteuffelstr."));

        MapArea ma = new MapArea(Area.AREATYPE_POLYGON, Coordinate.TYPE_UTM);
        ma.color = new Color().random();

        ma.area.coordinates.add(new UtmCoordinate(5813796+t, 389702+t));
        ma.area.coordinates.add(new UtmCoordinate(5813896+t, 389702+t));
        ma.area.coordinates.add(new UtmCoordinate(5813896+t, 389802+t));
        ma.area.coordinates.add(new UtmCoordinate(5813796+t, 389802+t));
        data.addElement(ma);

        t += 100;
        return data;
    }


    public static MapObjectSet makeExampleList(int num) {
        MapObjectSet dataset = new MapObjectSet();
        for (int i=0; i<num; i++) {
            dataset.add(VmzDtvwData.makeExample());
        }
        return dataset;
    }


    public static int compare(final VmzDtvwData o1, final VmzDtvwData o2) {
        // LOG.info("Comparing two VmzDtvwData ....................");
        return o1.trafficRate.compareTo(o2.trafficRate);
    }

    public static Comparator<VmzDtvwData> comparator = new Comparator<VmzDtvwData>(){
        @Override
        public int compare(final VmzDtvwData o1, final VmzDtvwData o2) {
            return VmzDtvwData.compare(o1, o2);
        }
    };


}
