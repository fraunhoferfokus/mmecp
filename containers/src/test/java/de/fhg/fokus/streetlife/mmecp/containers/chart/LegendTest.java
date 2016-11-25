package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by csc on 25.09.2015.
 */
public class LegendTest {

    @Test(enabled = true)
    public void testLegendToJson() throws IOException {

        Legend leg = new Legend("theTitle");

        ChartDataItem cdi = new ChartDataItem("theLabel", 123456, "#ffffff");
        leg.addItem(cdi);
        leg.addItem(cdi);

        String json = Util.prettify(leg.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"key\" : \"theTitle\"")));
        assertThat(json, containsString(("\"label\" : \"theLabel\"")));
        assertThat(json, containsString(("\"value\" : 123456")));
        assertThat(json, containsString(("\"color\" : \"#ffffff\"")));
    }


    @Test(enabled = true)
    public void testLegendDummy() throws IOException {

        Legend leg = Legend.makeDummy();

        String json = Util.prettify(leg.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"key\" : \"a legend\"")));
        assertThat(json, containsString(("\"label\" : \"good\"")));
        assertThat(json, containsString(("\"value\" : 444")));
        assertThat(json, containsString(("\"color\" : \"#000000\"")));
        assertThat(json, containsString(("\"color\" : \"#ffffff\"")));
    }


    @Test(enabled = true)
    public void testAccidentsLegend() throws IOException {

        Legend leg = Legend.accidentsLegend;

        String json = Util.prettify(leg.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"key\" : ")));
        assertThat(json, containsString(("\"label\" : \"3-4\"")));
        assertThat(json, containsString(("\"label\" : \">10\"")));
        assertThat(json, containsString(("\"value\" : 1")));
        assertThat(json, containsString(("\"color\" : \"#ff5d00\"")));
        assertThat(json, containsString(("\"color\" : \"#ffff00\"")));
    }


    @Test(enabled = true)
    public void testTrafficRateLegend() throws IOException {

        Legend leg = Legend.trafficRateLegend;

        String json = Util.prettify(leg.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"key\" : ")));
        assertThat(json, containsString(("\"label\" : \"min\"")));
        assertThat(json, containsString(("\"label\" : \"max\"")));
        assertThat(json, containsString(("\"label\" : \"traffic\"")));
        assertThat(json, containsString(("\"value\" : 1")));
        assertThat(json, containsString(("\"color\" : \"#00ff00\"")));
        assertThat(json, containsString(("\"color\" : \"#ff0000\"")));
    }


}
