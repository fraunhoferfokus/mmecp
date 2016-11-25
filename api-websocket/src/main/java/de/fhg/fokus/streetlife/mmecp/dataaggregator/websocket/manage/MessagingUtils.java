package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;
import java.io.IOException;
import java.io.Serializable;

import javax.websocket.*;
/**
 * Created by bdi on 06/11/14.
 */
@ApplicationScoped
public class MessagingUtils implements Serializable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Inject
    private SessionManager sm;

    /**
     * Sends a message to all sessions that are connected to an endpoint.
     *
     * @param endpoint The endpoint to broadcast the message
     * @param message The message
     * @throws SessionManagerException Thrown if the endpoint does not exists
     * @throws IOException Thrown if ...
     */
    public void broadcastMessage(String endpoint, String message) throws SessionManagerException, IOException {
        if (!sm.getEndpointSessions(endpoint).isEmpty()) {
            for (Session session : sm.getEndpointSessions(endpoint)) {
                RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
                if(basicRemote != null){
                    session.getBasicRemote().sendText(message);
                }else{
                    LOG.warn("Could not send message {} because session remote is null.", message);
                }
            }
        } else {
            LOG.warn("No sessions at endpoint {}", endpoint);
        }
    }

    /**
     * Sends a message to a specific session for this endpoint
     * 
     * @param endpoint The endpoint to send the message
     * @param sessionId the sessionId
     * @param message The Message to send
     */
    public void sendMessage(String endpoint, String sessionId, String message) throws SessionManagerException, IOException {
        if (!sm.getEndpointSessions(endpoint).isEmpty()) {
            for (Session session : sm.getEndpointSessions(endpoint)) {
                if(session.getId().equals(sessionId)){
                    RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
                    if(basicRemote != null){
                        session.getBasicRemote().sendText(message);
                    }else{
                        LOG.warn("Could not send message {} because session remote is null.", message);
                    } 
                }
            }
        } else {
            LOG.warn("No sessions at endpoint {}", endpoint);
        }
    }
}
