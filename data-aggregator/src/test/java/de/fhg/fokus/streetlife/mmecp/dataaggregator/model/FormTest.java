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

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormTest{
	
	protected Form form;

	@BeforeMethod
	public void init(){
		this.form = new Form();
	}


	@Test
	public void getAndSetItemTypeShouldWorkProperly(){
		form.setItemtype("id");

		assertThat(form.getItemtype(), is("id"));

		form.setItemtype("label");

		assertThat(form.getItemtype(), is("label"));

		form.setItemtype("enum");

		assertThat(form.getItemtype(), is("enum"));		

		form.setItemtype("field");

		assertThat(form.getItemtype(), is("field"));
	}

	@Test
	public void getAndSetValueShouldWorkProperly(){
		form.setValue("some value");

		assertThat(form.getValue(), is("some value"));
	}

	@Test
	public void getAndSetLabelShouldWorkProperly(){
		form.setLabel("some label");

		assertThat(form.getLabel(), is("some label"));
	}

	@Test
	public void getAndSetFieldShouldWorkProperly(){
		Field field = new Field("some description", "some input");

		form.setField(field);

		assertThat(form.getField(), is(field));
	}

	@Test
	public void getAndSetEnumShouldWorkProperly(){
		Enum enumyType = new Enum();

		form.setEnum(enumyType);

		assertThat(form.getEnum(), is(enumyType));
	}

	@Test
	public void serializeToJsonShouldUseEnumyKeywordInsteadOfJavaClassName() throws IOException{
		String[] enums = {"enum1", "enum2"};
		Enum enumyType = new Enum("some description", Arrays.asList(enums), 1);

		form.setItemtype("enum");
		form.setEnum(enumyType);

		final ObjectMapper mapper = new ObjectMapper();

		String tmpForm = mapper.writeValueAsString(form);

		assertThat(tmpForm, containsString("\"enum\":{"));
	}
}