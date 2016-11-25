package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.json;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Request;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;

/*
 * created by flo (florian.thiemer@fokus.fraunhofer.de) - 22.4.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GetDataResponseBean implements Serializable{

	@JsonProperty
	protected Request request;

	@JsonProperty
	protected List<List<List<List<Double>>>> response;

	public void setRequest(Request request){
		this.request = request;
	}

	public Request getRequest(){
		if(this.request == null){
			this.request = new Request();
		}
		return this.request;
	}

	public void setResponse(List<List<List<List<Double>>>> response){
		this.response = response;
	}

	public List<List<List<List<Double>>>> getResponse(){
		if(this.response == null){
			this.response = new ArrayList<List<List<List<Double>>>>();
			
		}
		return this.response;
	}

}