package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorFactory;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.*;
import org.codehaus.jackson.JsonNode;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.AssertJUnit.assertNotNull;

public class DataAggregatorClientImplTest{

	protected static final Logger logger = LoggerFactory.getLogger(DataAggregatorClientImplTest.class);

	protected DataAggregatorClient dac;

	@BeforeMethod
	public void initDataAggregatorClient(){	
		dac = DataAggregatorFactory.newInstance("POJO").getClient();

		// since Injection is not working in test cases :(
		((DataAggregatorClientImpl)dac).genericDataApiUrl = "http://private-f559f-streetlifehistoricdataapi.apiary-mock.com";
		((DataAggregatorClientImpl)dac).liveDataApiUrl = "http://private-91266-streetlifelivedataapi.apiary-mock.com";
		((DataAggregatorClientImpl)dac).storageBaseUrl = "http://private-f5ae7-streetlifemmecp.apiary-mock.com";
		((DataAggregatorClientImpl)dac).cipClientUrl = "https://www.cityintelligenceplatform.siemens.com/app-sl-pr/rest/streetlife/parkride";
		((DataAggregatorClientImpl)dac).cipUserName = "mobility";
		((DataAggregatorClientImpl)dac).cipPassword = "<--ENTER CIP PASSWORD HERE-->"";

		dac.init();
	}
	
	@Test
	public void shouldHaveBaseURL(){
		assertNotNull("the storage base url should be not null",((DataAggregatorClientImpl)dac).storageBaseUrl);
	}

	@Test
	public void shouldHavaGenericDataApiUrl(){
		assertNotNull("the generic data api url should not be null", ((DataAggregatorClientImpl)dac).genericDataApiUrl);
	}

	@Test
	public void shouldHavaLiveDataApiUrl(){
		assertNotNull("the live data api url should not be null", ((DataAggregatorClientImpl)dac).liveDataApiUrl);
	}

	@Test
	public void shouldHaveCipClientUrl(){
		assertNotNull("the live data api url should not be null", ((DataAggregatorClientImpl)dac).cipClientUrl);
	}

	@Test
	public void shouldInitDataAggregatorClient(){
		assertNotNull("the data aggregation client should not be null after initialization.",dac);
	}

	@Test
	public void shouldCreateProxyForGenericDataApi(){
		assertNotNull("the generic data api proxy should be initialized.",((DataAggregatorClientImpl)dac).genericDataApi);
	}

	private Matcher<JsonNode> hasItemsInJsonNodeArray(final String... items){
		return new BaseMatcher<JsonNode> (){
			@Override
			public boolean matches(final Object jsonArray){
				JsonNode jsonNode = (JsonNode)jsonArray;
				for(String item:items){
					boolean foundItem = false;
					Iterator<JsonNode> iter = jsonNode.getElements();
					while (iter.hasNext()) {
						JsonNode temp = iter.next();
						if(temp.getTextValue().equals(item)){
							foundItem = true;
						}
					}
					if(!foundItem){
						return false;
					}
				}
				return true;
			}

			@Override
		    public void describeTo(final Description description) {
		       description.appendText("should contain: ").appendValue(items);
		    }
		};
	}


	@Test(enabled = false)
	public void getKeysShouldHaveCorrectFunctions(){
		Keys keys = dac.getKeys();

		assertThat(keys.getFunctions().size(), is(8));
		assertThat(keys.getFunctions(), hasItems("mean", "SD", "n", "median", "Q1", "Q3", "min", "max"));
	}

	@Test(enabled = false)
	public void getKeysShouldHaveCorrectAttributeIdentifiers(){
		Keys keys = dac.getKeys();

		assertThat(keys.getAttributeIdentifiers().size(), is(2));
		assertThat(keys.getAttributeIdentifiers(), hasItems("parkinglotId(8155).occupancy", "bikestationId(515).availability"));
	}

