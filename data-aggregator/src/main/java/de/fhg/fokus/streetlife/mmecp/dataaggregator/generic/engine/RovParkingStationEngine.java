package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * Created by flo on 05/29/15.
 */
public class RovParkingStationEngine extends BaseLiveEngine {

    // private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private static final ObjectMapper Mapper = new ObjectMapper();

    private static final String USECASENAME = "ParkAndRide@Rovereto";
    public String getUseCaseID() {return USECASENAME;}

    private JsonNode exampleData = null;

    public String parseResponse(String response) {
        readExampleData();
        JsonNode json = null;
        try {
            json = Mapper.readTree(response);
        } catch (IOException e) {
            // e.printStackTrace();
            return "";
        }

        Integer macroMaxMeasurementCount = null;
        Iterator<JsonNode> iter = json.getElements();
        while (iter.hasNext()) {
            JsonNode curObject = iter.next();
            if (curObject.path("type").getTextValue().equals("mapobject")) {
                if (curObject.path("objectType").getTextValue().equals("ParkingStation") &&
                        !(curObject.path("objectSubtype").getTextValue().equals("macro"))) {
                    double avg = getAverageOccupancy(curObject.path("objectID").getTextValue(), Calendar.HOUR_OF_DAY, 10, 12);
                    addMapObjectAttribute(curObject, "Average Occupancy (10-12)", String.format("%.2f", avg * 100) + "%");
                    setMapObjectColor(curObject, avg, 130, 0);
                    ArrayList<Double> week = getAverageOccupancyWeek(curObject.path("objectID").getTextValue());
                    ArrayList<String> days = new ArrayList<>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));
                    addMapObjectChart(curObject, "barchart", "Weekdays", "Average Occupancy", days, week);
                } else if (curObject.path("objectType").getTextValue().equals("ParkingStation") &&
                        curObject.path("objectSubtype").getTextValue().equals("macro")) {
                    if (macroMaxMeasurementCount == null)
                        macroMaxMeasurementCount = new Integer(getMeasurementDistribution(json));
                    List<String> tmp = curObject.path("elements").findValuesAsText("value");
                    double count = Double.valueOf(tmp.get(tmp.size() - 1));
                    setMapObjectColor(curObject, count / macroMaxMeasurementCount, 0, 130);
                }
            }
        }
        return json.toString();
    }


    public String handleEvent(Event event) {
        if (event == null) {return null;}

        // handle Rovereto park and ride scenario's options with life thread
        if (event.getOptionID() != null && event.getOptionID().startsWith("parkandridesim:")) {
            if (event.startLiveData() ) {
                this.start(event, new RovParkingSimRunner(this, event), /*schedule every*/60/*seconds*/);
            } else if (event.stopLiveData()) {
                this.stop(event);
            }
            return "";
        }

        // else, handle other (non-live) options

        Map<String, Object> where = event.fetchWhereWithoutID();
        List<String> type = (List<String>)where.get("type");
        List<com.fasterxml.jackson.databind.node.ArrayNode> jsonFiles = new ArrayList<>();

        if(where == null || type.contains("clock")) {
            jsonFiles.add((com.fasterxml.jackson.databind.node.ArrayNode)Util.loadJsonFromFile("parkingStationsClock"));
        }
        if(where == null || type.contains("fee")) {
            jsonFiles.add((com.fasterxml.jackson.databind.node.ArrayNode)Util.loadJsonFromFile("parkingStationsFee"));
        }
        if(where == null || type.contains("free")) {
            jsonFiles.add((com.fasterxml.jackson.databind.node.ArrayNode)Util.loadJsonFromFile("parkingStationsFree"));
        }
        if(where == null || type.contains("macro")) {
            jsonFiles.add((com.fasterxml.jackson.databind.node.ArrayNode)Util.loadJsonFromFile("parkingStationsMacro"));
        }
        String responseData = this.parseResponse(Util.merge(jsonFiles).toString());
        return MapObjectSet.wrapMapObjectsJsonString(responseData, event);
    }


    private double getAverageOccupancy(String key, int field, int start, int end) {
        ArrayList<Double> occupancies = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        JsonNode dataObjects = getObjectsInData("name", key, exampleData);
        Iterator<JsonNode> ite = dataObjects.getElements();
        while (ite.hasNext()) {
            JsonNode curObject = ite.next();
            Date curDate = null;
            try {
                curDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(curObject.path("time").getTextValue());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal.setTime(curDate);
            if (start <= cal.get(field) && end >= cal.get(field)) {
                int occupied = curObject.path("value").path("slotsOccupiedOnFree").getIntValue() +
                        curObject.path("value").path("slotsOccupiedOnPaying").getIntValue() +
                        curObject.path("value").path("slotsOccupiedOnTimed").getIntValue();
                int total = curObject.path("value").path("slotsFree").getIntValue() +
                        curObject.path("value").path("slotsPaying").getIntValue() +
                        curObject.path("value").path("slotsTimed").getIntValue();
                occupancies.add((double) occupied / total);
            }
        }
        if (occupancies.size() > 0)
            return occupancies.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        return 0.0;
    }

    private double getAverageOccupancy(String key, int day) {
        return getAverageOccupancy(key, Calendar.DAY_OF_WEEK, day, day);
    }

    private ArrayList<Double> getAverageOccupancyWeek(String key) {
        ArrayList<Double> occupancyWeek = new ArrayList<>(7);
        for (int i = 1; i <= 7; i++)
            occupancyWeek.add(i - 1, getAverageOccupancy(key, i));
        return occupancyWeek;
    }

    private void addMapObjectAttribute(JsonNode object, String label, String value) {
        ObjectNode newNode = Mapper.createObjectNode();
        newNode.put("label", label);
        newNode.put("value", value);
        ((ArrayNode) object.path("elements")).addObject().put("attribute", newNode);
    }

    private int getMeasurementDistribution(JsonNode json) {
        LOG.info("getMeasurementDistribution");
        Iterator<JsonNode> ite = json.getElements();
        int maxCount = 0;
        while (ite.hasNext()) {
            JsonNode curObject = ite.next();
            if (curObject.path("type").getTextValue().equals("mapobject")) {
                if (curObject.path("objectType").getTextValue().equals("ParkingStation") &&
                        (curObject.path("objectSubtype").getTextValue().equals("macro"))) {
                    int count = getObjectsInData("areaId", curObject.path("objectID").getTextValue(), exampleData).size();
                    addMapObjectAttribute(curObject, "Measurement Count", String.valueOf(count));
                    maxCount = (count > maxCount) ? count : maxCount;
                }
            }
        }
        LOG.info("max measurement: " + maxCount);
        return maxCount;
    }

    private void setMapObjectColor(JsonNode object, double value, double minColor, double maxColor) {
        Iterator<JsonNode> elements = object.path("elements").getElements();
        double n = minColor;
        double m = maxColor - n;
        while (elements.hasNext()) {
            JsonNode curElement = elements.next();
            if (curElement.get("maparea") != null) {
                //TODO Exception when Color not exist
                Color color = new Color(Color.HSBtoRGB((float) (m * value + n) / 360f, 1f, 0.7f));
                ((ObjectNode) curElement.path("maparea").path("color")).put("red", color.getRed());
                ((ObjectNode) curElement.path("maparea").path("color")).put("green", color.getGreen());
                ((ObjectNode) curElement.path("maparea").path("color")).put("blue", color.getBlue());
                ((ObjectNode) curElement.path("maparea").path("color")).put("alpha", 0.8);
            }
        }
    }

    private void addMapObjectChart(JsonNode object, String chartType, String xDescription, String yDescription, ArrayList<String> x, ArrayList<Double> y) {
        ArrayNode data = Mapper.createArrayNode();
        for (int i = 0; i < y.size(); i++) {
            ObjectNode item = Mapper.createObjectNode();
            item.put("label", x.get(i));
            item.put("value", y.get(i));
            data.add(item);
        }
        ObjectNode newNode = Mapper.createObjectNode();
        newNode.put("type", chartType);
        newNode.put("labeldescription", xDescription);
        newNode.put("valuedescription", yDescription);
        newNode.put("data", data);
        ((ArrayNode) object.path("elements")).addObject().put("chart", newNode);

    }

    public void readExampleData() {
        try {
            exampleData = Mapper.readTree(this.getClass().getResourceAsStream("/streetlogrovereto.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonNode getObjectsInData(String keyName, String keyValue, JsonNode data) {
        Iterator<JsonNode> ite = data.getElements();
        ArrayNode objects = Mapper.createArrayNode();
        while (ite.hasNext()) {
            JsonNode curObject = ite.next();
            if (curObject.path("value").path(keyName).getTextValue().equals(keyValue))
                objects.add(curObject);
        }
        return objects;
    }


}
