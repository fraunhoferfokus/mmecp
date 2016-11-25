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
public class ChartDataTest {

    @Test(enabled = true)
    public void testChartDataToJson() throws IOException {

        ChartData cd = new ChartData("chartKey");

        ChartDataItem cdi = new ChartDataItem("theLabel", 123456, "#ffffff");
        cd.addItem(cdi);
        cd.addItem(cdi);

        String json = Util.prettify(cd.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"label\" : \"theLabel\"")));
        assertThat(json, containsString(("\"value\" : 123456")));
        assertThat(json, containsString(("\"color\" : \"#ffffff\"")));
    }

}
