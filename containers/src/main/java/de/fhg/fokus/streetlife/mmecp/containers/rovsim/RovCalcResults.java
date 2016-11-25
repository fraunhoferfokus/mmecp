package de.fhg.fokus.streetlife.mmecp.containers.rovsim;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by csc on 08.01.2016.
 */
public class RovCalcResults {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private Double percentageDemand;
    private Integer maximumDemand;
    private Integer newParkingUsers;

    public RovCalcResults(Double percentageDemand, Integer maximumDemand, Integer newParkingUsers) {
        // JsonNode root = MAPPER.readTree(json);
        this.percentageDemand = percentageDemand;
        this.maximumDemand = maximumDemand;
        this.newParkingUsers = newParkingUsers;
    }

    public String toJsonString() {
        return Util.toJsonString(this);
    }

    public Double getPercentageDemand() {return percentageDemand;}
    public Integer getMaximumDemand() {return maximumDemand;}
    public Integer getNewParkingUsers() {return newParkingUsers;}


}
