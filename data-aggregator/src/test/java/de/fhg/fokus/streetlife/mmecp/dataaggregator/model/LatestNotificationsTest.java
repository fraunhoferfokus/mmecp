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

public class LatestNotificationsTest{
	
	protected LatestNotifications latestNotifications;

	@BeforeMethod
	public void init(){
		this.latestNotifications = new LatestNotifications();
	}

	@Test
	public void argumentConstructorShouldWorkProperly(){
		String[] texts = {"2km jam in Baker Street.", "Jam on highway 69."};
		List<List<Integer>> locations = new ArrayList<List<Integer>>();

		locations.add(Arrays.asList(1,2,3));
		locations.add(Arrays.asList(4,5,6));
		locations.add(Arrays.asList(7,8,9));

		String[] times = {"2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"};

		this.latestNotifications = new LatestNotifications(Arrays.asList(texts), locations, Arrays.asList(times));

		assertThat(this.latestNotifications.getTexts(), hasItems("2km jam in Baker Street.", "Jam on highway 69."));
		assertThat(this.latestNotifications.getLocations(), hasSize(3));
		assertThat(this.latestNotifications.getLocations().get(0), hasItems(1,2,3));
		assertThat(this.latestNotifications.getLocations().get(1), hasItems(4,5,6));
		assertThat(this.latestNotifications.getLocations().get(2), hasItems(7,8,9));

		assertThat(latestNotifications.getTimes(), hasItems("2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"));
	}

	@Test
	public void noArgumentConstructorShouldWorkProperly(){
		assertThat(latestNotifications.getTexts(), empty());		
		assertThat(latestNotifications.getLocations(), empty());
		assertThat(latestNotifications.getTimes(), empty());
	}

	@Test
	public void getAndSetScalarsShouldWorkProperly(){
		String[] texts = {"2km jam in Baker Street.", "Jam on highway 69."};
		latestNotifications.setTexts(Arrays.asList(texts));

		assertThat(latestNotifications.getTexts(), hasItems("2km jam in Baker Street.", "Jam on highway 69."));
	}

	@Test
	public void getAndSetLocationsShouldWorkProperly(){
		List<List<Integer>> locations = new ArrayList<List<Integer>>();
		locations.add(Arrays.asList(1,2,3));
		locations.add(Arrays.asList(4,5,6));

		latestNotifications.setLocations(locations);

		assertThat(latestNotifications.getLocations(), hasSize(2));
		assertThat(latestNotifications.getLocations().get(0), hasItems(1,2,3));
		assertThat(latestNotifications.getLocations().get(1), hasItems(4,5,6));
	}

	@Test
	public void getAndSetTimesShouldWorkProperly(){
		String[] times = {"2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"};
		latestNotifications.setTimes(Arrays.asList(times));

		assertThat(latestNotifications.getTimes(), hasItems("2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"));
	}

}