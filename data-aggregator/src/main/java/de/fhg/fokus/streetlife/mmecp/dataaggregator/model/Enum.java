package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by florian on 05.05.15.
 */
public class Enum implements Serializable {

    private String description;

    private List<String> myEnum;

    private int selected;

    public Enum(){

    }

    public Enum(String description, List<String> myEnum, int selected){
        setDescription(description);
        setEnum(myEnum);
        setSelected(selected);
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public List<String> getEnum(){
        if(this.myEnum == null){
            this.myEnum = new ArrayList<String>();
        }

        return this.myEnum;
    }

    public void setEnum(List<String> myEnum){
        this.getEnum().addAll(myEnum);
    }

    public void setSelected(int selected){
        this.selected = selected;
    }

    public int getSelected(){
        return this.selected;
    }

}