	@Test(enabled = false)
	public void getKeysShouldHaveCorrectConditionKeywords(){
		Keys keys = dac.getKeys();

		assertThat(keys.getConditionKeywords().size(), is(7));
		assertThat(keys.getConditionKeywords(), hasItems("timeOfDay", "dayOfWeek", "dayOfMonth", "monthOfYear", "year", "lastNdays", "all"));
	}

	@Test(enabled = false)
	public void shouldCallGetData(){
		GetDataRequest request = new GetDataRequest();

		String[] functions = {"mean", "SD"};
		String[] attributeIdentifiers = {"bikestationId(515).availability"};
		String[] conditions0 = {"year(2001,2010)", "monthOfYear(1)"};
		String[] conditions1 = {"dayOfWeek(Mon)", "dayOfWeek(Tue)", "dayOfWeek(Wed)"};
		String[] conditions2 = {"timeOfDay(10:00,12:00)", "timeOfDay(12:00,14:00)", "timeOfDay(14:00,16:00)", "timeOfDay(16:00,18:00)"};

		request.setFunctions(Arrays.asList(functions));
		request.setAttributeIdentifiers(Arrays.asList(attributeIdentifiers));
		request.setConditions0(Arrays.asList(conditions0));
		request.setConditions1(Arrays.asList(conditions1));
		request.setConditions2(Arrays.asList(conditions2));

		GetDataResponse response = dac.getData(request);

		assertThat(response.getInfo(), is("Your request was"));
		assertThat(response.getFunctions(), hasItems("mean", "SD"));
		assertThat(response.getAttributeIdentifiers(), hasItems("bikestationId(515).availability"));
		assertThat(response.getConditions0(), hasItems("year(2001,2010)", "monthOfYear(1)"));
		assertThat(response.getConditions1(), hasItems("dayOfWeek(Mon)", "dayOfWeek(Tue)", "dayOfWeek(Wed)"));
		assertThat(response.getConditions2(), hasItems("timeOfDay(10:00,12:00)", "timeOfDay(12:00,14:00)", "timeOfDay(14:00,16:00)", "timeOfDay(16:00,18:00)"));
		
		assertThat(response.getResponse().size(), is(2));
	}

	@Test(enabled = false)
	public void getDataShouldHaveCorrectResponseValues(){
		GetDataRequest request = new GetDataRequest();

		String[] functions = {"mean", "SD"};
		String[] attributeIdentifiers = {"bikestationId(515).availability"};
		String[] conditions0 = {"year(2001,2010)", "monthOfYear(1)"};
		String[] conditions1 = {"dayOfWeek(Mon)", "dayOfWeek(Tue)", "dayOfWeek(Wed)"};
		String[] conditions2 = {"timeOfDay(10:00,12:00)", "timeOfDay(12:00,14:00)", "timeOfDay(14:00,16:00)", "timeOfDay(16:00,18:00)"};

		request.setFunctions(Arrays.asList(functions));
		request.setAttributeIdentifiers(Arrays.asList(attributeIdentifiers));
		request.setConditions0(Arrays.asList(conditions0));
		request.setConditions1(Arrays.asList(conditions1));
		request.setConditions2(Arrays.asList(conditions2));

		GetDataResponse response = dac.getData(request);

		assertThat(response.getResponse().size(), is(2));
		
		assertThat(response.getResponse().get(0).size(), is(1));
		assertThat(response.getResponse().get(1).size(), is(1));

		assertThat(response.getResponse().get(0).get(0).size(), is(3));
		assertThat(response.getResponse().get(1).get(0).size(), is(3));
		
		assertThat(response.getResponse().get(0).get(0).get(0), hasItems(1.0,	2.0,	3.0,	4.0));
		assertThat(response.getResponse().get(0).get(0).get(1), hasItems(5.0,	6.0,	7.0,	8.0));
		assertThat(response.getResponse().get(0).get(0).get(2), hasItems(9.0, 	10.0, 	11.0, 	12.0));
		
		assertThat(response.getResponse().get(1).get(0).get(0), hasItems(13.0,	14.0,	15.0,	16.0));
		assertThat(response.getResponse().get(1).get(0).get(1), hasItems(17.0,	18.0,	19.0,	20.0));
		assertThat(response.getResponse().get(1).get(0).get(2), hasItems(21.0,	22.0,	23.0,	24.0));

	}


