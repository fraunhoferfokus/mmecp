package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.ConcurrentUtils;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.ToPanelCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by csc on 08.12.2015.
 */
public abstract class BaseLiveEngine implements LiveResponseParseEngine {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService executor  = Executors.newScheduledThreadPool(10);
    // private BaseDataRunner runner             = null;
    private Map<String,Future> runners         = new HashMap<>();
    private ToPanelCallback callbackEndpoint   = null;
    private String sessionId                   = null;


    public synchronized void start(Event event, BaseDataRunner runner) {this.start(event, runner, 0);}

    public synchronized void start(Event event, BaseDataRunner runner, int scheduleEverySeconds) {
        if (event == null || runner == null) return;

        LOG.info("Data life runner START() called.");

        Future f = this.runners.get(event.getOptionID());
        if (f != null && !f.isDone()) return;  // do not start a new thread in case of an already running thread for the option

        if (scheduleEverySeconds > 0) {
            f = executor.scheduleAtFixedRate(runner, 0L, scheduleEverySeconds, TimeUnit.SECONDS);
        } else {
            f = executor.schedule(runner, 0L, TimeUnit.SECONDS);
        }
        this.runners.put(event.getOptionID(), f);
        LOG.info("Data life runner : STARTED.");
    }


    public synchronized void stop(Event event) {
        if (event == null) return;

        LOG.info("Data life runner STOP() called.");
        Future f = this.runners.get(event.getOptionID());
        if (f != null) {
            if (!f.isDone()) {
                f.cancel(true);
            }
            this.runners.remove(event.getOptionID());
        }
        LOG.info("Data life runner STOP() : executor stop initiated.");
    }


    public void stopAllThreads() {
        if(executor != null){
            ConcurrentUtils.stop(executor);
        }
    }


    protected boolean isRunning(String optionID) {
        if (optionID == null || optionID.isEmpty()) return false;
        Future f = this.runners.get(optionID);
        if (f == null) return false;
        return !f.isDone() && !f.isCancelled();
    }


    public void setSessionId(String sessionId) {this.sessionId = sessionId;}
    public String getSessionId() {return this.sessionId;}

    public void setEndpointCallback(ToPanelCallback endpointCallback) {this.callbackEndpoint = endpointCallback;}
    public ToPanelCallback getEndpointCallback() {return this.callbackEndpoint;}


}
