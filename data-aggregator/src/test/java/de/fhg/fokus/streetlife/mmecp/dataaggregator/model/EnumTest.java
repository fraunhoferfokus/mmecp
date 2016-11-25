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

public class EnumTest{
	
	protected Enum enumType;

	@BeforeMethod
	public void init(){
		this.enumType = new Enum();
	}

	@Test
	public void argumentConstructorShouldWorkProperly(){
		String[] enums = {"enum1", "enum2"};
		this.enumType = new Enum("some description", Arrays.asList(enums), 1);

		assertThat(enumType.getDescription(), is("some description"));
		assertThat(enumType.getEnum(), hasItems(enums));
		assertThat(enumType.getSelected(), is(1));
	}

	@Test
	public void noArgumentConstructorShouldWorkProperly(){
		assertThat(enumType.getDescription(), nullValue());
	}

	@Test
	public void getAndSetDescriptionShouldWorkProperly(){
		enumType.setDescription("some description");

		assertThat(enumType.getDescription(), is("some description"));
	}

	@Test
	public void getAndSetEnumShouldWorkProperly(){
		String[] tmpEnum = {"enum1", "enum2"}; 
		enumType.setEnum(Arrays.asList(tmpEnum));

		assertThat(enumType.getEnum(), hasItems("enum1", "enum2"));
	}

	@Test
	public void getEnumShouldNeverReturnNull(){
		assertThat(enumType.getEnum(), notNullValue());
	}

	@Test
	public void getAndSetSelectedShouldWorkProperly(){
		enumType.setSelected(1);

		assertThat(enumType.getSelected(), is(1));
	}
}