package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by csc on 02.03.2016.
 */
public class VenueCache {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Map<Integer,JsonNode> venueDetailsMap = new HashMap<>();

    public static JsonNode get(Integer venueId) {

        JsonNode node = VenueCache.venueDetailsMap.get(venueId);
        if (node != null) return node;

        // no entry found in cache -> try to fetch the venue details from Songkick now
        try {
            String venueJson = SongKickService.fetchSongKickVenueDetails(venueId);
            JsonNode root = mapper.readTree(venueJson);

            String status = root.get("resultsPage").get("status").asText();
            if (!"ok".equals(status)) throw new IOException("Could not retrieve songkick venue details data: " + status);

            JsonNode venueNode = root.get("resultsPage").get("results").get("venue");
            if (venueNode != null && venueNode.isObject()) {
                venueDetailsMap.put(venueId, venueNode);
                return venueNode;
            }
        } catch (IOException e) {
            return null;
        }

        return null;
    }

}
