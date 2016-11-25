package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by csc on 10/01/15.
 */
public class BerBikeSharingEngine implements ResponseParseEngine, ChartRequestHandler {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private static final String USECASENAME = "BikeSharing@Berlin";
	public String getUseCaseID() {return USECASENAME;}

	private JsonNode exampleData = null;


	@Override
    public String parseResponse(String response) {
    	return null;
    }


	@Override
	public String handleEvent(Event event) {
		return null;
	}


	@Override
	public String handleChartRequest(Event event) {
		return null;
	}


}
