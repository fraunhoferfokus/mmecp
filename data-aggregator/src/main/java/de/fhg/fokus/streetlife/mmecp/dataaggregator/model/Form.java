package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.net.URL;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by florian on 01.05.15.
 */
public class Form implements Serializable {

    private String itemtype;

    private String value;

    private String label;

    private Field field;

    private Enum myEnum;

    public String getItemtype(){
        return this.itemtype;
    }

    public void setItemtype(String itemtype){
        this.itemtype = itemtype;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }

    public void setField(Field field){
        this.field = field;
    }

    public Field getField(){
        return this.field;
    }

    public void setEnum(Enum myEnum){
        this.myEnum = myEnum;
    }

    public Enum getEnum(){
        return this.myEnum;
    }
}