	@Test(enabled = false)
	public void getScalarAttributesShouldHaveCorrectResponseValues(){
		ScalarAttributes scalarAttributes = dac.getScalarAttributes();

		assertThat(scalarAttributes.getAttributeIdentifiers(), hasItems("parkinglotId(8155).occupancy", "bikestationId(515).availability"));
	}

	@Test(enabled = false)
	public void getNotificationAttributesShouldHaveCorrectResponseValues(){
		NotificationAttributes notificationAttributes = dac.getNotificationAttributes();

		assertThat(notificationAttributes.getAttributeIdentifiers(), hasItems("public_transport.disruptions", "streettraffic.jams"));
	}

	@Test(enabled = false)
	public void getLatestScalarsShouldHaveCorrectResponseValues(){
		String[] attributeIdentifiers = {"parkinglotId(8155).occupancy", "bikestationId(515).availability"};
		LiveDataRequest liveDataRequest = new LiveDataRequest(Arrays.asList(attributeIdentifiers), 0);

		LatestScalars latestScalars = dac.getLatestScalars(liveDataRequest);

		assertThat(latestScalars.getScalars(), hasItems(50.0, 12.0));
		assertThat(latestScalars.getLocations(), hasSize(2));
		assertThat(latestScalars.getLocations().get(0).toArray(), arrayContaining(57, 10, 67));
		assertThat(latestScalars.getLocations().get(1).toArray(), arrayContaining(43,12,67));
		assertThat(latestScalars.getTimes(), hasItems("2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"));
	}


	@Test(enabled = false)
	public void getLatestNotificationShouldHaveCorrectResponseValues(){
		LiveDataRequest liveDataRequest = new LiveDataRequest();
		liveDataRequest.setAttributeIdentifier("streettraffic.jams");
		liveDataRequest.setNumberOfTexts(2);
		liveDataRequest.setSubscriptionFlag(0);

		LatestNotifications latestNotifications = dac.getLatestNotifications(liveDataRequest);

		assertThat(latestNotifications.getTexts(), hasItems("2km jam in Baker Street.", "Jam on highway 69."));
		assertThat(latestNotifications.getLocations(), hasSize(2));
		assertThat(latestNotifications.getLocations().get(0).toArray(), arrayContaining(57, 10, 67));
		assertThat(latestNotifications.getLocations().get(1).toArray(), arrayContaining(43,12,67));
		assertThat(latestNotifications.getTimes(), hasItems("2015-04-01T08:41:51+00:00", "2015-04-01T08:41:51+00:00"));
	}

	@Test(enabled = false)
	public void unsubscribeShouldHaveCorrectResponseValues(){
		String [] attributeIdentifiers = { "parkinglotId(8155).occupancy",
                  "bikestationId(515).availability",
                  "public_transport.disruptions",
                  "streettraffic.jams"};
		LiveDataRequest liveDataRequest = new LiveDataRequest();
		liveDataRequest.setAttributeIdentifiers(Arrays.asList(attributeIdentifiers));

		LiveDataResponse liveDataResponse = dac.unsubscribe(liveDataRequest);

		assertThat(liveDataResponse.getResponse(), is("OK"));
	}

	@Test(enabled = false)
	public void subscribeShouldHaveCorrectResponseValues(){
		String [] attributeIdentifiers = { "parkinglotId(8155).occupancy",
                  "bikestationId(515).availability",
                  "public_transport.disruptions",
                  "streettraffic.jams"};
		LiveDataRequest liveDataRequest = new LiveDataRequest();
		liveDataRequest.setAttributeIdentifiers(Arrays.asList(attributeIdentifiers));

		LiveDataResponse liveDataResponse = dac.subscribe(liveDataRequest);

		assertThat(liveDataResponse.getResponse(), is("OK"));
	}

