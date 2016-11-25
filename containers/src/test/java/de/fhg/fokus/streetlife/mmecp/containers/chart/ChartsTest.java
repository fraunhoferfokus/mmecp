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
public class ChartsTest {

    @Test(enabled = true)
    public void testChartsToJson() throws IOException {

        ChartData cd = new ChartData("chartKey");
        cd.addItem(new ChartDataItem("itemLabel1", 123456, "#ffffff"));
        cd.addItem(new ChartDataItem("itemLabel2", 234567, "#000000"));

        Chart c = new Chart(new Event(), "chartTitle",
                new ChartOptions("chartType", 100, 200, true, false, 10, 0.01),
                cd);

        Charts cs = new Charts();
        cs.addChart(c);

        String json = Util.prettify(cs.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"label\" : \"itemLabel1\"")));
        assertThat(json, containsString(("\"value\" : 234567")));
        assertThat(json, not(containsString(("\"color\" : \"#123456\""))));
    }


    @Test(enabled = true)
    public void makeExampleChartsAndJson() throws IOException {

        ChartOptions opt1 = new ChartOptions("discreteBarChart", 240, 380, true, false, 0, 0.0);
        opt1.setMargin(new Margin(20,20,60,55));
        opt1.setxAxis("Car Speed", 0);
        opt1.setyAxis("Amount of Cars", 30);

        ChartData cd1 = new ChartData("Cumulative Return");
        cd1.addItem(new ChartDataItem("Free", 1532, "#A5C989"));
        cd1.addItem(new ChartDataItem("Not Free", 4321, "#D16D82"));

        Chart c1 = new Chart(new Event(), "CO2 Emission Rate", opt1, cd1);

        ChartOptions opt2 = new ChartOptions("pieChart", 100, 320, false, true, 500, 0.001);
        opt2.setMargin(new Margin(5, 35, 5, 0));
//        opt2.setxAxis("Car Speed", 0);
//        opt2.setyAxis("Amount of Cars", 30);

//        ChartData cd2 = new ChartData("--noKey--");
        ChartData cd2 = new ChartData("");
        cd2.addItem(new ChartDataItem("Free Parking Slots", 500, "#5D8896"));
        cd2.addItem(new ChartDataItem("Fee Parking Slots", 3200, "#5AC3E6"));
        cd2.addItem(new ChartDataItem("Clock Parking Slots", 1300, "#9CCBDB"));

        Chart c2 = new Chart(new Event(), "Parking Situation", opt2, cd2);

        Charts charts = new Charts();
        charts.addChart(c1);
        charts.addChart(c2);

        String json = Util.prettify(charts.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"value\" : 4321")));
        assertThat(json, containsString(("\"axisLabelDistance\" : 30")));
        assertThat(json, containsString(("\"title\" : \"Parking Situation\"")));
        assertThat(json, containsString(("\"title\" : \"CO2 Emission Rate\"")));
    }

}