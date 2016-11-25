package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.dto.EventWrapper;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.BerCO2Engine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.RovDemoEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.impl.EventProcessor;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.MessagingUtils;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManager;
import mockit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mockit.Deencapsulation.setField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by bdi on 03/11/14.
 * Modified by flo on 11/05/15
 */
public class ToPanelEndpointTest {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected ToPanelEndpoint toPanelEndpoint;

	protected ObjectMapper mapper;

	public static final class MockSession extends MockUp<Session> {
		@Mock
		public void $clinit() {/* do nothing */ }

		@Mock
		public String getId() {
			return "someId";
		}
	}

	public static final class MockMessagingUtils extends MockUp<MessagingUtils> {
		@Mock
		public void $clinit() {/* do nothing */}

		@Mock
		public void broadcastMessage(String endpoint, String message) {

		}

		@Mock
		public void sendMessage(String endpoint, String sessionId, String message){

		}
	}

	public static final class MockSessionManager extends MockUp<SessionManager> {
		@Mock
		public void $clinit() { /* */}

		@Mock
		public List<Session> getEndpointSessions(String endpoint) {
			Session s = new MockUp<Session>() {
				@Mock
				public RemoteEndpoint.Basic getBasicRemote() {
					return new MockUp<RemoteEndpoint.Basic>() {
						public void sendText(String text) {
							/* do nothing */
						}
					}.getMockInstance();
				}
			}.getMockInstance();

			List<Session> tmpList = new ArrayList<Session>();
			tmpList.add(s);
			return tmpList;
		}
	}

	@BeforeMethod
	public void init() throws Exception {

		new MockSession();
		new MockMessagingUtils();
		new MockSessionManager();

		mapper = new ObjectMapper();
		toPanelEndpoint = new ToPanelEndpoint();

		// mock MessagingUtils
		toPanelEndpoint.mu = new MessagingUtils();
		toPanelEndpoint.sm = new SessionManager();

		// set endpooint name to a dummy value
		Field field = ToPanelEndpoint.class.getDeclaredField("endpointName");
		field.setAccessible(true);
		field.set(toPanelEndpoint, "the_endpoint_name");
	}


