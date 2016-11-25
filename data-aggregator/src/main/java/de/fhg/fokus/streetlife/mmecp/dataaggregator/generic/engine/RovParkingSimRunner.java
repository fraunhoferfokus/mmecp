package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.chart.Legend;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.ParkingLot;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.RovSimClient;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.RovSimResults;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzBikeAccData;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by csc on 17.11.2015.
 */
public class RovParkingSimRunner extends BaseDataRunner {

    private Legend legend = Legend.heatMapLegend;

//    @Inject
//    @New
//    Instance<RovSimClient> rovSimClientInstance;

    private RovSimClient rovSimClient;

    public RovParkingSimRunner(LiveResponseParseEngine parent, Event event) {
        super(parent, event);
//        rovSimClient = rovSimClientInstance.get();
        rovSimClient = new RovSimClient();
    }

    public void run() {
        int[] foo = new int[] {1,1,1,1,1,1,1,1,1,1};

        try {
            String response = "";

            LOG.info("Running RovParkingSimRunner in order to get live data...");

            // testing handling of RovSim

            ParkingLot lot1 = new ParkingLot("LOT_1", "address #1", new GpsCoordinate(52.52, 13.13), 100);
            ParkingLot lot2 = new ParkingLot("LOT_2", "address #2", new GpsCoordinate(61.61, 21.21), 200);
            ParkingLot lot3 = new ParkingLot("LOT_3", "address #3", new GpsCoordinate(62.62, 22.22), 300);

            lot1.setOccupation(0);
            lot2.setOccupation(100);
            lot3.setOccupation(200);

            List<ParkingLot> oldLots = new LinkedList<>();
            // allLots.add(lot1);
            oldLots.add(lot2);
            oldLots.add(lot3);

            try {
                LOG.info("rovSimClient IS: {}", rovSimClient);
                RovSimResults simResults  = rovSimClient.runSimulation(oldLots, lot1);
                if (simResults != null) {
                    LOG.info("SimResults are: {}", simResults.toJsonString());
                }
            } catch (IOException e) {
                LOG.warn("Rov runSimulation failed: " + e.getMessage());
            }

            // end of Rov sim test run section

            int i=0;
            for (int num : foo) {
                if (isCanceled()) {
                    LOG.info("CANCELLATION of RovParkingSimRunner detected - EXITING now.");
                    return;
                } // terminate thread if running has been set to false (by parent)
                i++;

                LOG.info("Calling example star maker " + i + fun(i) +" time (and sleeping 2sec each time)... ");
                MapObjectSet dataset = VmzBikeAccData.makeExampleList(1);
                dataset.setIDsFrom(event);
                callback(dataset.toJsonString());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    LOG.info("INTERRUPTION of RovParkingSimRunner SLEEP detected - EXITING now.");
                    return;
                }
            }
        } catch (IOException | SessionManagerException e) {
            LOG.info("Could not send back results for RovParkingSimRunner life data:\n{}", e);
        }
        this.firstTime = false;
    }


    public void callback(MapObjectSet dataset) throws IOException, SessionManagerException {
        if (dataset != null && dataset.size() > 0) {
            dataset.setIDsFrom(this.event);
            dataset.setLegend(this.legend);
            String response = dataset.toJsonString();
            LOG.info("BER CO2 VMZ Life data runner calling toPanelEndpoint callback with response:\n{}", Util.shorten(response, 1111));
            callback(response);
        }
    }


    public static String fun(int i) {
        switch (i) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }


}
