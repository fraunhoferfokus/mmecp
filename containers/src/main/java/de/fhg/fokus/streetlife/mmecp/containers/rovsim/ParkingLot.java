package de.fhg.fokus.streetlife.mmecp.containers.rovsim;

import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by csc on 08.01.2016.
 */
public class ParkingLot {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingLot.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String name;
    private String address;
    private GpsCoordinate location;
    private Integer capacity;
    private Integer occupation = 0;
    private List<String> rideServices = new LinkedList<>();
    private Integer interceptedTrafficFlow = 0;
    private Double percentageOfMaxDemand = 0.9;

    public ParkingLot() {
    }

    public ParkingLot(String name, String address, GpsCoordinate location, Integer capacity) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.capacity = capacity;
    }

    public String toJsonString() {
        return Util.toJsonString(this);
    }

    public static String toJsonString(List<ParkingLot> parking) {
        return Util.toJsonString(parking);
    }


    public static List<ParkingLot> parseJsonList(String body) {
        try {
            JsonNode root = MAPPER.readTree(body);
            LOG.info("##################### 1");
            return ParkingLot.parseJsonList(root);
        } catch (IOException e) {
            // e.printStackTrace();
            LOG.error(";;;;;;;;;   " + e.getMessage());
            return null;
        }
    }


    public static List<ParkingLot> parseJsonList(JsonNode root) {
        if (!root.isArray()) return null;
        try {
            List<ParkingLot> lots = new LinkedList<>();
            LOG.info("##################### 2");
            Iterator<JsonNode> iter = ((ArrayNode)root).iterator();
            while (iter.hasNext()) {
                JsonNode node = iter.next();
                LOG.info(":::: " + node.toString());
                lots.add(MAPPER.readValue(node, ParkingLot.class));
            }
            LOG.info("##################### 3");
            return lots;
        } catch (IOException e) {
            // e.printStackTrace();
            LOG.error(";;;;;;;;;   " + e.getMessage());
            return null;
        }
    }


    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public GpsCoordinate getLocation() {return location;}
    public void setLocation(GpsCoordinate location) {this.location = location;}

    public Integer getCapacity() {return capacity;}
    public void setCapacity(Integer capacity) {this.capacity = capacity;}

    public Integer getOccupation() {return occupation;}
    public void setOccupation(Integer occupation) {this.occupation = occupation;}

    public List<String> getRideServices() {return rideServices;}
    public void setRideServices(List<String> rideServices) {this.rideServices = rideServices;}

    public Integer getInterceptedTrafficFlow() {return interceptedTrafficFlow;}
    public void setInterceptedTrafficFlow(Integer interceptedTrafficFlow) {this.interceptedTrafficFlow = interceptedTrafficFlow;}

    public Double getPercentageOfMaxDemand() {return percentageOfMaxDemand;}
    public void setPercentageOfMaxDemand(Double percentageOfMaxDemand) {this.percentageOfMaxDemand = percentageOfMaxDemand;}

}
