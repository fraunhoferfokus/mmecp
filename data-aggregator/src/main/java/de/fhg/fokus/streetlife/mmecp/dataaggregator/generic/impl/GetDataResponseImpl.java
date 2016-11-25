package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.GetDataResponse;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.json.GetDataResponseBean;

import java.util.Arrays;
import java.util.List;

import java.io.Serializable;

/*
 * created by flo (florian.thiemer@fokus.fraunhofer.de) - 22.4.2015
 */
public class GetDataResponseImpl implements GetDataResponse, Serializable{
	
	protected GetDataResponseBean getDataResponseBean;

	public GetDataResponseImpl(GetDataResponseBean getDataResponseBean){
		this.getDataResponseBean = getDataResponseBean;
	}

	public void setInfo(String info){
		getDataResponseBean.getRequest().setInfo(info);
	}

	public String getInfo(){
		return getDataResponseBean.getRequest().getInfo();
	}

	/*
	 * Add the provided functions to the underlying data representation.
	 * @param String... functions the functions to add to the function list
	 */
	public void setFunctions(String... functions){
		getDataResponseBean.getRequest().getContext().getFunctions().addAll(Arrays.asList(functions));
	}

	/*
	 * Getter method for the functions
 	 * @return List<String> the functions
	 */
	public List<String> getFunctions(){
		return getDataResponseBean.getRequest().getContext().getFunctions();
	}

	public void setAttributeIdentifiers(String... attributeIdentifiers){
		getDataResponseBean.getRequest().getContext().getAttributeIdentifiers().addAll(Arrays.asList(attributeIdentifiers));
	}

	public List<String> getAttributeIdentifiers(){
		return getDataResponseBean.getRequest().getContext().getAttributeIdentifiers();
	}

	public void setConditions0(String... conditions0){
		getDataResponseBean.getRequest().getContext().getConditions0().addAll(Arrays.asList(conditions0));
	}

	public List<String> getConditions0(){
		return getDataResponseBean.getRequest().getContext().getConditions0();
	}

	public void setConditions1(String... conditions1){
		getDataResponseBean.getRequest().getContext().getConditions1().addAll(Arrays.asList(conditions1));
	}

	public List<String> getConditions1(){
		return getDataResponseBean.getRequest().getContext().getConditions1();
	}

	public void setConditions2(String... conditions2){
		getDataResponseBean.getRequest().getContext().getConditions2().addAll(Arrays.asList(conditions2));
	}

	public List<String> getConditions2(){
		return getDataResponseBean.getRequest().getContext().getConditions2();
	}

	public void setResponse(List<List<List<List<Double>>>> response){
		getDataResponseBean.getResponse().addAll(response);
	}

	public List<List<List<List<Double>>>> getResponse(){
		return getDataResponseBean.getResponse();
	}

}