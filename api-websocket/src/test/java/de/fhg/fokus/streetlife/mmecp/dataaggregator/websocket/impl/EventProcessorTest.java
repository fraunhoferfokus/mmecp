package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.ConcurrentUtils;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.ResponseEngineFactory;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.RovParkingStationEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.TamParkingStationEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.LiveResponseParseEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.ResponseParseEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.ToPanelEndpoint;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.ConfigReader;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.runners.RetrieveTreLiveDataRunner;
import mockit.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static mockit.Deencapsulation.setField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EventProcessorTest {

	protected static final Logger logger = LoggerFactory.getLogger(EventProcessorTest.class);
	protected static ObjectMapper mapper = new ObjectMapper();
	protected EventProcessor ep;
	protected Event event;
	protected @Mocked ToPanelEndpoint toPanelEndpoint;


	@BeforeMethod
	public void init() {
		ep = new EventProcessor(toPanelEndpoint, "someSessionId");
		event = Event.loadFromFile("basic_event");
		ep.setEvent(event);
	}

	public static final class ProceedingMockUtil extends MockUp<Util> {
		@Mock
		Event createEvent(Invocation inv, String filename) {
			return inv.proceed(filename);
		}
		@Mock
		JsonNode loadJsonLegacyFile(Invocation inv, String filename) {
			return inv.proceed(filename);
		}
	}

	@Test(enabled = true)
	public void shouldSetEvent() {
		assertThat(ep.getEvent(), is(event));
	}

	@Test(enabled = true)
	public void shouldIdentifyParkingAreaContext() {
		assertThat(ep.getEvent().hasParkingAreaContext(), is(true));
	}

	@Test(enabled = true)
	public void shouldIdentifyWorkingDayFilter() {
		assertThat(ep.getEvent().hasWorkingDayFilter(), is(true));
	}

	@Test(enabled = true)
	public void shouldIdentifyMorningTimeBandFilter() {
		assertThat(ep.getEvent().hasMorningTimeBandFilter(), is(true));
	}

	@Test(enabled = true)
	public void shouldIdentifyAfternoonTimeBandFilter() {
		Event event = Event.loadFromFile("basic_event_with_afternoon_filter");
		ep.setEvent(event);
		assertThat(ep.getEvent().hasAfternoonTimeBandFilter(), is(true));
	}

	@Test(enabled = true)
	public void shouldIdentifyNightTimeBandFilter() {
		Event event = Event.loadFromFile("basic_event_with_night_filter");
		ep.setEvent(event);
		assertThat(ep.getEvent().hasNightTimeBandFilter(), is(true));
	}

	@Test(enabled = true)
	public void shouldIdentifyListOfParkingStationReturnType() {
		Event event = Event.loadFromFile("simple_event");
		ep.setEvent(event);
		assertThat(ep.getEvent().fetchReturnType(), is("List<ParkingArea>"));
	}

	@Test(enabled = true)
	public void shouldIdentifyListOfFiltersReturnType() {
		Event event = Event.loadFromFile("list_of_filters_event");
		ep.setEvent(event);
		assertThat(ep.getEvent().fetchReturnType(), is("List<Filter>"));
	}

	@Test(enabled = true)
	public void shouldIdentifyParkingStationReturnTypeWhenWhenOperatorSelectsAnIdMatch() {
		Event event = Event.loadFromFile("simple_event_with_id");
		ep.setEvent(event);
		assertThat(ep.getEvent().fetchReturnType(), is("ParkingArea"));
	}

	@Test(enabled = true)
	public void shouldParseIdOfParkingStation() {
		Event event = Event.loadFromFile("simple_event_with_id");
		ep.setEvent(event);
		assertThat(ep.getId(), is("Microzone_ABC"));
	}

	@Test(enabled = true)
	public void shouldParseWherePart() {
		Event event = Event.loadFromFile("basic_event_with_where");
		ep.setEvent(event);
		assertThat(ep.getEvent().fetchWhereWithoutID(), allOf(hasKey("type"), hasKey("some_property")));

		// assertThat(ep.getWhere().get("type"), isA(java.util.List.class));
		assertThat((java.util.List<String>) (ep.getEvent().fetchWhereWithoutID().get("type")), hasItems("free", "fee"));

		// assertThat(ep.getWhere().get("some_property"), isA(String.class));
		assertThat((String) (ep.getEvent().fetchWhereWithoutID().get("some_property")), is("abc"));

	}

	@Test(enabled = true)
	public void shouldNotAddIdPartOfWhereClause() {
		Event event = Event.loadFromFile("basic_event_with_where");
		ep.setEvent(event);
		assertThat(ep.getEvent().fetchWhereWithoutID(), not(hasEntry("id", "Microzone_ABC")));
	}

	@Test(enabled = true)
	public void shouldDetectStartLiveQuery() {
		Event event = Event.loadFromFile("basic_event_with_start_live_query");
		ep.setEvent(event);
		assertThat(ep.getEvent().startLiveData(), is(true));
	}

	@Test(enabled = true)
	public void stopLiveDataShouldReturnFalseIfStartLiveQueryIsTrue() {
		Event event = Event.loadFromFile("basic_event_with_start_live_query");
		ep.setEvent(event);
		assertThat(ep.getEvent().stopLiveData(), is(false));
	}

	@Test(enabled = true)
	public void shouldDetectStopLiveQuery() {
		Event event = Event.loadFromFile("basic_event_with_stop_live_query");
		ep.setEvent(event);
		assertThat(ep.getEvent().stopLiveData(), is(true));
	}

	@Test(enabled = true)
	public void startLiveDataShouldReturnFalseIfStopLiveQueryIsTrue() {
		Event event = Event.loadFromFile("basic_event_with_stop_live_query");
		ep.setEvent(event);
		assertThat(ep.getEvent().startLiveData(), is(false));
	}

	@Test(enabled = true)
	public void startAndStopLiveDataShouldReturnFalseIfNoLiveFieldIsSend() {
		Event event = Event.loadFromFile("basic_event");
		ep.setEvent(event);
		assertThat(ep.getEvent().startLiveData(), is(false));
		assertThat(ep.getEvent().stopLiveData(), is(false));
	}

	@Test(enabled = true)
	public void listFilterShouldReturnTheListOfFilters() throws IOException {
		String listOfFilter = Util.prettify(ConfigReader.readConfigJson());

		assertThat(listOfFilter, containsString("\"title\" : \"Tampere park and ride\""));
		assertThat(listOfFilter, containsString("\"title\" : \"Berlin events modal split\""));
	}

	@Test(enabled = true)
	public void processShouldReturnTheListFilter() throws IOException {
		Event event = Event.loadFromFile("list_of_filters_event");
		assertThat(event, notNullValue());
		String response = Util.prettify(ep.process(event));

		assertThat(response, containsString("\"title\" : \"Rovereto park and ride\""));
		assertThat(response, containsString("\"title\" : \"Berlin events modal split\""));
	}

	@Test(enabled = true)
	public void processShouldReturnAllParkAndRideAtRoveretoData(
			@Mocked final Util util,
			@Mocked final RovParkingStationEngine parkingStationEngine,
			@Mocked final ResponseEngineFactory responeEngineFactory) throws IOException, IllegalAccessException, InstantiationException {

		Event event = Event.loadFromFile("park_and_ride_at_rovereto");
		assertThat(event, notNullValue());
		ep.process(event);

		// new Verifications(){{
			// Util.loadJsonLegacyFile("parkingStationsClock"); times = 1;
			// Util.loadJsonLegacyFile("parkingStationsFree"); times = 1;
			// Util.loadJsonLegacyFile("parkingStationsFee"); times = 1;
			// Util.loadJsonLegacyFile("parkingStationsMacro"); times = 1;
			// responeEngineFactory.getRovParkingStationEngine(); times = 1;
			// parkingStationEngine.parseResponse(anyString); times = 1;
		new Verifications() {{
			responeEngineFactory.makeResponseParseEngine("ParkAndRide@Rovereto"); times = 1;
			parkingStationEngine.handleEvent((Event) any); times = 1;
		}};
	}

	@Test(enabled = false)
	public void processShouldReturnOnlySelectedParkAndRideDataTypes(
			@Mocked final RovParkingStationEngine parkingStationEngine) throws IOException {
		new Expectations(Util.class) {{
			Util.loadJsonFromFile("parkingStationsFee"); result = mapper.readTree("[{\"a\":\"a\"}]");
			Util.loadJsonFromFile("parkingStationsFree"); result = mapper.readTree("[{\"b\":\"b\"}]");
//			Util.merge((List<ArrayNode>)any); result = "[{\"a\":\"a\"},{\"b\":\"b\"}]";
		}};

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_free_and_fee");
		assertThat(event, notNullValue());
		ep.process(event);

		new Verifications() {{
			Util.loadJsonFromFile("parkingStationsFree"); times = 1;
			Util.loadJsonFromFile("parkingStationsFee"); times = 1;
			parkingStationEngine.parseResponse("[{\"blub\":\"blub\"}]"); times = 1;
		}};
	}

	@Test(enabled = false)
	public void processShouldLoadRovParkingStationsClock(
			@Mocked final Util util /*,
			@Mocked RovParkingStationEngine engine*/ ) throws IOException {

		new Expectations() {{
			Util.loadJsonFromFile(anyString); result = mapper.readTree("[{\"blub\":\"blub\"}]");
		}};

		final RovParkingStationEngine engine = new RovParkingStationEngine();

		new Expectations(RovParkingStationEngine.class) {{
			engine.parseResponse(anyString); result = "{\"blub\":\"blub\"}";
		}};

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_clock");
		assertThat(event, notNullValue());
		ep.process(event);

		new Verifications() {{
			Util.loadJsonFromFile("parkingStationsClock"); times = 1;
			engine.parseResponse("[{\"blub\":\"blub\"}]"); times = 1;
		}};
	}

	@Test(enabled = false)
	public void processShouldLoadRovParkingStationsFee(
			@Mocked final Util util,
			@Mocked RovParkingStationEngine engine) throws IOException {
		new Expectations() {{
			Util.loadJsonFromFile(anyString); result = mapper.readTree("[{\"blub\":\"blub\"}]");
			engine.parseResponse(anyString); result = "{\"blub\":\"blub\"}";
		}};

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_fee");
		assertThat(event, notNullValue());
		ep.process(event);

		new Verifications() {{
			Util.loadJsonFromFile("parkingStationsFee"); times = 1;
		}};
	}

	@Test(enabled = false)
	public void processShouldLoadRovParkingStationsFree(
			@Mocked final Util util,
			@Mocked RovParkingStationEngine engine) throws IOException {
		new Expectations() {{
			Util.loadJsonFromFile(anyString); result = mapper.readTree("[{\"blub\":\"blub\"}]");
			engine.parseResponse(anyString); result = "{\"blub\":\"blub\"}";
		}};

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_free");
		assertThat(event, notNullValue());
		ep.process(event);

		new Verifications() {{
			Util.loadJsonFromFile("parkingStationsFree"); times = 1;
		}};
	}

	@Test(enabled = false)
	public void processShouldLoadRovParkingStationsMacro(
			@Mocked final Util util,
			@Mocked RovParkingStationEngine engine) throws IOException {
		new Expectations() {{
			Util.loadJsonFromFile(anyString); result = mapper.readTree("[{\"blub\":\"blub\"}]");
			engine.parseResponse(anyString); result = "{\"blub\":\"blub\"}";
		}};

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_macro");
		assertThat(event, notNullValue());
		ep.process(event);

		new Verifications() {{
			Util.loadJsonFromFile("parkingStationsMacro"); times = 1;
		}};
	}

	@Test(enabled = true)
	public void mergeShouldMergeTwoJsonArrays() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arr1 = (ArrayNode)mapper.readTree("[1, 2, 3]");
		ArrayNode arr2 = (ArrayNode)mapper.readTree("[4, 5, 6]");

		ArrayNode merged = Util.merge(arr1, arr2);
		assertThat(merged, hasArrayNodeItems(1,2,3,4,5,6));
	}

	@Test(enabled = true)
	public void mergeShouldMergeMultipleJsonArrays() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<ArrayNode> nodes = new ArrayList<ArrayNode>();

		nodes.add((ArrayNode)mapper.readTree("[1]"));
		nodes.add((ArrayNode)mapper.readTree("[2]"));
		nodes.add((ArrayNode)mapper.readTree("[3]"));
		nodes.add((ArrayNode)mapper.readTree("[4]"));

		ArrayNode merged = Util.merge(nodes);
		assertThat(merged, hasArrayNodeItems(1,2,3,4));
	}

	@Test
	public void mergeShouldMergeSingleJsonArrays() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<ArrayNode> nodes = new ArrayList<ArrayNode>();

		nodes.add((ArrayNode) mapper.readTree("[1]"));

		ArrayNode merged = Util.merge(nodes);
		assertThat(merged, hasArrayNodeItems(1));
	}

	@Test(enabled = true)
	public void shouldCreateANewLiveDataThreadForLiveData(
//			@Mocked final ResponseEngineFactory responeEngineFactory,
			@Mocked final ScheduledThreadPoolExecutor executor,
			@Mocked final RetrieveTreLiveDataRunner runner) throws Exception{

		Event event = Event.loadFromFile("basic_live_event");
		assertThat(event, notNullValue());
		ResponseParseEngine engine = ResponseEngineFactory.getTheFactory().makeResponseParseEngine(event.getUseCaseID());
		// System.out.println((engine instanceof ResponseParseEngine)?"yes":"no");
		// System.out.println((engine instanceof LiveResponseParseEngine)?"yes":"no");
		// System.out.println((engine instanceof TamParkingStationEngine)?"yes":"no");
		setField(engine, "executor", executor);
		// ep.process(event);
		engine.handleEvent(event);

		new Verifications() {{
			Executors.newScheduledThreadPool(anyInt); times = 1;
			//runner.setEndpointCallback((ToPanelEndpoint)any);
			executor.scheduleAtFixedRate((RetrieveTreLiveDataRunner)any, 0L, 10L, TimeUnit.SECONDS); times = 1;
		}};
	}

	@Test(enabled = true)
	public void shouldOnlyStartOneThreadForLiveData(
//		@Mocked final ResponseEngineFactory responseEngineFactory,
		@Mocked final ScheduledThreadPoolExecutor executor,
		@Mocked final RetrieveTreLiveDataRunner runner) throws Exception{

		Event event = Event.loadFromFile("basic_live_event");
		assertThat(event, notNullValue());
		ResponseParseEngine engine = ResponseEngineFactory.getTheFactory().makeResponseParseEngine(event.getUseCaseID());
		// System.out.println((engine instanceof ResponseParseEngine)?"yes":"no");
		// System.out.println((engine instanceof LiveResponseParseEngine)?"yes":"no");
		// System.out.println((engine instanceof TamParkingStationEngine)?"yes":"no");
		setField(engine, "executor", executor);
		// ep.process(event);
		engine.handleEvent(event);

		new Verifications() {{
			Executors.newScheduledThreadPool(anyInt); times = 1;
			//runner.setEndpointCallback((ToPanelEndpoint)any);
			executor.scheduleAtFixedRate((RetrieveTreLiveDataRunner) any, 0L, 10L, TimeUnit.SECONDS); times = 1;
		}};
	}

	@Test(enabled = false)
	public void shouldStopRetrieveLiveDataThread(
		@Mocked final ScheduledThreadPoolExecutor executor,
		@Mocked final ConcurrentUtils cu) throws IllegalAccessException, InstantiationException {

		Event event = Event.loadFromFile("basic_live_event");
		assertThat(event, notNullValue());
		ResponseParseEngine engine = ResponseEngineFactory.getTheFactory().makeResponseParseEngine(event.getUseCaseID());
		setField(engine, "executor", executor);
		// ep.process(event);
		engine.handleEvent(event);

		event = Event.loadFromFile("basic_live_stop_event");
		assertThat(event, notNullValue());
		engine.handleEvent(event);

		new Verifications() {{
			ConcurrentUtils.stop(executor); times = 1;
		}};
	}

	@Test(enabled = false)
	public void shouldStopCurrentRunner(
		@Mocked final ScheduledThreadPoolExecutor executor,
		@Mocked final ConcurrentUtils cu) {

		LiveResponseParseEngine engine = new TamParkingStationEngine();
		setField(engine, "executor", executor);
		// engine.stop();

		new Verifications() {{
			ConcurrentUtils.stop(executor); times = 1;
		}};
	}

	@Test(enabled = true)
	public void shouldNotStopCurrentRunnerIfItIsNull(
		@Mocked final ConcurrentUtils cu) {

		// setField(ep, "executor", null);
		ep.stop();

		new Verifications() {{
			ConcurrentUtils.stop((ScheduledThreadPoolExecutor)any); times = 0;
		}};
	
	}


	/**
	 * Hamcrest Matcher for a JsonNode. It returns true, if the JsonNode has an element entry
	 * with the given label and value property.
	 */
	protected Matcher<ArrayNode> hasArrayNodeItems(Integer ... items) {
		return new BaseMatcher<ArrayNode>() {
			@Override
			public boolean matches(final Object obj) {
				for(Integer item:items) {
						
					ArrayNode node = (ArrayNode)obj;
					Iterator<JsonNode> elements = node.elements();
					boolean found = false;
					while(elements.hasNext()) {
						JsonNode element = elements.next();
						int intElement = element.asInt();

						if(intElement == item) {
							found = true;
						}
					}
					if(found == false) {
						return false;
					}
				}
				return true;
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("ArrayNode should have items");
			}
		};
	}


}