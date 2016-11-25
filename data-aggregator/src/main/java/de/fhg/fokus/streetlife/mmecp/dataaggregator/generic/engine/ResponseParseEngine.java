package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;

import java.io.Serializable;

/**
 * Created by bdi on 21/12/14.
 */
public interface ResponseParseEngine extends Serializable {

    public String getUseCaseID();

    public String handleEvent(Event event);

    public String parseResponse(String response);

}
