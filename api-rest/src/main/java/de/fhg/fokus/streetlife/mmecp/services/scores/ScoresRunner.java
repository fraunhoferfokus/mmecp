package de.fhg.fokus.streetlife.mmecp.services.scores;

import de.fhg.fokus.streetlife.mmecp.containers.util.ConcurrentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by csc on 08.02.2016.
 */
public class ScoresRunner implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScoresServer serverLink = null;
    private ScoresFetcher fetcher = null;

    public ScoresRunner(ScoresServer serverLink) {
        this.serverLink = serverLink; // save server to call back for later, use it in run()

        try {
            this.fetcher = new ScoresFetcher();
            this.executor.scheduleAtFixedRate(this, 0L, 60L, TimeUnit.SECONDS);
        } catch (IOException e) {
            LOG.error(e.toString());
        }
    }


    @Override
    public void run() {
        try {
            String json = fetcher.getAllScores();
            serverLink.writeJsonCache(json);
        } catch (IOException e) {
            LOG.error("ScoresFetcher could not gather user scores data: {}", e);
        }
    }


    public void stop() {
        if (executor != null) {
            ConcurrentUtils.stop(executor);
        }
    }


    public String setCutoffDate(String dateString) {
        String reply = "no fetcher";
        if (this.fetcher != null) {
            reply = this.fetcher.setCutoffDate(dateString);
        }

        // further to setting the cutoff date, re-fetch all user scores, so that the new date becomes effective immediately
        this.run();

        return reply;
    }

}
