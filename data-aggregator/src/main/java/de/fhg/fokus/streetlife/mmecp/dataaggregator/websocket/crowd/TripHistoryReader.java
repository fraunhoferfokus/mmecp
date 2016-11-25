package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.crowd;

import de.fhg.fokus.streetlife.mmecp.containers.crowd.CipClient;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.TripHistory;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.TripHistoryAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by csc on 07.10.2015.
 */

public class TripHistoryReader {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    TripHistoryAction tripHistoryAction = null;


    public TripHistoryReader() {
        try {
            this.tripHistoryAction = new CipClient().getTarget().proxy(TripHistoryAction.class);
        } catch (GeneralSecurityException e) {
            LOG.error("Cannot instatiate CIP client for TripHistoryReader: " + e.getMessage());
        }
    }


    public TripHistory getTripHistory(String deviceId, boolean initialRun) throws IOException {
        if (initialRun == true) {
            return getTripHistory(deviceId);
        } else {
            return getTripHistoryLatestGame(deviceId);
        }
    }


    private final DateFormat DateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    // date format = 18-03-2016
    private String formatDate(LocalDate date) {
        return DateFormatter.format(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }


    public TripHistory getTripHistoryByDate(LocalDate date) throws IOException {
        if (tripHistoryAction == null || date == null) return null;
        //else
        return parseResponse(tripHistoryAction.getTripHistoryByDate(formatDate(date)));
    }


    public TripHistory getTripHistoryByDatePaged(LocalDate date, int page) throws IOException {
        if (tripHistoryAction == null || date == null) return null;
        //else
        return parseResponse(tripHistoryAction.getTripHistoryByDatePaged(formatDate(date), page));
    }


    public TripHistory getTripHistory(String deviceId) throws IOException {
        if (tripHistoryAction == null) return null;
        //else
        return parseResponse(tripHistoryAction.getTripHistory(deviceId));

    }


    public TripHistory getTripHistoryLatestGame(String deviceId) throws IOException {
        if (tripHistoryAction == null) return null;
        //else
        return parseResponse(tripHistoryAction.getTripHistoryLatestGame(deviceId));
    }


    public TripHistory getTripHistoryCurrentGamePaged(int page) throws IOException {
        if (tripHistoryAction == null) return null;
        //else
        return parseResponse(tripHistoryAction.getTripHistoryCurrentGamePaged(page));
    }


    private TripHistory parseResponse(Response response) throws IOException {
        LOG.info("TripHistoryReader status: " + response.getStatus());
        // LOG.info("TripHistoryReader headers: " + response.getHeaders());

        if (response.getStatusInfo() != Response.Status.OK) {
            throw new IOException("web service failure: " +
                    Integer.toString(response.getStatusInfo().getStatusCode()) + " " +
                    response.getStatusInfo().getReasonPhrase());
        }
        // else, all is okay:

//        Instant start = Instant.now();

        String json = response.readEntity(String.class);
        return new TripHistory(json);

//        Instant end = Instant.now();
//        LOG.info("DURATION ### = " + Duration.between(start, end));

        //return th;
    }

}
