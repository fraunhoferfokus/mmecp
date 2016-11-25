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

public class FieldTest{
	
	protected Field field;

	@BeforeMethod
	public void init(){
		this.field = new Field();
	}

	@Test
	public void argumentConstructorShouldWorkProperly(){
		this.field = new Field("some description", "some input");

		assertThat(field.getDescription(), is("some description"));
		assertThat(field.getInput(), is("some input"));
	}

	@Test
	public void noArgumentConstructorShouldWorkProperly(){
		assertThat(field.getDescription(), nullValue());
		assertThat(field.getInput(), nullValue());
	}


	@Test
	public void getAndSetDescriptionShouldWorkProperly(){
		field.setDescription("some description");

		assertThat(field.getDescription(), is("some description"));
	}

	@Test
	public void getAndSetInputShouldWorkProperly(){
		field.setInput("some input");

		assertThat(field.getInput(), is("some input"));
	}
}