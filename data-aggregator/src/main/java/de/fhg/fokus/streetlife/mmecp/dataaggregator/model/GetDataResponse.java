package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.util.List;
import java.io.Serializable;

public interface GetDataResponse extends Serializable{
	public void setInfo(String info);
	public String getInfo();
	public void setFunctions(String... functions);
	public List<String> getFunctions();
	public void setAttributeIdentifiers(String... attributeIdentifiers);
	public List<String> getAttributeIdentifiers();
	public void setConditions0(String... conditions0);
	public List<String> getConditions0();
	public void setConditions1(String... conditions1);
	public List<String> getConditions1();
	public void setConditions2(String... conditions2);
	public List<String> getConditions2();
	public List<List<List<List<Double>>>> getResponse();
	public void setResponse(List<List<List<List<Double>>>> response);
}