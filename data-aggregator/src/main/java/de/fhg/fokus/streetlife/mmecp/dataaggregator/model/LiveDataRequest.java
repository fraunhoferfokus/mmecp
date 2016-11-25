package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.net.URL;

import java.util.List;
import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by florian on 20.05.15.
 */
public class LiveDataRequest implements Serializable {

	protected List<String> attributeIdentifiers;
	protected String attributeIdentifier;
	protected int numberOfTexts;
	protected int subscriptionFlag;

	public LiveDataRequest(List<String> attributeIdentifiers, int subscriptionFlag){
		this.setAttributeIdentifiers(attributeIdentifiers);
		this.subscriptionFlag = subscriptionFlag;
	}

	public LiveDataRequest(List<String> attributeIdentifiers, int numberOfTexts, int subscriptionFlag){
		this.setAttributeIdentifiers(attributeIdentifiers);
		this.numberOfTexts = numberOfTexts;
		this.subscriptionFlag = subscriptionFlag;
	}

	public LiveDataRequest(){
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

	public String getAttributeIdentifier(){
		return this.attributeIdentifier;
	}

	public void setAttributeIdentifier(String attributeIdentifier){
		this.attributeIdentifier = attributeIdentifier;
	}

	public int getSubscriptionFlag(){
		return this.subscriptionFlag;
	}

	public void setSubscriptionFlag(int subscriptionFlag){
		this.subscriptionFlag = subscriptionFlag;
	}

	public int getNumberOfTexts(){
		return this.numberOfTexts;
	}

	public void setNumberOfTexts(int numberOfTexts){
		this.numberOfTexts = numberOfTexts;
	}

}