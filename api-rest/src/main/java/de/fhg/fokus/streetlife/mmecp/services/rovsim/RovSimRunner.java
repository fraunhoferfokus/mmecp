package de.fhg.fokus.streetlife.mmecp.services.rovsim;

import de.fhg.fokus.streetlife.mmecp.containers.rovsim.ParkingLot;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.RovCalcResults;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.RovSimResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by csc on 08.01.2016.
 */
public class RovSimRunner {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    //private static final ObjectMapper MAPPER = new ObjectMapper();

    public RovSimRunner() {
    }

    public RovSimResults runSimulation(List<ParkingLot> allParkingLots) {

        // TODO: do something fancy with the "allParkingLots" List's contents!!!
        // example below: average out the occupation, in relation to existing capacities

        int occupations = 0;
        int capacities = 0;

        for (ParkingLot lot : allParkingLots) {
            occupations += lot.getOccupation();
            capacities  += lot.getCapacity();
        }

        for (ParkingLot lot : allParkingLots) {
            lot.setOccupation(occupations*lot.getCapacity()/capacities);
        }

        return new RovSimResults(123.0, 456.0, allParkingLots);
    }



    private final Double demandPerRideService[] = {0.0,10.0,15.0,20.0,25.0,30.0,80.0,70.0,50.0,30.0,75.0,65.0,45.0,25.0,75.0,10.0};

    private final Double  parkingSizeFactors[] = {1.00,0.99,0.98,0.96,0.94,0.92,0.90};
    private final Integer parkingSizeBoundaries[] = {0,50,100,200,300,400,500};

    private Double getPercentage(Set<Integer> s, int min, int max) {
        for (int i=min; i<=max; i++)
            if (s.contains(i))
                return demandPerRideService[i];
        return 0.0;
    }

    private Double getParkingFactor(Integer capacity) {
        Double sizeFactor = 1.0;
        for (int j = 0; j < parkingSizeBoundaries.length; j++) {
            if (capacity >= parkingSizeBoundaries[j])
                sizeFactor = parkingSizeFactors[j];
        }
        return sizeFactor;
    }


    public RovCalcResults runCalculation(String rideServices, Integer capacity, Integer passingFlow, Integer regulation, Integer method) {

        if (rideServices == null || capacity == null || passingFlow == null) return null;

        if (method == null) method = 1;
        if (regulation == null) regulation = 1;

        // at first dissect the rideServices, e.g. "1,2,3,4" -> [1, 2, 3, 4]
        String services[] = rideServices.split(",");
        if (services == null || services.length == 0) return null;

        Set<Integer> serviceSet = new HashSet(6);
        for (String s : services) {
            serviceSet.add(Integer.parseInt(s.trim()));
        }
        LOG.info("got service SET: " + serviceSet.toString());

        // condense to highest ranked service in each category
        List<Double> percentages = new ArrayList<>(8);
        percentages.add(getPercentage(serviceSet, 1, 5));
        percentages.add(getPercentage(serviceSet, 6, 9));
        percentages.add(getPercentage(serviceSet, 10, 13));
        percentages.add(getPercentage(serviceSet, 14, 14));
        percentages.add(getPercentage(serviceSet, 15, 15));

        LOG.info("got percentage SET: " + percentages.toString());

        Double p = 0.0;
        switch (method) {
            case 0:   // return sum of percentages
                for (Double q : percentages) p += q;
                break;
            case 1:    // return sum of percentages, bound by 100%
                for (Double q : percentages) p += q;
                if (p>100.0) p = 100.0;
                break;
            case 2:    // calculate percentages with overlap, "1 - (1-x)*(1-y)*(1-z)..."
                p = 1.0;
                for (Double q : percentages) p *= (1.0-q/100.0);
                p = (1.0 - p)*100;
                break;
            case 3:    // return max of percentages
                for (Double q : percentages) if (q>p) p = q;
                break;
            default:
                return null;
        }

        Double  percentDemand = Math.round(p*100)/100.0;
        Double  maxDemand     = p*capacity/100.0 * (1.05-regulation/20.0);
        Double  newUsers      = Math.min(maxDemand, passingFlow);

        // percentage from which on a slower slope starts,
        // to reach a maximum of "getParkingFactor(capacity)*capacity" at 100% demand:
        Double cut = 0.8;
        if (newUsers > cut*capacity)
            newUsers = cut*capacity+(newUsers-cut*capacity)*(getParkingFactor(capacity)-cut)/(1.0-cut);

        return new RovCalcResults(percentDemand, (int)Math.round(maxDemand), (int)Math.round(newUsers));
    }


}
