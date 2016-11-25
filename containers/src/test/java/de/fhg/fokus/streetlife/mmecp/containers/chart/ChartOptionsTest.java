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
public class ChartOptionsTest {

    @Test(enabled = true)
    public void testChartDataItemToJson() throws IOException {

        ChartOptions co = new ChartOptions("chartType", 100, 200, true, true, 10, 0.01);
        co.setMargin(new Margin(11, 22, 33, 44));
        co.setxAxis("xLabel", 8);
        co.setyAxis("yLabel", 6);
        String json = Util.prettify(co.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"type\" : \"chartType\"")));
        assertThat(json, containsString(("\"labelThreshold\" : 0.01,")));
        assertThat(json, containsString(("\"axisLabelDistance\" : 6")));
    }

}
