package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.Area;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObject;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mpo on 23.09.2016.
 */
public class BikeStationsService {

    private static BikeStationsService instance;

    private static final String serviceUrl =  "https://www.cip.siemens.com/jboss/cip-mobilityguidance/mobilityguidance/";
    private static final String allCurrentBikeSations = "bike/getAllCurrentBikeStationStatus";
    private static final String availabilityProfileForAWeekday = "getAvailabilityProfileByIdAtWeekday/BSId/{{modeId}}/weekdayId/{{weekday}}";
    private static final int MAXIMUM_BIKE_STATIONS = 20; //should be removed in the future

    final HttpGet getRequest = new HttpGet(serviceUrl + allCurrentBikeSations);
    private static final Logger LOG = LoggerFactory.getLogger(BikeStationsService.class);


    private BikeStationsService(){

    }
    public static BikeStationsService getInstance () {
        if (BikeStationsService.instance == null) {
            BikeStationsService.instance = new BikeStationsService ();
        }
        return BikeStationsService.instance;
    }


    public String getAllCurrentBikeStations(){

        String data = "";

        try {

            URL url = new URL(serviceUrl+allCurrentBikeSations);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            //System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                data = data + output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return data;

    }
    public String getAvailabilityProfileForAWeekday(String weekday,String modeId){

        String data = "";

        try {

            String urlString = serviceUrl+availabilityProfileForAWeekday.replace("{{modeId}}",modeId);
            urlString = urlString.replace("{{weekday}}",weekday);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            //System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                data = data + output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return data;

    }




    public String getAllBikeStationsAsMapObject(Event userevent,String weekday,boolean withForecast){

        String json = this.getAllCurrentBikeStations();
        JsonNode root = null;




        MapObjectSet dataset = new MapObjectSet();

        ObjectMapper mapper = new ObjectMapper();
        try {
            root = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // collect all events, indexed by venue-id
        Iterator<JsonNode> iter = root.iterator();
        int loadedBikeStations = 0;
        while (iter.hasNext()) {
            JsonNode bikeStation = iter.next();


            MapObject mapObject = new MapObject("bsid_"+bikeStation.get("modeId"), "subType")
                    .withAttribute("Station Name", bikeStation.get("description").asText())
                    .withAttribute("Station ID", bikeStation.get("modeId").getIntValue())
                    .withIconAt(new GpsCoordinate(bikeStation.get("latitude").asDouble(),bikeStation.get("longitude").asDouble()), Area.Icon.BIKE_STATION_ICON);

            mapObject.withAttribute("Latitude",bikeStation.get("latitude").asDouble()+"");
            mapObject.withAttribute("Longitude",bikeStation.get("longitude").asDouble()+"");

            if(withForecast){

                this.addAvialabilityForecastToMapObject(mapObject,weekday,bikeStation.get("modeId").getIntValue()+"");
                mapObject.withAttribute("Forecast Date",userevent.getContext().getStartDate());
            }


            dataset.add(mapObject);
            if(loadedBikeStations == MAXIMUM_BIKE_STATIONS)
            {
                break;
            }
            loadedBikeStations++;
        }


        dataset.setIDsFrom(userevent);
        String response = dataset.toJsonString();
        //System.out.println(response);
        return response;

    }

    public MapObject addAvialabilityForecastToMapObject(MapObject mapObject,String weekDay,String modeId)
    {
        String avialabilityData = this.getAvailabilityProfileForAWeekday(weekDay,modeId);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode avialabilityDataJson = null;
        try {
            avialabilityDataJson = mapper.readTree(avialabilityData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<JsonNode> iter = avialabilityDataJson.iterator();
        int minutes = 0;
        String minutesShow = "";
        int hours = 0;
        String hoursShow = "";

        //mapObject.withAttribute("--------------","--------------");
        //mapObject.withAttribute("TIME","PREDICTED BIKES AVAILABLE");



        ArrayList<String> diagramDataLabels = new ArrayList<String>();
        ArrayList<Double> diagramDataValues = new ArrayList<Double>();



        while (iter.hasNext()) {
            JsonNode bikes = iter.next();
            hours = minutes / 60;
            minutesShow = "" + (minutes % 60);
            hoursShow = hours+"";
            if(hoursShow.length() == 1)
            {
                hoursShow = "0" + hoursShow;
            }
            if(minutesShow.length() == 1)
            {
                minutesShow = "0" + minutesShow;
            }
            String time = ""+hoursShow+":"+minutesShow;
            //System.out.println(time);
            //System.out.println(bikes.toString());

            // bikes availability  in the table
            //mapObject.withAttribute(time,bikes.toString());

            //bikes availability in the diagram
            diagramDataLabels.add(time);
            diagramDataValues.add(bikes.asDouble());

            minutes = minutes + 30;



        }
        //mapObject.withAttribute("--------------","--------------");
        mapObject.withDiagramData(diagramDataLabels,diagramDataValues,"historicalBarChart","Predicted Bikes Avialable");


        return mapObject;

    }


    }