	@Test(groups = {"cipintegration"}, enabled = false)
	public void getScalarAttributesCIPShouldWorkProperly(){
		List<String> scalarAttributes = dac.getScalarAttributesCIP();

		assertThat(scalarAttributes, hasItems("time", "trend", "status", "code", "name1", "y", "x"));
	}

	@Test(groups = {"ciphotfix"})
	public void splitArrayStringShouldReturnAnArrayWithEachElement(){
		String sampleString = "\"[a, b, c, d]\"";

		List<String> splittedString = (new DataAggregatorClientImpl()).splitArrayString(sampleString);

		assertThat(splittedString, hasItems("a", "b", "c", "d"));
	}

	@Test(groups = {"ciphotfix"})
	public void splitArrayStringShouldIgnoreLeadingOrTrailingSpaces(){
		String sampleString = "    \"[a,b,c,d]\"       ";

		List<String> splittedString = (new DataAggregatorClientImpl()).splitArrayString(sampleString);

		assertThat(splittedString, hasItems("a", "b", "c", "d"));
	}

	@Test(groups = {"ciphotfix"})
	public void splitArrayStringShouldOnlySplitIfTheStringsStartAndEndsWithProperArraySign(){

		assertThat((new DataAggregatorClientImpl()).splitArrayString("[a,b,c,d]\""), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("[a,b,c,d]"), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("[a,b,c,d\""), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("[a,b,c,d"), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("\"a,b,c,d]\""), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("\"a,b,c,d]"), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("\"a,b,c,d\""), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("\"a,b,c,d"), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("a,b,c,d]\""), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("a,b,c,d]"), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("a,b,c,d\""), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString("a,b,c,d"), nullValue());
		assertThat((new DataAggregatorClientImpl()).splitArrayString(""), nullValue());
	}

	@Test(groups = {"ciphotfix"})
	public void createMultiParamQueryStringShouldTransformAnArray(){
		String[] param = {"a", "b", "c", "d"};

		assertThat((new DataAggregatorClientImpl()).createMultiParamQueryString(Arrays.asList(param)), is("a,b,c,d"));
	}

	@Test(groups = {"ciphotfix"})
	public void createMultiParamQueryStringShouldTransformAnArrayWithOneElement(){
		String[] param = {"a"};

		assertThat((new DataAggregatorClientImpl()).createMultiParamQueryString(Arrays.asList(param)), is("a"));
	}

	@Test(groups = {"ciphotfix"})
	public void createMultiParamQueryStringShouldTransformAnArrayWithNoElement(){
		String[] param = {};

		assertThat((new DataAggregatorClientImpl()).createMultiParamQueryString(Arrays.asList(param)), is(""));
	}

	@Test(enabled = false, groups = {"cipintegration"})
	public void getScalarValuesCalledWithName1AttributesShouldHaveName1Property(){
		String[] attributes = {"name1"};

		List<Map<String, String>> scalarValues = dac.getScalarValuesCIP(Arrays.asList(attributes));

		for(Map<String, String> scalarValuesEntry: scalarValues){
			assertThat(scalarValuesEntry, hasKey("name1"));
		}

		// assertThat(scalarValues, everyItem(hasKey("name1")));
	}

	@Test(enabled = false, groups = {"cipintegration"})
	public void getScalarValuesCalledWithMultipleAttributesShouldHaveAllPropertiesSet(){
		String[] attributes = {"trend","time","status","code","y","x","name1"};

		List<Map<String, String>> scalarValues = dac.getScalarValuesCIP(Arrays.asList(attributes));

		for(Map<String, String> scalarValuesEntry: scalarValues){
			assertThat(scalarValuesEntry, hasKey("name1"));
			//assertThat(scalarValuesEntry, hasKey("trend"));
			assertThat(scalarValuesEntry, hasKey("time"));
			assertThat(scalarValuesEntry, hasKey("status"));
			assertThat(scalarValuesEntry, hasKey("code"));
			assertThat(scalarValuesEntry, hasKey("y"));
			assertThat(scalarValuesEntry, hasKey("x"));
		}

		// assertThat(scalarValues, everyItem(hasKey("name1")));
	}


}