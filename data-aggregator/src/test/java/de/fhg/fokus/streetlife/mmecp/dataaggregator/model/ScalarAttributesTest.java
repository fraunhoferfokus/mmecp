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

public class ScalarAttributesTest{
	
	protected ScalarAttributes scalarAttributes;

	@BeforeMethod
	public void init(){
		this.scalarAttributes = new ScalarAttributes();
	}

	@Test
	public void argumentConstructorShouldWorkProperly(){
		String[] attributeIdentifiers = {"attribute(0123).a", "attribute(0123).b"};
		this.scalarAttributes = new ScalarAttributes(Arrays.asList(attributeIdentifiers));

		assertThat(scalarAttributes.getAttributeIdentifiers(), hasItems("attribute(0123).a", "attribute(0123).b"));
	}

	@Test
	public void noArgumentConstructorShouldWorkProperly(){
		assertThat(scalarAttributes.getAttributeIdentifiers(), empty());
	}

	@Test
	public void getAndSetAttributeIdentifierShouldWorkProperly(){
		String[] attributeIdentifiers = {"attribute(0123).a", "attribute(0123).b"};
		scalarAttributes.setAttributeIdentifiers(Arrays.asList(attributeIdentifiers));

		assertThat(scalarAttributes.getAttributeIdentifiers(), hasItems("attribute(0123).a", "attribute(0123).b"));
	}

}