package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by csc on 22.10.2015.
 */
public class TripHistory {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final org.codehaus.jackson.map.ObjectMapper Mapper = new org.codehaus.jackson.map.ObjectMapper();
   // private final ObjectWriter ow = Mapper.writer().withDefaultPrettyPrinter();

    private JsonNode root = Mapper.createObjectNode();
    private int pageCount;

    public TripHistory() {}

    public TripHistory(String json) throws IOException {
        if (json != null && !json.isEmpty()) {
            this.root = Mapper.readTree(json);
        }
    }

    public TripHistory(JsonNode root) {
        if (root != null) {
            this.root = root;
        }
    }

    public static final int MODE_GET_ONLY_SELECTED_TRIPS = 1;
    public static final int MODE_GET_DETAILED_TRIPS_WHERE_POSSIBLE = 2;
    public static final int MODE_GET_ONLY_TRACKED_DATA = 4;
    public static final int MODE_GET_ONLY_DANGEROUS_POINTS = 8;


    public Integer countSafeTripRequests(boolean mode) {
        if (this.root == null) return null;

        JsonNode historyList = this.root.get("historyList");
        if (historyList == null || !historyList.isArray()) return null;

        Integer count = 0;

        Iterator<JsonNode> historyIter = historyList.iterator();
        while (historyIter.hasNext()) {
            JsonNode tripNode = historyIter.next();

            String bikeRountingConstraints = "";
            try {
                JsonNode request = Mapper.readTree(unwrap(SAFEGET(tripNode, "request")));

                JsonNode trafficTypes = SAFEGET(request, "trafficTypes");
                if (!trafficTypes.toString().toLowerCase().contains("bike")) continue;

                JsonNode routingConstraints = SAFEGET(request, "routingConstraints");
                if (!routingConstraints.isArray()) continue;

                Iterator<JsonNode> iter = routingConstraints.iterator();
                while (iter.hasNext()) {
                    JsonNode constraint = iter.next();
                    if (SAFEGET(constraint, "trafficType").asText().toLowerCase().contains("bike")) {
                        bikeRountingConstraints = bikeRountingConstraints + "," + SAFEGET(constraint, "routingConstraintType").asText();
                    }
                }
            } catch (IOException e) {continue;}

            // check and count only if and constraints for trafficType "bike" exist
            if (bikeRountingConstraints.isEmpty()) continue;

            if (mode == true) {
                // count only if roadsafety is among the constraints for bike
                if (bikeRountingConstraints.toLowerCase().contains("roadsafety")) count++;
            } else {
                // count occurence of constraints for bike without safety
                if (!bikeRountingConstraints.toLowerCase().contains("roadsafety")) count++;
            }
        }
        return count;
    }


    public String getSafeTripStatsMessage() {
        Integer safeTripRequests = this.countSafeTripRequests(true);
        Integer unsafeTripRequests = this.countSafeTripRequests(false);
        return "Save Bike Trip requests: " + safeTripRequests + "/" + (safeTripRequests + unsafeTripRequests) + " (" +
                (Math.floor(10000.0 * safeTripRequests / (safeTripRequests + unsafeTripRequests))/100.0) + "%)";
    }


    public Double getSafeTripPercentage() {
        Integer safeTripRequests = this.countSafeTripRequests(true);
        Integer unsafeTripRequests = this.countSafeTripRequests(false);
        return 100.0 * safeTripRequests / (safeTripRequests + unsafeTripRequests);
    }


    public List<List<SegmentData>> getAllTripsTracked(int mode) {
        if (mode < 0) return null;
        if (this.root == null) return null;

        JsonNode historyList = this.root.get("historyList");
        if (historyList == null || !historyList.isArray()) return null;

        List<List<SegmentData>> coordinatesList = new ArrayList<>();

        Iterator<JsonNode> historyIter = historyList.iterator();
        while (historyIter.hasNext()) {
            JsonNode tripNode = historyIter.next();

            try {
                String userOrigin = SAFEGET(tripNode, "userOrigin").asText().replace("Hier", "current position");
                String userDestination = SAFEGET(tripNode, "userDestination").asText().replace("Hier", "current position");
                String timeStamp = SAFEGET(tripNode, "timeStamp").asText().replace('T', ' ');

                String trackedDataJson = unwrap(SAFEGET(tripNode, "trackedData"));
                if (trackedDataJson == null || trackedDataJson.isEmpty() || trackedDataJson.equals("[]")) continue;

                String tripSelectedJson = unwrap(SAFEGET(tripNode, "tripSelected"));
                if (tripSelectedJson == null || tripSelectedJson.isEmpty() || tripSelectedJson.equals("null")) continue;

                JsonNode tripSelectedData = Mapper.readTree(tripSelectedJson);
                String tripUrl = SAFEGET(tripSelectedData, "url").asText();

                JsonNode tripTrackedData = Mapper.readTree(trackedDataJson);

                if (tripTrackedData.isArray()) {
                    List<Coordinate> coords = GpsCoordinate.parseGpsCoordinates(tripTrackedData);
                    if (coords != null && coords.size() >= 4) {
                        SegmentData segment = new SegmentData(tripUrl, userOrigin, userDestination, "n/a", coords, timeStamp, 1);
                        // store tracked path into trips list as a trip with exactly one segment
                        List list = new ArrayList(1);
                        list.add(segment);
                        coordinatesList.add(list);
                    }
                }
            } catch (IOException e) {
                LOG.warn("Json problem: " + e.getMessage());
                continue;
            }
        }
        return coordinatesList;
    }


