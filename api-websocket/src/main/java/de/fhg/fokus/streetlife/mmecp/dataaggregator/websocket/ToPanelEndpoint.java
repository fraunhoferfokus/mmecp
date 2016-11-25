package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket;

import de.fhg.fokus.streetlife.mmecp.configurator.MMECPConfig;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.dto.EventWrapper;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.BerCO2Engine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.ResponseParseEngineMethod;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.EngineType;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.ResponseParseEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.ToPanelCallback;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.impl.EventProcessor;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.MessagingUtils;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManager;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ServerEndpoint(value = "/panelui")
public class ToPanelEndpoint implements ToPanelCallback {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private ObjectMapper mapper = new ObjectMapper();

	// for simulation
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
	private final int TWENTY_SECONDS = 20 * 1000;
	List<String> demoObject = new ArrayList<>();
	List<String> demoNotification = new ArrayList<>();
	protected static Map<String, EventProcessor> activeEventProcessors = new HashMap<>();

	@EJB
	protected DataAggregatorClient dac;

	@Inject
	protected SessionManager sm;

	@Inject
	protected MessagingUtils mu;

	@Inject
	@MMECPConfig(value = "mmecp.backend.api.websocket.endpoint.panelui")
	private String endpointName;

	@Inject
	@ResponseParseEngineMethod(EngineType.ROVDEMO)
	protected ResponseParseEngine rovdemoEngine;
	//@ResponseParseEngineMethod(EngineType.FIWARE)
	//private ResponseParseEngine fiwareEngine;
	@ResponseParseEngineMethod(EngineType.BERCO2)
	protected BerCO2Engine berco2Engine;


	@PostConstruct
	public void init() {
		//dac = DataAggregatorFactory.newInstance("POJO").getClient();
		//dac.init();
	}


	@OnOpen
	public void onOpen(Session session) throws IOException, SessionManagerException {
		if (session == null) {return;}
		LOG.info("User {} connected...", session.getId());
		sm.addEndpointSession(endpointName, session);
		//dac = DataAggregatorFactory.newInstance("POJO").getClient();
		//dac.init();
	}


	@OnMessage
	public void onMessage(String message, Session session) throws IOException, SessionManagerException {
		if (session == null || message == null) {return;}

		LOG.info("Received request {}", message);

		EventWrapper eventWrapper = mapper.readValue(message, EventWrapper.class);
		Event event = mapper.readValue(eventWrapper.getEvent(), Event.class);

		LOG.info("Received event {}", eventWrapper.getEvent());

		// Lookup existing Sessions
		if(activeEventProcessors.containsKey(session.getId()) == false) {
			activeEventProcessors.put(session.getId(), new EventProcessor(this, session.getId()));
		}

		EventProcessor ep = activeEventProcessors.get(session.getId());
		ep.setEvent(event);
		String response = ep.process();

		if(response != null){
			LOG.info("RESPONSE ({} bytes) starts with:\n{}", response.length(), Util.shorten(response, 1111));
			// Util.writeToFile(response);
			mu.sendMessage(endpointName, session.getId(), response);
		}
	}


	@OnClose
	public void onClose(Session session, CloseReason closeReason) throws IOException, SessionManagerException {
		if (session == null || closeReason == null) {return;}

		LOG.info("Connection to user {} closed because: {}", session.getId(), closeReason.toString());
		
		EventProcessor ep = activeEventProcessors.get(session.getId());
		if(ep != null){
			LOG.info("Stopping all active Threads...");
			ep.stop();
		}
		sm.removeSession(endpointName, session);
	}


	@OnError
	public void onError(Session session, Throwable t) {
		if (session == null || t == null) {return;}

		LOG.error("Something went wrong in session [{}]", session.getId(), t);
		LOG.error("Trying to close session {}", session.getId());
		try {
			onClose(session, new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, t.getMessage()));
			LOG.error("Session {} closed", session.getId());
		} catch (IOException | SessionManagerException e) {
			LOG.error("Can not close session [{}]", session.getId(), e);
		}
	}


	public synchronized void callback(String result, String sessionId) throws IOException, SessionManagerException {
		if (result != null && sessionId != null) {
			mu.sendMessage(endpointName, sessionId, result);
		}
	}


	private void setNewGuidance(String guidance) {
		//TODO generate guidance and save to data storage
	}


}
