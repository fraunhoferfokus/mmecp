package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.net.URL;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by florian on 20.05.15.
 */
public class LatestNotifications implements Serializable {

	protected List<String> texts;

	protected List<List<Integer>> locations;

	protected List<String> times;

	public LatestNotifications(List<String> texts, List<List<Integer>> locations, List<String> times){
		setTexts(texts);
		setLocations(locations);
		setTimes(times);
	}

	public LatestNotifications(){
		this.texts = new ArrayList<String>();
		this.locations = new ArrayList<List<Integer>>();
		this.times = new ArrayList<String>();
	}

	public List<String> getTexts(){
		if(this.texts == null){
			this.texts = new ArrayList<String>();
		}
		return this.texts;
	}

	public void setTexts(List<String> texts){
		getTexts().addAll(texts);
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