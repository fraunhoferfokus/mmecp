package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bdi on 06/11/14.
 */
@ApplicationScoped
public class SessionManager implements Serializable {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private Map<String, List<Session>> sessionMap;

	@PostConstruct
	public void init() {
		LOG.debug("@PostConstruct is working");
		sessionMap = new HashMap<String, List<Session>>();

	}

	/**
	 * Returns a list of all active sessions that are belonging to an endpoint.
	 *
	 * @param endpoint
	 *            The endpoint the sessions are belonging to
	 * @return A list of sessions
	 * @throws SessionManagerException
	 *             Thrown if the endpoint does not exists
	 */
	public List<Session> getEndpointSessions(String endpoint) throws SessionManagerException {
		List<Session> sessions;

		if (sessionMap.get(endpoint) != null)
			sessions = sessionMap.get(endpoint);
		else
			throw new SessionManagerException("Endpoint " + endpoint + " does not exist.");

		return sessions;
	}

	/**
	 * Created a new endpoint with an empty list for sessions if endpoint is not existing.
	 *
	 * @param endpoint
	 *            The endpoint to create
	 */
	public void addEndpoint(String endpoint) {
		if (sessionMap.putIfAbsent(endpoint, new ArrayList<Session>()) != null)
			LOG.warn("Endpoint {} already exists", endpoint);
		else
			LOG.info("Added new endpoint {}", endpoint);
	}

	/**
	 * Removes a given endpoint including all its sessions.
	 *
	 * @param endpoint
	 *            The endpoint to remove
	 */
	public void removeEndpoint(String endpoint) {
		sessionMap.remove(endpoint);
		LOG.info("Removed endpoint {} with all its sessions", endpoint);
	}

	/**
	 * Adds a session to an endpoint. Creates an endpoint with an empty list if the endpoint not exist.
	 *
	 * @param endpoint
	 *            The endpoint to create
	 * @param session
	 *            The session to create
	 * @throws SessionManagerException
	 *             Thrown if session already exists
	 */
	public void addEndpointSession(String endpoint, Session session) throws SessionManagerException {
		if (sessionMap.get(endpoint) == null) {
			this.addEndpoint(endpoint);
		}

		if (!sessionMap.get(endpoint).contains(session)) {
			sessionMap.get(endpoint).add(session);
			LOG.info("Added Session {} to endpoint {}", session.getId(), endpoint);
		} else {
			throw new SessionManagerException("Session " + session.getId() + " already exists in endpoint " + endpoint);
		}
	}

	/**
	 * Removes a session from an endpoint.
	 *
	 * @param endpoint
	 *            The endpoint where to remove the session
	 * @param session
	 *            The session to remove
	 */
	public void removeSession(String endpoint, Session session) {
		if (sessionMap.get(endpoint).remove(session))
			LOG.info("Session {} removed from endpoint {}", session.getId(), endpoint);
		else
			LOG.warn("Can not remove session {}, because it does not exist in endpoint {}", session, endpoint);
	}

}
