package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.containers.util.SimpleCache;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by csc on 13.08.2015 for use case: generate output JSON for display of VMZ traffic counters
 */
public class VmzBikeRouteData extends VmzBikeAccData {

    // private static final Logger LOG = LoggerFactory.getLogger(VmzBikeRouteData.class);

    private static final ObjectMapper Mapper = new ObjectMapper();

    private static SimpleCache<MapObjectSet> Cache = new SimpleCache<>();


    public VmzBikeRouteData() {
        super();
    }


    public static MapObjectSet readExampleDataSet(Event event) {
        if (event == null) {return null;}

        String select = event.getContext().getSelect();
        MapObjectSet dataSet = new MapObjectSet();
        String suffix;
        Color color;

        if (select != null && select.startsWith("example#unsafe")) {
            suffix = "u";
            color = Color.RED;
        } else if (select != null && select.startsWith("example#safe")) {
            suffix = "s";
            color = Color.BLUE;
        } else {
            return dataSet;
        }
        color.setAlpha(0.8f);

        // look in cache first
        String cacheID = "VmzBikeAccData_" + select;
        MapObjectSet cachedResult = Cache.get(cacheID);
        if (cachedResult != null) {
            return cachedResult;
        }

        final int[] ids = {2,3,5,6,7};
        for (int i : ids) {
            String fileName = "geojson/map" + i + suffix + ".json"; // GeoJson style file name

            JsonNode root;
            try {
                String contents = Util.loadResourceAsString(fileName);
                if (contents == null || contents.isEmpty()) {continue;}
                root = Mapper.readTree(contents);
            } catch (IOException e) {
                continue;
            }

            for (JsonNode geometryJson : root.findValues("geometry")) {
                if (geometryJson.get("type").asText().equals("LineString")) {
                    List<Coordinate> points = GpsCoordinate.parseLineStringGpsCoordinatesFromGeoJsonGeometry(geometryJson);
                    String id = "objID_su_" + /*suffix +*/ i;
                    dataSet.add(new MapObject(id, event.getSubType()).withRouteThrough(points, color, id));
                }
            }
        }
        dataSet.setSubTypeAll(event.getSubType());
        Cache.put(cacheID, dataSet);
        return dataSet;
    }


}
