package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.ToPanelCallback;

/**
 * Created by bdi on 21/12/14.
 */
public interface LiveResponseParseEngine extends ResponseParseEngine {

    public void setEndpointCallback(ToPanelCallback endpointCallback);
    public ToPanelCallback getEndpointCallback();

    public void setSessionId(String sessionId);
    public String getSessionId();

    public void start(Event event, BaseDataRunner runner);
    public void stop(Event event);

    public void stopAllThreads();

}