	@Test(enabled = true)
	public void shouldRespondParkingStationsClock() throws Exception {

		new MockUp<MessagingUtils>() {
			@Mock
			public void $clinit() {/* do nothing */}

			@Mock(invocations = 1)
			public void sendMessage(String endpoint, String sessionId, String message) {
				assertThat(message, containsString("\"objectID\":\"Benacense _ 03\""));
			}
		};

		Session session = new MockSession().getMockInstance();

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_clock");
		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));

		String message = mapper.writeValueAsString(eventWrapper);

		toPanelEndpoint.rovdemoEngine = new RovDemoEngine();
		((RovDemoEngine) toPanelEndpoint.rovdemoEngine).readExampleData();

		toPanelEndpoint.onMessage(message, session);
	}


	@Test(enabled = true)
	public void shouldRespondParkingStationsFee() throws Exception {

		final Logger logger = LoggerFactory.getLogger("ShouldRespondParkingStationsFeeLogger");

		new MockUp<MessagingUtils>() {

			@Mock
			public void $clinit() {/* do nothing */}

			@Mock(invocations = 1)
			public void sendMessage(String endpoint, String sessionId, String message) {
				logger.info("Broadcasting message {}", message);
				assertThat(message, containsString("\"objectID\":\"Milite Ignoto _ 02\""));
			}
		};

		Session session = new MockSession().getMockInstance();

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_fee");

		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));

		String message = mapper.writeValueAsString(eventWrapper);

		toPanelEndpoint.rovdemoEngine = new RovDemoEngine();
		((RovDemoEngine) toPanelEndpoint.rovdemoEngine).readExampleData();

		toPanelEndpoint.onMessage(
				mapper.writeValueAsString(eventWrapper) /*message */,
				new MockSession().getMockInstance() /* session */);
	}

	@Test(enabled = true)
	public void shouldRespondParkingStationsFree() throws Exception {

		new MockUp<MessagingUtils>() {
			@Mock
			public void $clinit() {/* do nothing */}

			@Mock(invocations = 1)
			public void sendMessage(String endpoint, String sessionId, String message) {
				assertThat(message, containsString("\"objectID\":\"Abetone _ 01\""));
			}
		};

		Session session = new MockSession().getMockInstance();

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_free");

		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));

		String message = mapper.writeValueAsString(eventWrapper);

		toPanelEndpoint.rovdemoEngine = new RovDemoEngine();
		((RovDemoEngine) toPanelEndpoint.rovdemoEngine).readExampleData();

		toPanelEndpoint.onMessage(message, session);
	}


	@Test(enabled = true)
	public void shouldRespondParkingStationsMacro() throws Exception {

		new MockUp<MessagingUtils>() {
			@Mock
			public void $clinit() {/* do nothing */}

			@Mock(invocations = 1)
			public void sendMessage(String endpoint, String sessionId, String message) {
				assertThat(message, containsString("\"objectID\":\"STADIO_B\""));
			}
		};

		Session session = new MockSession().getMockInstance();

		Event event = Event.loadFromFile("park_and_ride_at_rovereto_macro");

		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));

		String message = mapper.writeValueAsString(eventWrapper);

		toPanelEndpoint.rovdemoEngine = new RovDemoEngine();
		((RovDemoEngine) toPanelEndpoint.rovdemoEngine).readExampleData();

		toPanelEndpoint.onMessage(message, session);
	}


	@Test(enabled = true)
	public void onTimeoutShouldCallStopAllThreads(
			@Injectable Session s, @Mocked EventProcessor processor, @Mocked CloseReason closeReason, @Mocked SessionManager sessionManager) throws Exception {

		Map<String, EventProcessor> activeEventProcessors = new HashMap<>();

		activeEventProcessors.put("s1", processor);

		setField(toPanelEndpoint, "activeEventProcessors", activeEventProcessors);

		new Expectations() {{
			s.getId();
			result = "s1";
		}};

		toPanelEndpoint.onClose(s, closeReason);

		new Verifications() {{
			processor.stop();
			times = 1;
		}};

	}


	@Test(enabled = true)
	public void onMessageShouldCreateOneEventProcessorPerSession(
			@Injectable Session s1, @Injectable Session s2, @Mocked EventProcessor processor) throws Exception {

		new Expectations() {{
			s1.getId();
			result = "1";
			s2.getId();
			result = "2";
		}};

		Event event = Event.loadFromFile("basic_event");

		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));

		String message = mapper.writeValueAsString(eventWrapper);

		toPanelEndpoint.onMessage(message, s1);
		toPanelEndpoint.onMessage(message, s2);
		toPanelEndpoint.onMessage(message, s1);

		new Verifications() {{
			new EventProcessor((ToPanelEndpoint) any, anyString);
			times = 2;
		}};

		assertThat(ToPanelEndpoint.activeEventProcessors.keySet(), hasSize(2));
		assertThat(ToPanelEndpoint.activeEventProcessors, allOf(hasKey("1"), hasKey("2")));
	}


	@Test(enabled = true)
	public void callbackShouldBroadcastTheMessageToEndpointName(@Mocked MessagingUtils mu) throws Exception {

		toPanelEndpoint.callback("1", "someId");

		new Verifications() {{
			mu.sendMessage("the_endpoint_name", "someId", "1");
			times = 1;
		}};
	}

	@Test(enabled = true)
	public void shouldUnderStandAngularStringifiedObjects(
			@Mocked EventProcessor eventProcessor,
			@Mocked Session session) throws Exception {

		new Expectations() {{
			session.getId();
			result = "1";
		}};

		toPanelEndpoint.onMessage("{\"event\":\"{\\\"useCaseID\\\":\\\"ParkAndRide@Tampere\\\",\\\"live\\\":\\\"start\\\",\\\"context\\\":{\\\"select\\\":\\\"ParkingArea\\\"}}\"}", session);

		List<Event> eventCaptures = new ArrayList<Event>();

		new Verifications() {{
			eventProcessor.setEvent(withCapture(eventCaptures));
			times = 1;
		}};

		Event event = eventCaptures.get(0);
		assertThat(event.getUseCaseID(), is("ParkAndRide@Tampere"));
		assertThat(event.getLive(), is("start"));
		assertThat(event.getContext().getSelect(), is("ParkingArea"));
	}

	@Test(enabled = true)
	public void shouldRespondWithCO2EmissionsForBerlin() throws Exception {

		new MockUp<MessagingUtils>() {
			@Mock
			public void $clinit() {/* do nothing */}

			@Mock(invocations = 1)
			public void sendMessage(String endpoint, String sessionId, String message) throws IOException {
				// LOG.info("VmzDtvwData data in shouldRespondWithCO2EmissionsForBerlin is: {}", message);
				assertThat(Util.prettify(message), containsString("\"label\" : \"traffic_rate"));
			}
		};

		Session session = new MockSession().getMockInstance();
		String message = mapper.writeValueAsString(new EventWrapper(mapper.writeValueAsString(Event.loadFromFile("co2_ber_event"))));

		Event event = Event.loadFromFile("co2_ber_event");

		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));

		toPanelEndpoint.berco2Engine = new BerCO2Engine();
		toPanelEndpoint.onMessage(message, session);

	}


	@Test(enabled = true)
	public void shouldRespondWithChartsForBerlin() throws Exception {

		new MockUp<MessagingUtils>() {
			@Mock
			public void $clinit() {/* do nothing */}

			@Mock(invocations = 1)
			public void sendMessage(String endpoint, String sessionId, String message) {
				LOG.info("got chart JSON back: \n{}", Util.shorten(message, 222));
				assertThat(message, containsString("discreteBarChart"));
			}
		};

		Session session = new MockSession().getMockInstance();

		// String message = mapper.writeValueAsString(new EventWrapper(mapper.writeValueAsString(Util.loadFromFile("chart_request"))));

		Event event = Event.loadFromFile("chart_request");
		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));
		String message = mapper.writeValueAsString(eventWrapper);

		//toPanelEndpoint.berco2Engine = new BerCO2Engine();
		toPanelEndpoint.onMessage(message, session);
	}


	@Test(enabled = true)
	public void shouldRespondWithRealChartForBerlin() throws Exception {

		new MockUp<MessagingUtils>() {
			@Mock
			public void $clinit() {/* do nothing */}

			@Mock(invocations = 1)
			public void sendMessage(String endpoint, String sessionId, String message) throws IOException {
				LOG.info("got chart JSON back: \n{}", Util.shorten(message, 2222));
				assertThat(Util.prettify(message), containsString("\"key\" : \"vehicles per day\","));
			}
		};

		Session session = new MockSession().getMockInstance();
		Event event = Event.loadFromFile("chart_request_BerCO2_default");
		EventWrapper eventWrapper = new EventWrapper(mapper.writeValueAsString(event));
		String message = mapper.writeValueAsString(eventWrapper);

		//toPanelEndpoint.berco2Engine = new BerCO2Engine();
		toPanelEndpoint.onMessage(message, session);
	}


}
