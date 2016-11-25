package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by csc on 24.02.2016.
 */
public class SongKickService {

    private static final Logger LOG = LoggerFactory.getLogger(SongKickService.class);

    private static final HttpHost target = new HttpHost("api.songkick.com", 443, "https");
    private static final String Api = "/api/3.0";
    private static final String ApiKey = "?apikey=FBXGTcVM94k5lHpa";
    private static final String songKickUrl = Api + "/metro_areas/28443/calendar.json" + ApiKey;
    private static final String venueDetails = Api + "/venues/{venue_id}.json" + ApiKey;

    public static String fetchSongKickBerlinAreaData() throws IOException {
        return fetchSongKickBerlinAreaData(/*page=*/1);
    }

    public static String fetchSongKickBerlinAreaData(int page) throws IOException {

        final HttpGet getRequest = new HttpGet(songKickUrl + "&page=" + page);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpResponse response = httpClient.execute(target, getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("Error during SongKick API access: http " + statusCode);
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("got no data back via SongKick http GET metro_area calendar request");
            }

            String json = EntityUtils.toString(entity);

            LOG.info("Okay. got SongKick Berin metroArea event data; Json reply size=" + json.length());
            return json;

        } catch (IOException e) {
            LOG.error("Could not fetch SongKick events data: " + e.toString());
            throw e;
        }
    }


    public static String fetchSongKickVenueDetails(Integer venueId) throws IOException {

        final HttpGet getRequest = new HttpGet(venueDetails.replace("{venue_id}", venueId.toString()));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpResponse response = httpClient.execute(target, getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("Error during SongKick API access for venue details: http " + statusCode);
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("got no data back via SongKick http GET venue details request");
            }

            String json = EntityUtils.toString(entity);

            LOG.info("Okay. got SongKick Berin venue details data; Json reply size=" + json.length());
            return json;

        } catch (IOException e) {
            LOG.error("Could not fetch SongKick venue details data: " + e.toString());
            throw e;
        }
    }


}
