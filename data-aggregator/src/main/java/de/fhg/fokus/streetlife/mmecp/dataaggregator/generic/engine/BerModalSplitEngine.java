package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.chart.*;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.CipClient;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.SimpleCache;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.Area;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObject;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.geotools.io.NumberedLineWriter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;


/**
 * Created by csc on 01/07/15.
 */
public class BerModalSplitEngine extends BaseLiveEngine implements ChartRequestHandler {

    private static SimpleCache<String> replyCache      = new SimpleCache<>();

    private static final String USECASENAME = "ModalSplit@Berlin";
    public String getUseCaseID() {return USECASENAME;}

    private final ObjectMapper mapper = new ObjectMapper();

    private VenueCache venueCache = new VenueCache();

    public BerModalSplitEngine() {
    }

    public String parseResponse(String response) {
        // LOG.info("parseResponse() passing message: {}", Util.shorten(response, 888));
        return response;
    }
    

    
    public String handleSongKickRequest(Event userevent){

        // look in replyCache at first
        String cachedResponse = replyCache.get(userevent.getOptionID());
        if (cachedResponse != null) {
            return this.parseResponse(cachedResponse);
        }

        Map<Integer, List<JsonNode>> eventsPerVenueMap = new HashMap<>(1000);

        try {
            for (int i=1; i<=34; i++) {
                String json = SongKickService.fetchSongKickBerlinAreaData(/*page=*/i);
                JsonNode root = mapper.readTree(json);

                String status = root.get("resultsPage").get("status").asText();
                if (!"ok".equals(status)) throw new IOException("Could not retrieve songkick events data: " + status);

                //LOG.info("Got songkick data: " + Util.shorten(Util.prettify(json), 2222));

                JsonNode events = root.get("resultsPage").get("results").get("event");
                if (events == null || events.isNull() || !events.isArray()) continue;

                // collect all events, indexed by venue-id
                Iterator<JsonNode> iter = events.iterator();
                while (iter.hasNext()) {
                    JsonNode event = iter.next();
                    if (event == null || event.isNull() || !event.isObject()) continue;

                    JsonNode ven = event.get("venue");
                    if (ven == null || ven.isNull() || !ven.isObject()) continue;
                    JsonNode vid = ven.get("id");
                    if (vid == null || vid.isNull() || !vid.isNumber()) continue;
                    Integer venueId = vid.asInt();

                    List<JsonNode> eventList = eventsPerVenueMap.get(venueId);

                    if (eventList != null) {
                        eventList.add(event);
                    } else {
                        List<JsonNode> newEventList = new ArrayList<>();
                        newEventList.add(event);
                        eventsPerVenueMap.put(venueId, newEventList);
                    }
                }
            }

            MapObjectSet dataset = new MapObjectSet();

            /*
            for (JsonNode venue : root.findValues("venue")) {
                MapObject mo = new MapObject(venue.get("id").asText(), "subType")
                        .withAttribute("name", venue.get("displayName").asText())
                        .withIconAt(new GpsCoordinate(venue.get("lat").asDouble(), venue.get("lng").asDouble()), Area.Icon.EVENT_ICON);
                dataset.add(mo);
            }
            */

            for (Map.Entry<Integer, List<JsonNode>> entry : eventsPerVenueMap.entrySet()) {
                Integer venueId = entry.getKey();
                if (venueId == null) continue;
                List<JsonNode> venueEvents = entry.getValue();
                if (venueEvents == null || venueEvents.size() == 0) continue;
                JsonNode theVenue = venueEvents.get(0).get("venue");

                // create venue icon with name as attribute to display on Berlin city map
                MapObject mo = new MapObject("vid_"+venueId, "subType")
                        .withAttribute("venue", theVenue.get("displayName").asText())
                        .withIconAt(new GpsCoordinate(theVenue.get("lat").asDouble(),theVenue.get("lng").asDouble()),Area.Icon.EVENT_ICON);

                // try to find out the capcacity of a venue via its venue id
                JsonNode venueNode = venueCache.get(venueId);
                if (venueNode != null && venueNode.isObject()) {
                    //LOG.info("Venue details: " + venueNode.toString());
                    JsonNode venueSize = venueNode.get("capacity");
                    if (venueSize != null && venueSize.isNumber()) {
                        mo.withAttribute("capacity", venueSize.asInt());
                    }
                }

                // add all events for a venue as MapObject attributes
                int i=0;
                for (JsonNode event : venueEvents) {
                    if (!"ok".equals(event.get("status").asText())) continue;
                    i++;
                    String displayName;
                    try {
                        if ("Festival".equals(event.get("type").asText())) {
                            displayName = event.get("displayName").asText();
                        } else {
                            displayName = event.get("performance").iterator().next().get("displayName").asText();
                        }
                    } catch (Exception e) {
                        displayName = "Unknown";
                    }
                    mo.withAttribute(event.get("start").get("date").asText(),displayName);
                }
                if (i>0) dataset.add(mo);
            }

            dataset.setIDsFrom(userevent);
            LOG.info("DATASET SIZE is: " + dataset.size());
            String response = dataset.toJsonString();

            // store generated reply in cache
            // replyCache.put(event.getOptionID(), response);
            return this.parseResponse(response);
        } catch (Exception e) {
            LOG.error("Modal Split Engine failure: " + e.getMessage());
            return null;
        }
    }


