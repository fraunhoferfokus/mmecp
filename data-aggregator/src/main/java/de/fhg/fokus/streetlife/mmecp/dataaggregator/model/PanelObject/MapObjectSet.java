package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import de.fhg.fokus.streetlife.mmecp.containers.chart.Legend;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by csc on 13.08.2015 for use case: generate output JSON for display of VMZ traffic counters
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "VmzDtvwDataTypeIdResolver")
//@JsonTypeIdResolver(VmzDtvwDataTypeIdResolver.class)
public class MapObjectSet {

    private String usecaseID;
    private String optionID;
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    private Legend legend;
    private List<MapObject> mapobjects;

    public MapObjectSet() {this(null, null);}

    public MapObjectSet(Legend legend) {
        this(null, legend);
    }

    public MapObjectSet(Event event) {
        this(event, null);
    }

    public MapObjectSet(Event event, Legend legend) {
        this((event == null)?null:event.getUseCaseID(),
             (event == null)?null:event.getOptionID(),
             legend);
    }

    public MapObjectSet(String usecaseID, String optionID, Legend legend) {
        this.usecaseID = usecaseID;
        this.optionID = optionID;
        this.legend = legend;
        this.mapobjects = new LinkedList<>();
    }

    public void add(MapObject data) {
        this.mapobjects.add(data);
    }

    public int size() {
        return this.mapobjects.size();
    }


    public MapObjectSet setSubTypeAll(String subType) {
        for (MapObject data : this.mapobjects) {
            data.setObjectSubtype(subType);
        }
        return this;
    }


    public List<Long> getValueList( String valueAttibute ) {
        if (valueAttibute == null)
            return null;
        List<Long> valueList = new LinkedList<>();
        if (mapobjects.size() == 0 || valueAttibute.isEmpty())
            return valueList;

        for (MapObject data : this.mapobjects) {
            // get the value from the correctly named attribute and add it to the valueList
            for (Object element : data.getElements()) {
                if (element instanceof Attribute) {
                    Attribute attr = (Attribute) element;
                    if (valueAttibute.equals(attr.label)) {
                            valueList.add(Long.parseLong(attr.value));
                        break;
                    }
                }
            }
        }
        // System.out.println("valueList number of elements = " + valueList.size());
        return valueList;
    }


    public void adjustColors( String attributeToScaleOn, Color minColor, Color maxColor) {
        adjustColors(attributeToScaleOn, minColor, maxColor, 0L, 0L);  // 0,0 = signal to find out min/max from data
    }

    public void adjustColors( String attributeToScaleOn, Color minColor, Color maxColor, Long minVal, Long maxVal) {
        if (mapobjects.size() == 0 || attributeToScaleOn == null || attributeToScaleOn.isEmpty())
            return;

        Long minRate = minVal;
        Long maxRate = maxVal;

        if (minRate == 0 && maxRate == 0) { // 0,0 = signal to find out real min/max from data
            minRate = Long.MAX_VALUE;
            maxRate = Long.MIN_VALUE;

            // pass one: find min and max traffic_rate value
            for (MapObject data : this.mapobjects) {
                for( Object element : data.getElements()) {
                    if ( element instanceof Attribute) {
                        Attribute attr = (Attribute)element;
                        if (attributeToScaleOn.equals(attr.label)) {
                            Long rate = Long.parseLong(attr.value);
                            if (rate < minRate)
                                minRate = rate;
                            if (rate > maxRate)
                                maxRate = rate;
                            break;
                        }
                    }
                }
            }
        }
        // System.out.println("XXXX: min/max rate =" + minRate + " / " + maxRate);
        // pass two: re-set color depending on min, max, and traffic rate of each element
        for (MapObject data : this.mapobjects) {
            Long rate = 0L;
            // get the rate value first
            for( Object element : data.getElements()) {
                if ( element instanceof Attribute) {
                    Attribute attr = (Attribute)element;
                    if (attributeToScaleOn.equals(attr.label)) {
                        rate = Long.parseLong(attr.value);
                        break;
                    }
                }
            }
            // find the maparea field and fix the color value in it
            for( Object element : data.getElements()) {
                if ( element instanceof MapArea) {
                    MapArea ma = (MapArea)element;
                    if (minRate == maxRate) {  // avoid division by zero! mix colors 50:50 in this case!
                        ma.color.setMix(minColor, maxColor, 0.5f);
                    } else {
                        ma.color.setMix(minColor, maxColor, ((float) (rate - minRate)) / ((float) (maxRate - minRate)));
                    }
                }
            }
        }
    }


    public void adjustColors(String attributeToScaleOn, int colorScheme) {

        if (mapobjects.size() == 0 || attributeToScaleOn == null || attributeToScaleOn.isEmpty())
            return;

        // re-set color depending on accident counter in each element
        for (MapObject data : this.mapobjects) {
            int numAccidents = 0;
            // get the "accidents" value first
            for( Object element : data.getElements()) {
                if ( element instanceof Attribute) {
                    Attribute attr = (Attribute)element;
                    if (attributeToScaleOn.equals(attr.label)) {
                        numAccidents = Integer.parseInt(attr.value);
                        break;
                    }
                }
            }
            // find the "maparea" element and re-set the color value in it
            for( Object element : data.getElements()) {
                if ( element instanceof MapArea) {
                    MapArea ma = (MapArea)element;
                    ma.color.colorize(numAccidents, colorScheme);
                }
            }
        }
    }

    public Legend getLegend() { return legend; }
    public void setLegend(Legend legend) { this.legend = legend; }
    public MapObjectSet withLegend(Legend legend) { setLegend(legend); return this; }

    public List<MapObject> getMapobjects() { return mapobjects; }

    public void setMapobjects(List<MapObject> mapobjects) { this.mapobjects = mapobjects; }

    public String toString() {return this.toJsonString();}
    public String toJsonString() {return Util.toJsonString(this);}

    public String getUsecaseID() {return usecaseID;}
    public void setUsecaseID(String usecaseID) {this.usecaseID = usecaseID;}

    public String getOptionID() {return optionID;}
    public void setOptionID(String optionID) {this.optionID = optionID;}


    public void setIDsFrom(Event event) {
        setUsecaseID(event.getUseCaseID());
        setOptionID(event.getOptionID());
        setSubTypeAll(event.getSubType());
    }


    public void sort() {
        Collections.sort(this.mapobjects, MapObject.comparator);
    }


    public static String wrapMapObjectsJsonString(String mapObjectsJson, Event event) {
        // wrap Json in this Json: {"usecaseID":null, "optionID":null, "mapobjects":%responseData%}
        StringBuffer buf = new StringBuffer();
        buf.append("{\"usecaseID\":\"");
        buf.append(event.getUseCaseID());
        buf.append("\", \"optionID\":\"");
        buf.append(event.getOptionID());
        buf.append("\", \"mapobjects\":");
        buf.append(mapObjectsJson);
        buf.append("}");

        return buf.toString();

    }


}
