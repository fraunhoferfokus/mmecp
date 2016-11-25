package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.ToPanelCallback;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Created by csc on 17.11.2015.
 */
public abstract class BaseDataRunner implements Runnable, ToPanelCallback {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected LiveResponseParseEngine parent;
    protected Event event;
    protected boolean firstTime = true;
    // protected boolean running = true;

    public BaseDataRunner(LiveResponseParseEngine parent, Event event) {
        this.parent = parent;
        this.event = event;
    }

    public String getSessionId() {return (parent == null) ? null : parent.getSessionId();}

    public boolean isCanceled() {return Thread.currentThread().isInterrupted();}

    public void callback(String result) throws IOException, SessionManagerException {
        callback(result, getSessionId());
    }

    public void callback(String result, String sessionId) throws IOException, SessionManagerException {
        if (parent != null) {
            ToPanelCallback panel = parent.getEndpointCallback();
            if (panel != null) {
                try {
                    panel.callback(result, sessionId);
                } catch (InterruptedIOException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


}