    public String handleEvent(Event userevent) {
        if (userevent == null) return null;


        
        
        
        if ("ber_ms_full".equals(userevent.getOptionID())){ //songkicks events
        	
        	return this.handleSongKickRequest(userevent);
        	 	
        }
        
        
        
        
        if ("ber_ms_bs".equals(userevent.getOptionID())){ //bike stations
        	
        	return this.handleBikeStationsForecastRequest(userevent);
        	
        	
        }

        if ("ber_ms_bs_all".equals(userevent.getOptionID())){ //bike stations

            return this.handleAlleBikeStationsRequest(userevent);


        }
        return null;
        
        
        

    }

    private String handleAlleBikeStationsRequest(Event userevent) {
        BikeStationsService bikeStationsService = BikeStationsService.getInstance();
        String response =  bikeStationsService.getAllBikeStationsAsMapObject(userevent,"0",false);
        return this.parseResponse(response);



    }


    private String handleBikeStationsForecastRequest(Event userevent) {
		// TODO Auto-generated method stub


        LOG.info("bike station forecast request");

        String weekDay = userevent.getContext().getStartWeekDay();

        BikeStationsService bikeStationsService = BikeStationsService.getInstance();
        String response =  bikeStationsService.getAllBikeStationsAsMapObject(userevent,weekDay,true);
        return this.parseResponse(response);

	}
    
    

	public String handleChartRequest(Event event) {
        if (event == null) {return null;}
        if (!event.isChartRequest()) {return "";}
        // else
        LOG.info("BER CO2 chart request handler: got request for chart type: {}", event.getChartRequest().getType());

        // look in replyCache at first
        //String cachedResponse = chartReplyCache.get(event.getOptionID());
        //if (cachedResponse != null) {
        //    return this.parseResponse(cachedResponse);
        //}

        if ("ber_traffic_rates_full".equals(event.getOptionID()))
        {
            try {
                List<Long> values = new ArrayList<Long>() {{
                    add(1L);
                    add(2L);
                    add(3L);
                    add(4L);
                    add(5L);
                    add(6L);
                }};
                ChartData cd = Histogram.makeChartDataWithFixedSizeOfBins("vehicles per day", values,
                               /*size of bins=*/ 10000L, /*calculate percentages*/true);

                ChartOptions cho = new ChartOptions(ChartOptions.TYPE_BARCHART, 340, 500, true, true, 1, 0.1);
                cho.setxAxis("vehicles per day", 8 /*pixels distance*/, -16 /*angle*/);
                cho.setyAxis("occurrence [%]",  25 /*pixels distance*/, 0 /*angle*/);

                Chart chart = new Chart(event, "BER traffic rate density", cho, cd);
                //chart.setDescription("initial BER traffic rate bar chart description");

                String response = new Charts(chart).toJsonString();
                //chartReplyCache.put(event.getOptionID(), response);
                return this.parseResponse(response);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("BER CO2 Engine failure: " + e.getMessage());
                return null;
            }
        } else if ("ber_OTHER_OPTION".equals(event.getOptionID())) {
            return "";
        } else {
            return "";
        }
    }


}
