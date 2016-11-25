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

public class LiveDataResponseTest{
	
	protected LiveDataResponse liveDataResponse;

	@BeforeMethod
	public void init(){
		this.liveDataResponse = new LiveDataResponse();
	}

	@Test
	public void argumentConstructorShouldWorkProperly(){
		this.liveDataResponse = new LiveDataResponse("OK");

		assertThat(liveDataResponse.getResponse(), is("OK"));
	}

	@Test
	public void getAndSetResponseShouldWorkProperly(){
		liveDataResponse.setResponse("OK");

		assertThat(liveDataResponse.getResponse(), is("OK"));
	}

}