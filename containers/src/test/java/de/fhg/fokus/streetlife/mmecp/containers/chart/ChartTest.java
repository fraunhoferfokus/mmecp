package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by csc on 25.09.2015.
 */
public class ChartTest {

    @Test(enabled = true)
    public void testChartToJson() throws IOException {

        ChartData cd = new ChartData("chartKey");
        cd.addItem(new ChartDataItem("itemLabel1", 123456, "#ffffff"));
        cd.addItem(new ChartDataItem("itemLabel2", 234567, "#000000"));

        Chart c = new Chart(new Event(), "chartTitle",
                            new ChartOptions("discreteBarChart", 100, 200, true, false, 10, 0.01),
                            cd);

        String json = c.toJsonString();
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(Util.prettify(json), containsString("\"label\" : \"itemLabel2\""));
        assertThat(Util.prettify(json), containsString("\"value\" : 123456"));
        assertThat(Util.prettify(json), not(containsString("\"color\" : \"#00ff00\"")));
    }

}
