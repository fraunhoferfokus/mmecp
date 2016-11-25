package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class GetDataRequest implements Serializable{
	
	protected List<String> functions;

	protected List<String> attributeIdentifiers;

	protected List<String> conditions0;

	protected List<String> conditions1;

	protected List<String> conditions2;

	/*
	 * Add the provided functions to the underlying data representation.
	 * @param String... functions the functions to add to the function list
	 */
	public void setFunctions(List<String> functions){
		getFunctions().addAll(functions);
	}

	/*
	 * Getter method for the functions
 	 * @return List<String> the functions
	 */
	public List<String> getFunctions(){
		if(this.functions == null){
			this.functions = new ArrayList<String>();
		}
		return this.functions;
	}

	public void setAttributeIdentifiers(List<String> attributeIdentifiers){
		getAttributeIdentifiers().addAll(attributeIdentifiers);
	}

	public List<String> getAttributeIdentifiers(){
		if(this.attributeIdentifiers == null){
			this.attributeIdentifiers = new ArrayList<String>();
		}
		return this.attributeIdentifiers;
	}

	public void setConditions0(List<String> conditions0){
		getConditions0().addAll(conditions0);
	}

	public List<String> getConditions0(){
		if(this.conditions0 == null){
			this.conditions0 = new ArrayList<String>();
		}
		return this.conditions0;
	}

	public void setConditions1(List<String> conditions1){
		getConditions1().addAll(conditions1);
	}

	public List<String> getConditions1(){
		if(this.conditions1 == null){
			this.conditions1 = new ArrayList<String>();
		}
		return this.conditions1;
	}

	public void setConditions2(List<String> conditions2){
		getConditions2().addAll(conditions2);
	}

	public List<String> getConditions2(){
		if(this.conditions2 == null){
			this.conditions2 = new ArrayList<String>();
		}
		return this.conditions2;
	}

}