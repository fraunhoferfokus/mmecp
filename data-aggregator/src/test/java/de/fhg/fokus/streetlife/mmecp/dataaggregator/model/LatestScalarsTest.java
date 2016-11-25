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

public class LatestScalarsTest{
	
	protected LatestScalars latestScalars;

	@BeforeMethod
	public void init(){
		this.latestScalars = new LatestScalars();
	}

	@Test
	public void argumentConstructorShouldWorkProperly(){
		Double[] scalars = {0.0, 1.0, 100.0};
		List<List<Integer>> locations = new ArrayList<List<Integer>>();

		locations.add(Arrays.asList(1,2,3));
		locations.add(Arrays.asList(4,5,6));
		locations.add(Arrays.asList(7,8,9));

		String[] times = {"2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"};

		this.latestScalars = new LatestScalars(Arrays.asList(scalars), locations, Arrays.asList(times));

		assertThat(this.latestScalars.getScalars(), hasItems(0.0, 1.0, 100.0));
		assertThat(this.latestScalars.getLocations(), hasSize(3));
		assertThat(this.latestScalars.getLocations().get(0), hasItems(1,2,3));
		assertThat(this.latestScalars.getLocations().get(1), hasItems(4,5,6));
		assertThat(this.latestScalars.getLocations().get(2), hasItems(7,8,9));

		assertThat(latestScalars.getTimes(), hasItems("2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"));
	}

	@Test
	public void noArgumentConstructorShouldWorkProperly(){
		assertThat(latestScalars.getScalars(), empty());
		assertThat(latestScalars.getLocations(), empty());
		assertThat(latestScalars.getTimes(), empty());
	}

	@Test
	public void getAndSetScalarsShouldWorkProperly(){
		Double[] scalars = {0.0, 1.0, 100.0};
		latestScalars.setScalars(Arrays.asList(scalars));

		assertThat(latestScalars.getScalars(), hasItems(0.0, 1.0, 100.0));
	}

	@Test
	public void getAndSetLocationsShouldWorkProperly(){
		List<List<Integer>> locations = new ArrayList<List<Integer>>();
		locations.add(Arrays.asList(1,2,3));
		locations.add(Arrays.asList(4,5,6));

		latestScalars.setLocations(locations);

		assertThat(latestScalars.getLocations(), hasSize(2));
		assertThat(latestScalars.getLocations().get(0), hasItems(1,2,3));
		assertThat(latestScalars.getLocations().get(1), hasItems(4,5,6));
	}

	@Test
	public void getAndSetTimesShouldWorkProperly(){
		String[] times = {"2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"};
		latestScalars.setTimes(Arrays.asList(times));

		assertThat(latestScalars.getTimes(), hasItems("2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"));
	}

}