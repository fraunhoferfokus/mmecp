package de.fhg.fokus.streetlife.mmecp.services.scores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 08.01.2016.
 */
@ApplicationScoped
public class ScoresServer implements ScoresAction {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private ScoresRunner runner = new ScoresRunner(this);

    private String jsonCache = null;

    public ScoresServer() {
    }

    // callback method, invoked by ScoresFetcher
    public void writeJsonCache(String json) {
        this.jsonCache = json;
    }

    @PreDestroy
    void exit() {
        this.runner.stop();
    }

    @Override
    public String test() {
        return "Hello World!";
    }

    @Override
    public Response getAllScores() {
        if (this.jsonCache == null) {
            // upon first invocation, give the Runner max 6 seconds chance to update the cache via its callback
            // before answering with an HTTP 503 (service temporarily) not available error
            try {
                for (int i=0; i<30; i++) {
                    if (this.jsonCache != null) {
                        return Response.ok(this.jsonCache, MediaType.APPLICATION_JSON).build();
                    }
                    Thread.currentThread().sleep(200);
                }
            } catch (InterruptedException e) {
                // do nothing uppon interrupt
            }
            // still no update of the cache?
            if (this.jsonCache == null) {
                return Response.serverError().status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        }
        // else
        return Response.ok(this.jsonCache, MediaType.APPLICATION_JSON).build();
    }

    @Override
    public String setCutoffDate(String dateString) {
        return this.runner.setCutoffDate(dateString);
    }


}
