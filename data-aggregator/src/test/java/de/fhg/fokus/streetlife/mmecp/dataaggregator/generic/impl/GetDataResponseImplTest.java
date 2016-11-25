package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.json.GetDataResponseBean;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;

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


public class GetDataResponseImplTest{

  GetDataResponseImpl getDataResponse;

  @BeforeMethod
  public void init(){
    getDataResponse = new GetDataResponseImpl(new GetDataResponseBean());
  }

  @Test
  public void getDataResponseBeanShouldNotBeNull(){
    assertThat(getDataResponse.getDataResponseBean, notNullValue());
  }

  @Test
  public void shouldSetInfo(){
    getDataResponse.setInfo("Hello world!");

    assertThat(getDataResponse.getDataResponseBean.getRequest().getInfo(), is("Hello world!"));
  }

  @Test
  public void shouldGetInfo(){
    getDataResponse.setInfo("Hello world!");

    assertThat(getDataResponse.getInfo(), is("Hello world!"));
  }

  @Test
  public void shouldSetFunctions(){
    getDataResponse.setFunctions("mean", "sd");

    assertThat(getDataResponse.getDataResponseBean.getRequest().getContext().getFunctions(), hasItems("mean", "sd"));
  }

  @Test
  public void shouldGetFunctions(){
    getDataResponse.setFunctions("mean", "sd");

    assertThat(getDataResponse.getFunctions(), hasItems("mean", "sd"));
  }

  @Test
  public void shouldSetAttributeIdentifiers(){
    getDataResponse.setAttributeIdentifiers("parkinglotId(8155).occupancy", "bikestationId(515).availability");

    assertThat(getDataResponse.getDataResponseBean.getRequest().getContext().getAttributeIdentifiers(), hasItems("parkinglotId(8155).occupancy", "bikestationId(515).availability"));
  }

  @Test
  public void shouldGetAttributeIdentifiers(){
    getDataResponse.setAttributeIdentifiers("parkinglotId(8155).occupancy", "bikestationId(515).availability");

    assertThat(getDataResponse.getAttributeIdentifiers(), hasItems("parkinglotId(8155).occupancy", "bikestationId(515).availability"));
  }

  @Test
  public void shouldSetConditions0(){
    getDataResponse.setConditions0("year(2001,2010)", "monthOfYear(1)");

    assertThat(getDataResponse.getDataResponseBean.getRequest().getContext().getConditions0(), hasItems("year(2001,2010)", "monthOfYear(1)"));
  }

  @Test
  public void shouldGetConditions0(){
    getDataResponse.setConditions0("year(2001,2010)", "monthOfYear(1)");

    assertThat(getDataResponse.getConditions0(), hasItems("year(2001,2010)", "monthOfYear(1)"));
  }

  @Test
  public void shouldSetConditions1(){
    getDataResponse.setConditions1("dayOfWeek(Mon)", "dayOfWeek(Tue)", "dayOfWeek(Wed)", "dayOfWeek(Thu)", "dayOfWeek(Fri)");

    assertThat(getDataResponse.getDataResponseBean.getRequest().getContext().getConditions1(), hasItems("dayOfWeek(Mon)", "dayOfWeek(Tue)", "dayOfWeek(Wed)", "dayOfWeek(Thu)", "dayOfWeek(Fri)"));
  }

  @Test
  public void shouldGetConditions1(){
    getDataResponse.setConditions1("dayOfWeek(Mon)", "dayOfWeek(Tue)", "dayOfWeek(Wed)", "dayOfWeek(Thu)", "dayOfWeek(Fri)");

    assertThat(getDataResponse.getConditions1(), hasItems("dayOfWeek(Mon)", "dayOfWeek(Tue)", "dayOfWeek(Wed)", "dayOfWeek(Thu)", "dayOfWeek(Fri)"));
  }

  @Test
  public void shouldSetConditions2(){
    getDataResponse.setConditions2("timeOfDay(10:00,12:00)", "timeOfDay(12:00,14:00)", "timeOfDay(14:00,16:00)", "timeOfDay(16:00,18:00)");

    assertThat(getDataResponse.getDataResponseBean.getRequest().getContext().getConditions2(), hasItems("timeOfDay(10:00,12:00)", "timeOfDay(12:00,14:00)", "timeOfDay(14:00,16:00)", "timeOfDay(16:00,18:00)"));
  }

  @Test
  public void shouldGetConditions2(){
    getDataResponse.setConditions2("timeOfDay(10:00,12:00)", "timeOfDay(12:00,14:00)", "timeOfDay(14:00,16:00)", "timeOfDay(16:00,18:00)");

    assertThat(getDataResponse.getConditions2(), hasItems("timeOfDay(10:00,12:00)", "timeOfDay(12:00,14:00)", "timeOfDay(14:00,16:00)", "timeOfDay(16:00,18:00)"));
  }

  @Test
  public void shouldSetResponse(){

    Double[] response1 = {1.2, 1.3, 1.4};
    
    List<List<Double>> response2 = new ArrayList<List<Double>>();
    response2.add(Arrays.asList(response1));
    
    List<List<List<Double>>> response3 = new ArrayList<List<List<Double>>>();
    response3.add(response2);

    List<List<List<List<Double>>>> response4 = new ArrayList<List<List<List<Double>>>>();
    response4.add(response3);

    getDataResponse.setResponse(response4);

    assertThat(getDataResponse.getDataResponseBean.getResponse().get(0).get(0).get(0), hasItems(1.2,1.3,1.4));
  }

  @Test
  public void shouldGetResponse(){

    Double[] response1 = {1.2, 1.3, 1.4};
    
    List<List<Double>> response2 = new ArrayList<List<Double>>();
    response2.add(Arrays.asList(response1));
    
    List<List<List<Double>>> response3 = new ArrayList<List<List<Double>>>();
    response3.add(response2);

    List<List<List<List<Double>>>> response4 = new ArrayList<List<List<List<Double>>>>();
    response4.add(response3);

    getDataResponse.setResponse(response4);

    assertThat(getDataResponse.getResponse().get(0).get(0).get(0), hasItems(1.2,1.3,1.4));
  }

}
