package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;

import java.io.IOException;

/**
 * Created by csc on 12.10.15
 */
public interface ToPanelCallback {

    /**
     * Broadcasts the result message to the endpointName
     *
     * @param result The result string that have to be broadcast
     *
     * @param sessionId The ID of the ongoing session that should receive the result message
     */
    public void callback(String result, String sessionId) throws IOException, SessionManagerException;

}
