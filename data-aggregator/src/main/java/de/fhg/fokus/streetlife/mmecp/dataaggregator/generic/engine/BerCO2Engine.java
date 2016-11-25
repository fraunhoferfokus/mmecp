package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.chart.*;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.containers.util.SimpleCache;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl.ShapeFileReader;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.Area;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzDtvwData;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * Created by csc on 01/07/15.
 */
public class BerCO2Engine extends BaseLiveEngine implements ChartRequestHandler {

    private static SimpleCache<String> replyCache      = new SimpleCache<>();
    private static SimpleCache<String> chartReplyCache = new SimpleCache<>();

    private static final String USECASENAME = "CO2Emissions@Berlin";
    public String getUseCaseID() {return USECASENAME;}

    // private JsonNode exampleData = null;

    public BerCO2Engine() {
//		((BerCO2Engine)toPanelEndpoint.berco2Engine).readExampleData();
    }

    public String parseResponse(String response) {
        // LOG.info("BerCO2Engine::parseResponse passing message: {}", Util.shorten(response, 888));
        return response;
    }


    public String handleEvent(Event event) {
        if (event == null) return null;

        // handle example data request
        if (event.hasSelector("example_data")) {
            MapObjectSet mos = VmzDtvwData.makeExampleList(3);
            mos.setIDsFrom(event);
            return this.parseResponse(mos.toJsonString());
        }

        // handle option "ber_traffic_heatmap_life", with life thread
        if ("ber_traffic_heatmap_live".equals(event.getOptionID()) ||
            "ber_traffic_rates_live".equals(event.getOptionID())) {

            if (event.startLiveData()) {
                this.start(event, new BerCO2VmzLifeDataRunner(this, event));

                if ("ber_traffic_heatmap_live".equals(event.getOptionID())) {
                    return new MapObjectSet(event).withLegend(Legend.heatMapLegend).toJsonString();
                }
                if ("ber_traffic_rates_live".equals(event.getOptionID())) {
                    return new MapObjectSet(event).withLegend(Legend.trafficRateLegend).toJsonString();
                }
            } else if (event.stopLiveData()) {
                this.stop(event);
            }
            return "";
        }

        int maxSamples, style;
        String areaType;

        if (event.hasSelector("Traffic rate")) {
/*          maxSamples = 2000;
            areaType =   Area.AREATYPE_POLYGON;
            style    =   Area.STYLE_HEXAGON;
*/
            maxSamples = Integer.MAX_VALUE;
            areaType =   Area.AREATYPE_LINE;
            style    =   Area.DEFAULT_STYLE;
        } else if (event.hasSelector("Traffic heatmap")) {
            maxSamples = Integer.MAX_VALUE;
            areaType =   Area.AREATYPE_HEATMAP;
            style    =   Area.STYLE_POINT;
        } else {
            // no matching selector found
            return "";
        }

        // look in replyCache at first
        String cachedResponse = replyCache.get(event.getOptionID());
        if (cachedResponse != null) {
            return this.parseResponse(cachedResponse);
        }

        // handle the other options ("Traffic rate" and "Traffic heatmap", both without life thread)
        final String RESOURCENAME = "Detail_Links_V03_DTVw_polyline.shp";
        URL url = this.getClass().getClassLoader().getResource("shapeFiles" + File.separator + RESOURCENAME);

        try {
            MapObjectSet dataset = ShapeFileReader.readVmzDtvwDataFromFile(url, maxSamples, areaType, style);
            dataset.setIDsFrom(event);
            if ("ber_traffic_rates_full".equals(event.getOptionID())) {
                dataset.adjustColors(VmzDtvwData.TRAFFICRATE, Color.GREEN, Color.RED);
                dataset.withLegend(Legend.trafficRateLegend);
            }
            if ("ber_traffic_heatmap_full".equals(event.getOptionID())) {
                dataset.withLegend(Legend.heatMapLegend);
            }

            dataset.sort(); // sorted dataset gives nicer display in UI because red lines will be on top after sorting
            LOG.info("DATASET SIZE is: " + dataset.size());
            String response = dataset.toJsonString();

            // store generated reply in cache
            replyCache.put(event.getOptionID(), response);
            return this.parseResponse(response);
        } catch (Exception e) {
            LOG.error("BER CO2 Engine failure: " + e.getMessage());
            return null;
        }
    }


    public String handleChartRequest(Event event) {
        if (event == null) {return null;}
        if (!event.isChartRequest()) {return "";}
        // else
        LOG.info("BER CO2 chart request handler: got request for chart type: {}", event.getChartRequest().getType());

        if ("test".equals(event.getChartRequest().getType())) {
            try {
                return this.handleTestChartRequest(event);
            } catch (IOException e) {
                // e.printStackTrace();
                return ""; // e.getCause().toString();
            }
        }

        // look in replyCache at first
        //String cachedResponse = chartReplyCache.get(event.getOptionID());
        //if (cachedResponse != null) {
        //    return this.parseResponse(cachedResponse);
        //}

        if ("ber_traffic_rates_full".equals(event.getOptionID()) ||
            "ber_traffic_heatmap_full".equals(event.getOptionID()))
        {
            // else load shape file, get the value list, and convert it to histogram, then to JSON
            final String RESOURCENAME = "Detail_Links_V03_DTVw_polyline.shp";
            URL url = this.getClass().getClassLoader().getResource("shapeFiles" + File.separator + RESOURCENAME);

            try {
                MapObjectSet dataSet = ShapeFileReader.readVmzDtvwDataFromFile(url, /*maxSamples=*/Integer.MAX_VALUE);
                List<Long> values = dataSet.getValueList(VmzDtvwData.TRAFFICRATE);

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


    private String handleTestChartRequest(Event event) throws IOException {

        final String fileName = "json" + File.separator + "chart.json";

        // this next line would actually parse and validate the Json, but the generated Json parser doesn't work okay
        // Charts charts = Util.loadJsonObjectFromFile(fileName, Charts.class);
        // return charts.toJsonString();

        // instead just read the file contents as a String and pass it verbatim - and unvalidated
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        String theString = IOUtils.toString(is, "UTF-8");
        IOUtils.closeQuietly(is);
        return theString;
    }

/*
    public void readExampleData() {
        readExampleData("berco2DTVwData.json");
    }

    public void readExampleData(String resourceName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            exampleData = mapper.readTree(this.getClass().getResourceAsStream(resourceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/


}
