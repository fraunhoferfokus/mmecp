package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bdi on 21/12/14.
 */
public class FiWareEngine implements ResponseParseEngine {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String USECASENAME = "FiWare";
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
