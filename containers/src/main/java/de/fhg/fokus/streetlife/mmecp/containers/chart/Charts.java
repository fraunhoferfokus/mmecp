package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by csc on 25.09.2015.
 */
public class Charts {

    private final String type = "charts";

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Chart.class, name = "chart"),
    })
    private List<Chart> elements = new LinkedList();

    public Charts() {
        super();
    }

    public Charts(Chart chart) {
        this.addChart(chart);
    }

    public Charts(Chart chart1, Chart chart2) {
        this(chart1);
        this.addChart(chart2);
    }

    public Charts(Chart chart1, Chart chart2, Chart chart3) {
        this(chart1, chart2);
        this.addChart(chart3);
    }


    public void addChart( Chart chart ) {
        elements.add(chart);
    }


    public String getType() {
        return type;
    }
//    public void setType(String type) {this.type = type;}

    public List<Chart> getElements() {
        return elements;
    }
    public void setElements(List<Chart> elements) {
        this.elements = elements;
    }

    public String toString() {return this.toJsonString();}
    public String toJsonString() {return Util.toJsonString(this);}

}
