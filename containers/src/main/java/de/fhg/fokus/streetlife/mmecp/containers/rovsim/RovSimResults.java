package de.fhg.fokus.streetlife.mmecp.containers.rovsim;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by csc on 08.01.2016.
 */
public class RovSimResults {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private Double kilometersSaved;
    private Double CO2Saved;
    private List<ParkingLot> parkingLots;

    public RovSimResults(Double kilometersSaved, Double CO2Saved, List<ParkingLot> parkingLots) {
        this.kilometersSaved = kilometersSaved;
        this.CO2Saved = CO2Saved;
        this.parkingLots = parkingLots;
    }


    public RovSimResults(String json) throws IOException {

        JsonNode root = MAPPER.readTree(json);

        this.kilometersSaved = root.get("kilometersSaved").getDoubleValue();
        this.CO2Saved = root.get("co2Saved").getDoubleValue();
        this.parkingLots = ParkingLot.parseJsonList(root.get("parkingLots"));
    }


    public String toJsonString() {
        return Util.toJsonString(this);
    }

    public Double getKilometersSaved() {return kilometersSaved;}
    public Double getCO2Saved() {return CO2Saved;}
    public List<ParkingLot> getParkingLots() {return parkingLots;}


}
