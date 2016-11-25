package de.fhg.fokus.streetlife.mmecp.containers.chart;

/**
 * Created by csc on 23.09.2015.
 */
public class ChartRequest {

    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public ChartRequest() {
        this.type = new String();
    }

    public ChartRequest(String type) {
        this.type = type;
    }

}
