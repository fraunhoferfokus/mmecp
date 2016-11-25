package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.impl;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.ResponseEngineFactory;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.ChartRequestHandler;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.LiveResponseParseEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.ResponseParseEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.ToPanelEndpoint;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Der EventProcessor verarbeitet Events die von einem Panel an das Backend versendet werden.
 * Ein Aufrufer, der ein Event abgearbeitet haben möchte kann entweder ein neues EventProcessor Objekt erstellen oder
 * in einem bestehendem EventProcessor per #setEvent(Event) das aktuelle Event eintragen.
 *
 * Soll ein Event abgearbeitet werden, so muss die #process() Methode aufgerufen werden.
 *
 * EventProcessor ist als Monitor-Object entworfen und somit Thread-Safe.
 *
 * Sollen in Zukunft weitere Events hinzugefügt werden, so muss in der #process() Methode ein entsprechender
 * Abfragezweig eingefügt werden.
 *
 * Live Events und einmalige Events werden unterschiedlich behandelt. Live Events werden durch die #process() Methode
 * in einem eigenem Thread abgearbeitet. Dessen Ergebnisse werden kontinuierlich an einen callbackEndpoint weitergegeben.
 * Ein einmaliges Event wird hingegen durch die #process() Methode direkt verarbeitet. Das Ergebnis wird über sie zurückgegeben.
 *
 * @author tfl <florian.thiemer@fokus.fraunhofer.de>
 */
public class EventProcessor {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private Event event;

	private final ToPanelEndpoint callbackEndpoint;

	private String sessionId;

	private ResponseParseEngine engine = null;

	/**
	 * EventProcessor Constructor der einen ToPanelEndpoint als Callback erwartet. Dieser wird im Falle eines Live-Events aufgerufen.
	 * Es ist Aufgabe des Aufrufers Events über die #setEvent(Event) Methode zu setzen.
	 *
	 * @param callbackEndpoint ToPanelEndpoint der im Falle eines Live-Events durch die #process() Methode aufgerufen wird
	 *
	 */
	public EventProcessor(final ToPanelEndpoint callbackEndpoint, String sessionId) {
		this.callbackEndpoint = callbackEndpoint;
		this.sessionId = sessionId;
		this.event = new Event();
	}

	/**
	 * EventProcessor Constructor der einen ToPanelEndpoint als Callback erwartet und das aktuelle Event setzt.
	 *
	 * Der Aufrufer kann anschließend sofort mit #process() das Event abarbeiten.
	 *
	 * @param callbackEndpoint ToPanelEndpoint der im Falle eines Live-Events durch die #process() Methode aufgerufen wird
	 * @param event Event das abgearbeitet werden soll
	 */
	public EventProcessor(final ToPanelEndpoint callbackEndpoint, Event event) {
		this.callbackEndpoint = callbackEndpoint;
		this.sessionId = "";
		this.event = event;
	}

	public synchronized void setEvent(Event event) {this.event = event;}
	public synchronized Event getEvent() {return this.event;}

	public synchronized String getId() {
		if(event != null && event.hasId()) {
			return (String)event.getContext().where.get("id");
		}
		return null;
	}

	private static final String ERRORMESSAGE = "{\n"+
			"\t\"error\":\n"+
			"\t{\n"+
			"\t\t\"message\" : \"MMECP is currently not available. Please try again later.\"\n"+
			"\t}\n"+
			"}";

	public synchronized String process(Event event) {
		this.setEvent(event);
		return this.process();
	}

	public synchronized String process() {
		if(event == null ) {return null;}

		// if no useCaseID has been set, do not attempt to get a ResponseParseEngine!
		// if no useCaseID has been set, only do this:
		if (event.getUseCaseID() == null || event.getUseCaseID().isEmpty()) {
			if ("List<Filter>".equals(event.fetchReturnType())) {
				try {
					return ConfigReader.readConfigJson();
				} catch (IOException e) {
					LOG.error("Error reading filters.json: {}", e.getCause());
					return ERRORMESSAGE;
				}
			}
		}

		// fine, a useCaseID exists -> LookupColor and create the respective ResponseParseEngine and invoke it!
		try {
			// only make a new engine, if none exists yet or the use case has changed from the previous event

			// if (engine == null || engine.getUseCaseID() != event.getUseCaseID()) {
			if (engine == null || !engine.getUseCaseID().equals(event.getUseCaseID())) {

				// at first stop any possibly existing, stored engine
				this.stop();
				// then make a new one, according to the new use case
				this.engine = ResponseEngineFactory.getTheFactory().makeResponseParseEngine(event.getUseCaseID());

				if (engine instanceof LiveResponseParseEngine) {
					((LiveResponseParseEngine) this.engine).setEndpointCallback(callbackEndpoint);
					((LiveResponseParseEngine) this.engine).setSessionId(sessionId);
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if(engine == null) { return null; }

		// standard processing for all engines:
		String response;
		if (event.isChartRequest() && engine instanceof ChartRequestHandler) {
			// chart requests are handled by "handleChartRequest", if supported by an engine
			response = ((ChartRequestHandler)engine).handleChartRequest(event);
		} else {
			// every event that does not contain a ChartRequest is handled as usual by "handleEvent"
			response = engine.handleEvent(event);
		}
		// LOG.info("Generated JSON Reply is:\n{}", Util.shorten(response, 444));
		return response;
	}


	public synchronized void stop() {
		if (this.engine != null && this.engine instanceof LiveResponseParseEngine) {
			((LiveResponseParseEngine)this.engine).stopAllThreads();
		}
	}


}
