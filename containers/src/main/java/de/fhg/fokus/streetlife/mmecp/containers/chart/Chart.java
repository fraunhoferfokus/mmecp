package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csc on 25.09.2015.
 */
public class Chart {

    private String useCaseID;
    private String optionID;
    private String chartID;
    private String title;
    private ChartOptions options;
    private List<ChartData> data;
    private String description;

    public Chart(Event event, String title, ChartOptions options) {
        this.useCaseID = event.getUseCaseID();
        this.optionID = event.getOptionID();
        this.chartID = title.trim().replace(" ","");
        this.title = title;
        this.options = options;
        this.data = new ArrayList<ChartData>();
        this.description = "";
    }

    public Chart(Event event, String title, ChartOptions options, ChartData data) {
        this(event, title, options);
        this.data.add(data);
    }


    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    public ChartOptions getOptions() {
        return options;
    }
    public void setOptions(ChartOptions options) {
        this.options = options;
    }

    public List<ChartData> getData() {
        return data;
    }
    public void setData(List<ChartData> data) {
        this.data = data;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOptionID() {return optionID;}
    public void setOptionID(String optionID) {this.optionID = optionID;}

    public String getUseCaseID() {return useCaseID;}
    public void setUseCaseID(String useCaseID) {this.useCaseID = useCaseID;}

    public String getChartID() {return chartID;}
    public void setChartID(String chartID) {this.chartID = chartID;}


    public String toString() {return this.toJsonString();}

    public String toJsonString() {return Util.toJsonString(this);}


}
