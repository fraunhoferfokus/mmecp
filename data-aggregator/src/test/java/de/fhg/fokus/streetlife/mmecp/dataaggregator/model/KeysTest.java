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


public class KeysTest{


  Keys keys;

  @BeforeMethod
  public void init(){
    keys = new Keys();
  }

  @Test
  public void getAndSetFunctionsShouldWorkProperly(){
    keys.setFunctions("mean", "sd");

    assertThat(keys.getFunctions(), hasItems("mean", "sd"));
  }

  @Test
  public void getFunctionsShouldNeverReturnNull(){
  	assertThat(keys.getFunctions(), notNullValue());
  }

  @Test
  public void getAndSetAttributeIdentifiersShouldWorkProperly(){
    keys.setAttributeIdentifiers("parkinglotId(8155).occupancy", "bikestationId(515).availability");

    assertThat(keys.getAttributeIdentifiers(), hasItems("parkinglotId(8155).occupancy", "bikestationId(515).availability"));
  }

  @Test
  public void getAttributeIdentifiersShouldNeverReturnNull(){
  	assertThat(keys.getAttributeIdentifiers(), notNullValue());
  }

  @Test
  public void getAndSetConditionKeywordsShouldWorkProperly(){
    keys.setConditionKeywords("year(2001,2010)", "monthOfYear(1)");

    assertThat(keys.getConditionKeywords(), hasItems("year(2001,2010)", "monthOfYear(1)"));
  }

  @Test
  public void getConditionKeywordsShouldNeverReturnNull(){
  	assertThat(keys.getConditionKeywords(), notNullValue());
  }

}