    private String unwrap(JsonNode node) throws IOException {
        if (node == null) return null;
        return (node.isTextual()) ? Mapper.readValue(node, String.class): "";
    }


    public List<List<SegmentData>> getAllTripsSelected(int mode) throws IOException {
        if (mode < 0) return null;
        if (this.root == null) return null;

        JsonNode historyList = this.root.get("historyList");
        if (historyList == null || !historyList.isArray()) return null;

        List<List<SegmentData>> coordinatesList = new ArrayList<>();

        Iterator<JsonNode> historyIter = historyList.iterator();
        while (historyIter.hasNext()) {
            JsonNode tripNode = historyIter.next();
            String tripSelectedJsonString = unwrap(SAFEGET(tripNode, "tripSelected"));
            if (tripSelectedJsonString == null || tripSelectedJsonString.isEmpty() || tripSelectedJsonString.equals("null")) {
                continue;
            }
            JsonNode segmentsData;
            String tripUrl;
            try {
                JsonNode tripSelectedData = Mapper.readTree(tripSelectedJsonString);
                segmentsData = SAFEGET(tripSelectedData, "segments");
                tripUrl = SAFEGET(tripSelectedData, "url").asText();
            } catch (IOException e) {
                continue;
            }

            List<SegmentData> segmentList = new ArrayList<>();
            Iterator<JsonNode> iter = segmentsData.iterator();
            while (iter.hasNext()) {
                JsonNode segmentData = iter.next();
                try {
                    String trafficType        = SAFEGET(segmentData, "trafficType").asText();
                    String originAddress      = SAFEGET(SAFEGET(segmentData, "origin"),"address").asText();
                    String destinationAddress = SAFEGET(SAFEGET(segmentData, "destination"),"address").asText();
                    // LOG.info("TYPE: " + trafficType + ", FROM: " + originAddress + ", TO: " + destinationAddress);

                    int detailedFlag = 0;
                    List<Coordinate> coordinates = null;

                    // check for wayPointsData first, given the right mode
                    if (mode == MODE_GET_DETAILED_TRIPS_WHERE_POSSIBLE) {
                        JsonNode wayPointsData = segmentData.get("wayPoints");
                        if (wayPointsData != null) {
                            coordinates = GpsCoordinate.parseGpsCoordinates(wayPointsData);
                            if (!coordinates.isEmpty()) {
                                detailedFlag = 1;
                            } else {
                                // fully throw away an empty set of wayPoints
                                coordinates = null;
                            }
                        }
                    }

                    // if no wayPoints were queried and found, then fetch the guidingPoints instead
                    if (coordinates == null) {
                        coordinates = parseGuidingPoints(segmentData);
                    }

                    if (!coordinates.isEmpty()) {
                        segmentList.add(new SegmentData(tripUrl, originAddress, destinationAddress, trafficType,
                                coordinates, null, detailedFlag));
                    }
                } catch (IOException e) {
                    continue;
                }
            }

            try {
                SegmentData seg0 = segmentList.get(0);
                if (seg0.noOrigin()) {
                    seg0.setOrigin(SAFEGET(tripNode, "userOrigin").asText().replace("Hier", "current position"));
                }
                SegmentData segN = segmentList.get(segmentList.size()-1);
                if (segN.noDestination()) {
                    segN.setDestination(SAFEGET(tripNode, "userDestination").asText().replace("Hier", "current position"));
                }
                // String timeStamp = SAFEGET(tripNode, "timeStamp").asText().replace('T', ' ');
            } catch (IOException e) {
                // ignore exception and do nothing about it
            }

            alignSegmentEndpoints(segmentList);
            coordinatesList.add(segmentList);
        }
        return coordinatesList;
    }


