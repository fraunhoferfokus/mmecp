package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;

/**
 * Created by csc on 25.09.2015.
 */
public class Legend extends ChartData {

    public Legend() {
        super("Legend");
    }

    public Legend(String title) {
        super(title);
    }

    public static Legend makeDummy() {
        return new Legend("a legend") {{
            addItem(new ChartDataItem("bad", 111, "#000000"));
            addItem(new ChartDataItem("mediocre", 222, "#404040"));
            addItem(new ChartDataItem("good", 333, "#a0a0a0"));
            addItem(new ChartDataItem("best", 444, "#ffffff"));
        }};
    }


    public static final Legend tripsLegend = makeLegendForUserTrips();

    public static Legend makeLegendForUserTrips() {
        return new Legend("used transport") {{
            addItem(new ChartDataItem("user tracked path", 444, Color.simpleTrafficColorMapTracked.get("Other")));
            // addItem(new ChartDataItem("detailed bike", 333, Color.simpleTrafficColorMapTracked.get("Bike")));
            addItem(new ChartDataItem("other transport, planned", 222, Color.simpleTrafficColorMap.get("Other")));
            addItem(new ChartDataItem("bike trip, planned", 111, Color.simpleTrafficColorMap.get("Bike")));
        }};
    }


    public static final Legend accidentsLegend = makeLegendForAccidentCounters(Color.FOKUS_COLOR_SCHEME);
    public static final Legend severeAccidentsLegend = makeLegendForAccidentCounters(Color.FOKUS_COLOR_SCHEME_SEVERE);
    private static Legend makeLegendForAccidentCounters(int colorScheme) {

        int[] hist = new int[] {1,1,1,1,1,1};

        Legend leg = new Legend("# of accidents");
        for (int i=0; i<hist.length; i++) {
            String label = (i<5) ? ((2*i+1) + "-" + (2*i+2)) : ">10";
            Color color = new Color().colorize(2*i+1, colorScheme);
            leg.addItem(new ChartDataItem(label, hist[i], color.toString()));
        }
        return leg;
    }


    public static final Legend trafficRateLegend = makeLegendForTraficRate();
    private static Legend makeLegendForTraficRate() {

        int[] hist = new int[] {1,1,1,1,1,1,1,1,1,1};
        String[] label = new String[] {"traffic", "min", "", "", "", "", "", "", "traffic", "max"};

        Legend leg = new Legend("Traffic rate");
        for (int i=0; i<hist.length; i++) {
            leg.addItem(new ChartDataItem(label[i], hist[i],
                    Color.mixColor(Color.GREEN, Color.RED,
                            (float) i / (float)(hist.length - 1)).toString()));
        }
        return leg;
    }


    public static final Legend heatMapLegend = makeLegendForHeatMap();
    private static Legend makeLegendForHeatMap() {

        int[] hist = new int[] {1,1,1,1,1};
        String[] label = new String[] {"min", "low", "moderate", "high", "max"};
        String[] colors = new String[] {"#dddddd", "#00ffff", "#00ff00", "#ffff00", "#ff0000"};

        Legend leg = new Legend("Traffic density");
        for (int i=0; i<hist.length; i++) {
            leg.addItem(new ChartDataItem(label[i], hist[i], colors[i]));
        }
        return leg;
    }

    public String toString() {return Util.toJsonString(this);}

}
