package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by flo on 07/22/15.
 */
public class BerStaticBikeSharingDataEngine implements ResponseParseEngine {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private JsonNode exampleData = null;

	private static final String USECASENAME = "StaticBikeSharingData@Berlin";
	public String getUseCaseID() {return USECASENAME;}

	@Override
    public String parseResponse(String response) {
    	return null;
    }

	@Override
	public String handleEvent(Event event) {
		return null;
	}


}