    private List<Coordinate> parseGuidingPoints(JsonNode segmentData) throws IOException {
        JsonNode guidingPointsData = SAFEGET(segmentData, "guidingPoints");
        List<Coordinate> coordinates = new ArrayList<>();
        for (JsonNode guidingPointData : guidingPointsData) {
            try {
                JsonNode location = SAFEGETALT(guidingPointData, "location", "timedLocation");
                // finally, access the _inner_ location item:
                JsonNode coordAsJson = SAFEGET(location, "location");
                // parse coordinate Json into POJO and store it:
                coordinates.add(new GpsCoordinate(coordAsJson));
            } catch (IOException e) {
                LOG.warn("erroneous guidingPointData: {}", guidingPointData.toString());
                break;
            }
        }
        return coordinates;
    }


    private void alignSegmentEndpoints(List<SegmentData> segmentList) {
        if (segmentList == null || segmentList.size() < 2) return;

        // sometimes end points of consequitive trip segments do not match exactly
        // to make them match exactly, replace those endpoints by their common average
        try {
            for (int i = 0; i < segmentList.size() - 1; i++) {
                SegmentData seg1 = segmentList.get(i);
                if (seg1 == null) continue;

                SegmentData seg2 = segmentList.get(i + 1);
                if (seg2 == null) continue;

                Coordinate point1 = seg1.endCoordinate();
                if (point1 == null) continue;

                Coordinate point2 = seg2.startCoordinate();
                if (point2 == null) continue;

                Coordinate point3 = point1.getIntermediatePointTo(point2);
                if (point3 == null) continue;

                seg1.setEndCoordinate(point3);
                seg2.setStartCoordinate(point3);
            }
        } catch (Exception e) {
            LOG.error("PROBLEM in alignSegmentEndpoints(): " + e.toString());
        }
    }


    private static JsonNode SAFEGET(JsonNode node, String fieldName) throws IOException {
        if (node == null || fieldName == null) {
            throw new IOException("TripHistory: cannot access Json field, node or field name == null");
        }
        JsonNode result = node.get(fieldName);
        if (result == null) {
            // LOG.error("TripHistory Json parse failure: element \"" + fieldName + "\" not found.");
            throw new IOException("TripHistory Json parse failure: element \"" + fieldName + "\" not found.");
        }
        return result;
    }

    private static JsonNode SAFEGETALT(JsonNode node, String fieldName, String altFieldName) throws IOException {
        if (node == null || fieldName == null || altFieldName == null) {
            throw new IOException("TripHistory: cannot access Json field, node or field name == null");
        }
        JsonNode result = node.get(fieldName);
        if (result == null) {
            result = node.get(altFieldName);
            if (result == null) {
                // LOG.error("TripHistory Json parse failure: neither element \"" + fieldName + "\" nor \""+altFieldName+"\" found.");
                throw new IOException("TripHistory Json parse failure: neither element \"" + fieldName + "\" nor \""+altFieldName+"\" found.");
            }
        }
        return result;
    }


    public List<Coordinate> getAllDangerousPoints() {
        List<Coordinate> coordinates = new ArrayList<>();

        for (JsonNode wrappedCrowdSourcingString : root.findValues("crowdSourcingData")) {
            try {
                String crowdSourcingString = unwrap(wrappedCrowdSourcingString);
                if (crowdSourcingString != null && !crowdSourcingString.isEmpty() && !crowdSourcingString.equals("null")) {

                    JsonNode crowdSourcingNode = Mapper.readTree(crowdSourcingString);
                    JsonNode dangerousPointsList = SAFEGET(crowdSourcingNode, "dangerousPoints");
                    GpsCoordinate.parseGpsCoordinates(dangerousPointsList, coordinates);
                }
            } catch (IOException e) {
                LOG.warn("Problem with parsing coordinates from String: {}", wrappedCrowdSourcingString);
                continue;
            }
        }
        return coordinates;
    }


    public int getPageCount() {
        if (this.root == null) return 0;

        JsonNode pageCount = this.root.get("pageCount");
        if (pageCount == null || pageCount.isNull() || !pageCount.isNumber()) return 0;

        return pageCount.asInt();
    }


    public String toString() {
        return (this.root != null) ? Util.toJsonString(root) : "{}";
    }


}
