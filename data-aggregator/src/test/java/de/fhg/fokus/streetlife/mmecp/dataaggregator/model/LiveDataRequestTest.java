package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Description;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveDataRequestTest{
	
	protected LiveDataRequest liveDataRequest;

	@BeforeMethod
	public void init(){
		this.liveDataRequest = new LiveDataRequest();
	}

	@Test
	public void argumentConstructorWithAttributeIdentifiersAndSubscriptionFlagShouldWorkProperly(){
		String[] attributeIdentifiers = {"attribute(0123).a", "attribute(0123).b"};
		this.liveDataRequest = new LiveDataRequest(Arrays.asList(attributeIdentifiers), 0);

		assertThat(liveDataRequest.getAttributeIdentifiers(), hasItems("attribute(0123).a", "attribute(0123).b"));
		assertThat(liveDataRequest.getSubscriptionFlag(), is(0));
	}

	@Test
	public void argumentConstructorWithAttributeIdentifiersAndNumberOfTextsAndSubscriptionFlagShouldWorkProperly(){
		String[] attributeIdentifiers = {"attribute(0123).a", "attribute(0123).b"};
		this.liveDataRequest = new LiveDataRequest(Arrays.asList(attributeIdentifiers), 2, 0);

		assertThat(liveDataRequest.getAttributeIdentifiers(), hasItems("attribute(0123).a", "attribute(0123).b"));
		assertThat(liveDataRequest.getNumberOfTexts(), is(2));
		assertThat(liveDataRequest.getSubscriptionFlag(), is(0));
	}


	@Test
	public void noArgumentConstructorShouldWorkProperly(){
		assertThat(liveDataRequest.getAttributeIdentifiers(), empty());
	}

	@Test
	public void getAndSetAttributeIdentifiersShouldWorkProperly(){
		String[] attributeIdentifiers = {"attribute(0123).a", "attribute(0123).b"};
		liveDataRequest.setAttributeIdentifiers(Arrays.asList(attributeIdentifiers));

		assertThat(liveDataRequest.getAttributeIdentifiers(), hasItems("attribute(0123).a", "attribute(0123).b"));
	}

	@Test
	public void getAndSetAttributeIdentifierShouldWorkProperly(){
		String attribute = "some attribute";
		liveDataRequest.setAttributeIdentifier(attribute);

		assertThat(liveDataRequest.getAttributeIdentifier(), is("some attribute"));
	}

	@Test
	public void getAndSetSubscriptionFlagShouldWorkProperly(){
		liveDataRequest.setSubscriptionFlag(1);

		assertThat(liveDataRequest.getSubscriptionFlag(), is(1));
	}

	@Test
	public void getAndSetNumberOfTextsShouldWorkProperly(){
		liveDataRequest.setNumberOfTexts(2);

		assertThat(liveDataRequest.getNumberOfTexts(), is(2));
	}

}