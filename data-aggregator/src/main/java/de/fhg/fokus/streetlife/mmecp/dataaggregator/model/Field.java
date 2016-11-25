package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;

/**
 * Created by florian on 05.05.15.
 */
public class Field implements Serializable {

    private String description;

    private String input;

    public Field(){

    }

    public Field(String description, String input){
    	setDescription(description);
    	setInput(input);
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getInput(){
        return this.input;
    }

    public void setInput(String input){
        this.input = input;
    }

}
