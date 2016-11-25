package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.net.URL;

import java.util.List;
import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by florian on 20.05.15.
 */
public class ScalarAttributes implements Serializable {

	protected List<String> attributeIdentifiers;

	public ScalarAttributes(List<String> attributeIdentifiers){
		this.setAttributeIdentifiers(attributeIdentifiers);
	}

	public ScalarAttributes(){
		this.attributeIdentifiers = new ArrayList<String>();
	}

	public List<String> getAttributeIdentifiers(){
		if(this.attributeIdentifiers == null){
			this.attributeIdentifiers = new ArrayList<String>();
		}
		return this.attributeIdentifiers;
	}

	public void setAttributeIdentifiers(List<String> attributeIdentifiers){
		getAttributeIdentifiers().addAll(attributeIdentifiers);
	}

}
