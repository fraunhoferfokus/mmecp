package de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject;

import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * Created by csc on 05.11.2015.
 */
//    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonTypeName("attribute")
public class Attribute {

    public String label;
    public String value;

    public Attribute(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public Attribute(String label, Long value) {
        this.label = label;
        this.value = Long.toString(value);
    }

    public Attribute(String label, int value) {
        this.label = label;
        this.value = Integer.toString(value);
    }

    public String getLabel() {return label;}
    public String getValue() {return value;}

}
