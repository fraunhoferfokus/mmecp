package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csc on 25.09.2015.
 */
public class ChartData {

    private String key;
    private List<ChartDataItem> values = new ArrayList<>();

    public ChartData() {
        this.key = new String();
    }

    public ChartData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ChartDataItem> getValues() {
        return values;
    }

    public void setValues(List<ChartDataItem> values) {
        this.values = values;
    }

    public void addItem( ChartDataItem cdi ) {
        values.add(cdi);
    }

    /*
            {
                "key": "Cumulative Return",
                    "values": [
                {
                    "label": "Free",
                        "value": 1532,
                        "color": "#A5C989"
                },
                {
                    "label": "Not Free",
                        "value": 4321,
                        "color": "#D16D82"
                }
                ]
            }
     */

    public String toString() {return Util.toJsonString(this);}

}
