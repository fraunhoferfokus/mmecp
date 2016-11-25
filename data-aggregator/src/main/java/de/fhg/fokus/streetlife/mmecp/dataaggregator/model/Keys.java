package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;

public class Keys implements Serializable{

	protected List<String> functions;
	protected List<String> attributeIdentifiers;
	protected List<String> conditionKeywords;

	public Keys(){
		this.functions = new ArrayList<String>();
		this.attributeIdentifiers = new ArrayList<String>();
		this.conditionKeywords = new ArrayList<String>();
	}

	public void setFunctions(String... functions){
		this.functions.addAll(Arrays.asList(functions));
	}

	public List<String> getFunctions(){
		if(this.functions == null){
			this.functions = new ArrayList<String>();
		}
		return this.functions;
	}

	public void setAttributeIdentifiers(String... attributeIdentifiers){
		this.attributeIdentifiers.addAll(Arrays.asList(attributeIdentifiers));
	}

	public List<String> getAttributeIdentifiers(){
		if(this.attributeIdentifiers == null){
			this.attributeIdentifiers = new ArrayList<String>();
		}
		return this.attributeIdentifiers;
	}

	public void setConditionKeywords(String... conditionKeywords){
		this.conditionKeywords.addAll(Arrays.asList(conditionKeywords));
	}

	public List<String> getConditionKeywords(){
		if(this.conditionKeywords == null){
			this.conditionKeywords = new ArrayList<String>();
		}
		return this.conditionKeywords;
	}
}