package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.net.URL;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by florian on 20.05.15.
 */
public class LatestScalars implements Serializable {

	protected List<Double> scalars;

	protected List<List<Integer>> locations;

	protected List<String> times;

	public LatestScalars(List<Double> scalars, List<List<Integer>> locations, List<String> times){
		setScalars(scalars);
		setLocations(locations);
		setTimes(times);
	}

	public LatestScalars(){
		this.scalars = new ArrayList<Double>();
		this.locations = new ArrayList<List<Integer>>();
		this.times = new ArrayList<String>();
	}

	public List<Double> getScalars(){
		if(this.scalars == null){
			this.scalars = new ArrayList<Double>();
		}
		return this.scalars;
	}

	public void setScalars(List<Double> scalars){
		getScalars().addAll(scalars);
	}

	public List<List<Integer>> getLocations(){
		if(this.locations == null){
			this.locations = new ArrayList<List<Integer>>();
		}
		return this.locations;
	}

	public void setLocations(List<List<Integer>> locations){
		getLocations().addAll(locations);
	}

	public List<String> getTimes(){
		if(this.times == null){
			this.times = new ArrayList<String>();
		}
		return this.times;
	}

	public void setTimes(List<String> times){
		getTimes().addAll(times);
	}

}