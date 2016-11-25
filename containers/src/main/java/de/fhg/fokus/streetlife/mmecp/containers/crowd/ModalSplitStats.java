package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import java.util.*;

/**
 * Created by csc on 08.04.2016.
 */
public class ModalSplitStats {

    private Map<String,Integer> modeCounters;

    public ModalSplitStats() {
        modeCounters = new HashMap<>();
    }


    public void count(List<SegmentData> trip) {
        Set<String> transports = SegmentData.getTransportsSet(trip);
        for (String transport : transports) {
            count(transport);
        }
    }


    public void count(String mode) {
        count(mode, 1);
    }


    public void count(String mode, Integer count) {
        this.modeCounters.put(mode, this.modeCounters.getOrDefault(mode, 0) + 1);
    }


    public String toPercentagesString() {
        Integer sum = 0;
        for (Integer counter : this.modeCounters.values()) {
            sum = sum + counter;
        }
        if (sum<1) return "{}";

        Map<String,Double> percentages = new HashMap<>();
        for (String mode : this.modeCounters.keySet()) {
            percentages.put(mode, Math.floor(10000.0*this.modeCounters.get(mode)/sum)/100.0);
        }
        return percentages.toString();
    }


    public String toString() {
        return modeCounters.toString();
    }

}
