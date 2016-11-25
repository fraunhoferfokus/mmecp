package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;

/**
 * Created by csc on 25.09.2015.
 */
public class ChartDataItem {

    private String label;
    private Double value;
    private String color;

    /*
    {
        "label": "Free Parking Slots",
            "value": 500,
            "color": "#5D8896"
    }
    */

    public ChartDataItem(String label, Integer value, Color color) {
        this(label, value, color.toString());
    }


    public ChartDataItem(String label, Integer value, String color) {
        this(label, new Double(value), color);
    }

    public ChartDataItem(String label, Double value, String color) {
        this.label = label;
        this.value = value;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) { this.value = value; }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String toString() { return Util.toJsonString(this);}

}
