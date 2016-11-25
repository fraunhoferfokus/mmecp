package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.net.URL;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by florian on 20.05.15.
 */
public class LiveDataResponse implements Serializable {

	protected String response;

	public LiveDataResponse(String response){
		this.response = response;
	}

	public LiveDataResponse(){

	}

	public String getResponse(){
		return this.response;
	}

	public void setResponse(String response){
		this.response = response;
	}

}