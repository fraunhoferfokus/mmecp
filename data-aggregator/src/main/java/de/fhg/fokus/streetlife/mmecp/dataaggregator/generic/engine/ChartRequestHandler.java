package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;

/**
 * Created by bdi on 21/12/14.
 */
public interface ChartRequestHandler {

    public String handleChartRequest(Event event);
}
