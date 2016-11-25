package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by bdi on 03/11/14.
 */

@javax.websocket.ClientEndpoint
public class ClientEndpointTest {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @OnOpen
	public void onOpen(Session session) {
		try {
			LOG.info("[Client] Session opened, send request for ParkingStations");
			session.getBasicRemote().sendText("getObjectsOfType:ParkingStations");
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        LOG.info("[Client] Received message: {}", message);
    }
}
