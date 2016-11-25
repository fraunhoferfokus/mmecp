package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;

public class Context implements Serializable{

	protected List<String> functions;
	protected List<String> attributeIdentifiers;
	protected List<String> conditions0;
	protected List<String> conditions1;
	protected List<String> conditions2;

	public List<String> getFunctions(){
		if(this.functions == null){
			this.functions = new ArrayList<String>();
		}
		return this.functions;
	}

	public List<String> getAttributeIdentifiers(){
		if(attributeIdentifiers == null){
			this.attributeIdentifiers = new ArrayList<String>();
		}
		return this.attributeIdentifiers;
	}

	public List<String> getConditions0(){
		if(this.conditions0 == null){
			this.conditions0 = new ArrayList<String>();
		}
		return this.conditions0;
	}

	public List<String> getConditions1(){
		if(this.conditions1 == null){
			this.conditions1 = new ArrayList<String>();
		}
		return this.conditions1;
	}

	public List<String> getConditions2(){
		if(this.conditions2 == null){
			this.conditions2 = new ArrayList<String>();
		}
		return this.conditions2;
	}
}