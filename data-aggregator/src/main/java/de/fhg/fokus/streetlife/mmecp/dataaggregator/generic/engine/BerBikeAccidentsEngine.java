package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.chart.*;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.SimpleCache;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzBikeAccData;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzBikeRouteData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by csc on 01/07/15.
 */
public class BerBikeAccidentsEngine extends BaseLiveEngine implements ChartRequestHandler {

    private static final String USECASENAME = "BikeAccidents@Berlin";
    public String getUseCaseID() {return USECASENAME;}

    public BerBikeAccidentsEngine() {}

    @Override
    public String parseResponse(String response) {
        //LOG.info("BerBikeAccidents::parseResponse got message: {}", Util.shorten(response, 222));
        return response;
    }

    private static final Map<String,String> optionMap = new HashMap<String,String>() {{
        put("ber_acc_full_a1", "UHS_11_13_P");
        put("ber_acc_full_a2", "UHS_11_13_P_Kn");
        put("ber_acc_full_a3", "UHS_11_13_PS");
        put("ber_acc_full_a4", "UHS_11_13_PS_Kn");
        put("ber_acc_full_a5", "UHS_PS_Rad_Hotspot_13");
        put("ber_acc_full_a6", "UHS_PS_Rad_13");
        put("ber_acc_full_a7", "UHS_PS_Rad_13_Kn");
        put("ber_acc_full_a8", "UHS_PSS_Rad_Hotspot_13");
        put("ber_acc_full_a9", "UR_RadArt_13");
    }};

    private static SimpleCache<String> JsonCache = new SimpleCache<>();

    public String handleEvent(Event event) {
        if (event == null) {return null;}

        LOG.info("BER subType: " + event.getSubType());

        if (event.getOptionID().startsWith("ber_bike_trips")) {
            if (event.startLiveData()) {
                this.start(event, new BerBikeCrowdSourceDataRunner(this, event), /*schedule every*/900/*seconds*/);
                return new MapObjectSet(event).withLegend(Legend.tripsLegend).toJsonString();
            } else if (event.stopLiveData()) {
                this.stop(event);
                return "";
            }
        }

        if (event.getSubType().startsWith("BER_bk_rt")) {
            MapObjectSet exampleRoutes = VmzBikeRouteData.readExampleDataSet(event);
            return this.parseResponse(exampleRoutes.toJsonString());
        }

        // all other options of BER bike accidents scenario do this
        try {
            String resourceName = BerBikeAccidentsEngine.optionMap.get(event.getOptionID());
            if (resourceName == null || resourceName.isEmpty()) { return null; }
            // else
            String fullResourceName = "bikeaccdata" + File.separator + resourceName;

            // check the cache first
            String cachedResponse = JsonCache.get(fullResourceName);
            if (cachedResponse != null) {
                LOG.info("CACHE HIT A for: {}", fullResourceName);
                return cachedResponse;
            }

            // LOG.info("########## trying to open resource: " + fullResourceName + ".dbf");
            MapObjectSet accidentsDataset = VmzBikeAccData.readAccidentDataFromDbfResource(fullResourceName, event);
            accidentsDataset.setIDsFrom(event);
            accidentsDataset.setLegend((event.isSevereType())?Legend.severeAccidentsLegend:Legend.accidentsLegend);
            String json = accidentsDataset.toJsonString();
            JsonCache.put(fullResourceName, json);
            return this.parseResponse(json);

        } catch (Exception e) {
            System.out.println("BER BikeAccidents failure A: " + e.getMessage());
            e.printStackTrace(System.out);
            return null;
        }
    }


    private static SimpleCache<String> ChartJsonCache = new SimpleCache<>();

    public String handleChartRequest(Event event) {
        if (event == null) {return null;}
        if (!event.isChartRequest()) {return "";}
        try {
            String resourceName = BerBikeAccidentsEngine.optionMap.get(event.getOptionID());
            if (resourceName == null || resourceName.isEmpty()) { return null; }
            // else
            String fullResourceName = "bikeaccdata" + File.separator + resourceName;

            // check the cache first
            String cachedResponse = ChartJsonCache.get(fullResourceName);
            if (cachedResponse != null) {
                LOG.info("CACHE HIT B for: {}", fullResourceName);
                return cachedResponse;
            }

            // LOG.info("########## trying to open resource: " + fullResourceName + ".dbf");
            MapObjectSet accidentsDataset = VmzBikeAccData.readAccidentDataFromDbfResource(fullResourceName, event);

            List values = accidentsDataset.getValueList(VmzBikeAccData.ACCIDENTS);
            // ChartData cd = Histogram.makeChartDataWithFixedNumberOfBins("accidents density", values, /*number of bars=*/ 8);
            // ChartData cd = Histogram.makeChartDataWithFixedSizeOfBins("accidents density", values, /*size of bin=*/ 2L);
            ChartData cd = Histogram.makeChartForAccidentCounters("accidents density", values, event.isSevereType());

            ChartOptions cho = new ChartOptions(ChartOptions.TYPE_BARCHART, 340, 500, true, true, 1, 0.1);
            cho.setxAxis("number of accidents per spot", 8 /*pixels distance*/);
            cho.setyAxis("occurrences", 25 /*pixels distance*/);

            Chart chart = new Chart(event, "Berlin accidents occurrences", cho, cd);
            //chart.setDescription("initial BER accidents bar chart description");

            Charts charts = new Charts(chart);
            String chartJson = charts.toJsonString();
            ChartJsonCache.put(fullResourceName, chartJson);
            LOG.info("Chart JSON is:\n{}", Util.shorten(chartJson, 1111));
            return chartJson;
        } catch (Exception e) {
            System.out.println("BER BikeAccidents failure B: " + e.getMessage());
            return null;
        }
    }


}
